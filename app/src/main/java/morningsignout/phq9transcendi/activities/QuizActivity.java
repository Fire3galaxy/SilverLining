package morningsignout.phq9transcendi.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import morningsignout.phq9transcendi.R;

public class QuizActivity extends AppCompatActivity
        implements ImageButton.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String LOG_NAME = "QuizActivity";
    private static final int RED_FLAG_QUESTION = 17;
    private static final int NUM_QUESTIONS = 21;
    private static final int UPDATE_GOOGLE = 1;

    // Use String.format() with this to display current question
    private final String numberString = "%1$d/" + String.valueOf(NUM_QUESTIONS);

    private TextView question, questionNumText; //The text of the question
    private AnswerSeekBar answerBar;
    private Button answerNo, answerYes;
    private ImageButton next, prev;
    private LinearLayout containerButtons, containerBarText;

    private String[] questions;
    private String[] answersNormal;

    private String startTimestamp, endTimestamp;
    private double latitude = 0, longitude = 0;
    private Scores scores;  // Used for answering questions
    private boolean quizDone; //If all questions are answered
    private int questionNumber; //which question the user is on
    private AlertDialog.Builder dialogBuilder;
    private GoogleApiClient mGoogleApiClient;
    private ReentrantLock gpsLock = new ReentrantLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //Grab and set content; inital setup
        question = (TextView) findViewById(R.id.questionView);
        questionNumText = (TextView) findViewById(R.id.textView_question_number);
        answerBar = (AnswerSeekBar) findViewById(R.id.seekBar_quiz_answer);
        answerNo = (Button) findViewById(R.id.button_answer_no);
        answerYes = (Button) findViewById(R.id.button_answer_yes);
        next = (ImageButton) findViewById(R.id.imageButton_nextq);
        prev = (ImageButton) findViewById(R.id.imageButton_prevq);
        containerButtons = (LinearLayout) findViewById(R.id.container_buttons);
        containerBarText = (LinearLayout) findViewById(R.id.container_bar_text);

        questions = getResources().getStringArray(R.array.questions);
        answersNormal = getResources().getStringArray(R.array.answers_normal);

        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.app_name)
                .setMessage(R.string.dialog_quit_questionnaire)
                .setPositiveButton(R.string.dialog_return_home, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent backToMenu = new Intent(QuizActivity.this, IndexActivity.class);
                        backToMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(backToMenu);
                    }
                }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        reset();

        // Set all buttons to onClickListener function here
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        answerNo.setOnClickListener(this);
        answerYes.setOnClickListener(this);

        //Everything is set up, start quiz
        startQuiz();
        startTimestamp = getTimestamp();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        dialogBuilder.create().show();
    }

    private void reset() {
        scores = new Scores();
        quizDone = false;
        questionNumber = 1;
        answerBar.setProgress(0);
        containerButtons.setVisibility(View.GONE);
        containerBarText.setVisibility(View.VISIBLE);
        questionNumText.setText(String.format(numberString, 1));

        for (int i = 0; i < containerBarText.getChildCount(); i++)
            ((TextView) containerBarText.getChildAt(i)).setText(answersNormal[i]);
    }

    private void startQuiz() {
        if (!quizDone) {
            updateQuestions();
            questionNumber++;
            if (questionNumber > NUM_QUESTIONS)
                quizDone = true;
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        endTimestamp = getTimestamp();
        uploadToDatabase();

        Intent results = new Intent(this, ResultsActivity.class);
        results.putExtra(ResultsActivity.SCORE, scores.getFinalScore());
        results.putExtra(ResultsActivity.RED_FLAG, scores.containsRedFlag());
        startActivity(results);
        finish();
    }

    private void updateQuestions() {
        question.setText(questions[questionNumber - 1]);    // Question text
        questionNumText.setText(String.format(numberString, questionNumber));   // Question #

        if (questionNumber < RED_FLAG_QUESTION)
            putSeekBar();
        else if (questionNumber >= RED_FLAG_QUESTION)
            putButtons();

        if (scores.questionIsVisited(questionNumber - 1))
            answerBar.setAnswer(scores.getQuestionScore(questionNumber - 1));   // Previously saved answer

        // Hide previous button on first question
        if (questionNumber == 1)
            prev.setVisibility(View.INVISIBLE);
        else if (prev.getVisibility() != View.VISIBLE)
            prev.setVisibility(View.VISIBLE);

        // Hide next button on red flag questions unless already answered
        if (questionNumber >= RED_FLAG_QUESTION
                && (questionNumber == NUM_QUESTIONS || !scores.questionIsVisited(questionNumber - 1)))
            next.setVisibility(View.INVISIBLE);
        else if (next.getVisibility() != View.VISIBLE)
            next.setVisibility(View.VISIBLE);
    }

    private void putSeekBar() {
        if (answerBar.getVisibility() != View.VISIBLE) {
            answerBar.setProgress(0);
            answerBar.setVisibility(View.VISIBLE);
            containerBarText.setVisibility(View.VISIBLE);
            containerButtons.setVisibility(View.GONE);
        }
    }

    private void putButtons() {
        if (answerBar.getVisibility() != View.GONE) {
            answerBar.setVisibility(View.GONE);
            containerBarText.setVisibility(View.GONE);
            containerButtons.setVisibility(View.VISIBLE);
        }
    }

    private void addScore(int value) {
        // Question - 2 because the number is set to the next question and starts at base 1
        scores.putScore(questionNumber - 2, value);
    }

    // Uploads score data to Firebase. If no user ID exists, creates and stores one
    private void uploadToDatabase() {
        Firebase firebaseRef = new Firebase(FirebaseExtras.DATA_URL);
        String userID = getSharedPreferences(IndexActivity.PREFS_NAME, MODE_PRIVATE)
                .getString(FirebaseExtras.USER_ID, null);

        if (userID != null)
            scores.uploadDataToDatabase(firebaseRef, userID, startTimestamp, endTimestamp,
                    latitude, longitude);

        Log.d("QuizActivity", "Finished writing data");
    }

    // Which view was clicked: arrows (next/prev) or buttons (yes/no)
    @Override
    public void onClick(View v) {
        if (v.equals(next)) {
            if (questionNumber - 1 < RED_FLAG_QUESTION)
                addScore(answerBar.getAnswer());

            startQuiz();
        } else if (v.equals(prev)) {
            // Question - 2 because the number is set to the next question, not the current question
            questionNumber = Math.max(1, questionNumber - 2);

            // reset quizDone flag if not complete
            if (questionNumber < NUM_QUESTIONS)
                quizDone = false;

            startQuiz();
        } else if (v.equals(answerNo)) {
            addScore(0);
            startQuiz();
        } else if (v.equals(answerYes)) {
            addScore(1);
            startQuiz();
        }
    }

    String getTimestamp() {
        String timestamp = "";
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());

        String month = String.valueOf(date.get(Calendar.MONTH) + 1);
        String day = String.valueOf(date.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(date.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(date.get(Calendar.MINUTE));

        if (date.get(Calendar.MONTH) + 1 < 10) month = "0" + month;
        if (date.get(Calendar.DAY_OF_MONTH) < 10) day = "0" + day;
        if (date.get(Calendar.HOUR_OF_DAY) < 10) hour = "0" + hour;
        if (date.get(Calendar.MINUTE) < 10) minute = "0" + minute;

        timestamp += String.valueOf(date.get(Calendar.YEAR)) + "-";
        timestamp += month + "-";
        timestamp += day + " 'at' ";
        timestamp += hour + ":";
        timestamp += minute;

        return timestamp;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // FIXME: Same thing with onConnectionFailed. Should we ask for location permission?
            Log.e("QuizActivity", "Not given permission to access location");

            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            // Race condition between callback and access in uploadToDatabase()
            gpsLock.lock();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("QuizActivity", latitude + " " + longitude);
            gpsLock.unlock();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("QuizActivity", "Failed to connect to google play services");

        /* FIXME: If we can't connect to google, should we force the user to update? It kinda
         * says "Hey, we're sending data about you!" */
//        int error = connectionResult.getErrorCode();
//        if (error == ConnectionResult.SERVICE_MISSING
//                || error == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED
//                || error == ConnectionResult.SERVICE_DISABLED) {
//            try {
//                connectionResult.startResolutionForResult(this, UPDATE_GOOGLE);
//            } catch (IntentSender.SendIntentException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
