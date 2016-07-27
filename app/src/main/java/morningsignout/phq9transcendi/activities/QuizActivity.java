package morningsignout.phq9transcendi.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import android.widget.RelativeLayout;
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
    private static final int RED_FLAG_QUESTION = 16;    // zero-based number
    private static final int NUM_QUESTIONS = 21;        // total number of questions
    private static final String SAVE_TIMESTAMP = "Timestamp", SAVE_QUESTION_NUM = "Question Number";

    // Use String.format() with this to display current question
    private final String numberString = "%1$d/" + String.valueOf(NUM_QUESTIONS);

    private BlinkScrollView questionContainer;
    private TextView questionTextView, questionNumText; //The text of the question
    private AnswerSeekBar answerBar;
    private Button answerNo, answerYes;
    private ImageButton nextArrow, prevArrow;
    private LinearLayout containerButtons, containerBarText;

    private String[] questionArray;

    private String startTimestamp, endTimestamp;
    private double latitude = 0, longitude = 0;
    private Scores scores;                          // Used for keeping track of score
    private int questionNumber;                     // Which question the user is on (zero-based)
    private AlertDialog.Builder dialogBuilder;      // To confirm user wants to quit
    private GoogleApiClient mGoogleApiClient;
    private ReentrantLock gpsLock = new ReentrantLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (mGoogleApiClient == null) { // Create an instance of GoogleAPIClient.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //Grab and set content; inital setup
        SharedPreferences preferences = getPreferences(0);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            questionContainer = (BlinkScrollView) findViewById(R.id.question_container);
        questionTextView = (TextView) findViewById(R.id.questionView);
        questionNumText = (TextView) findViewById(R.id.textView_question_number);
        answerBar = (AnswerSeekBar) findViewById(R.id.seekBar_quiz_answer);
        answerNo = (Button) findViewById(R.id.button_answer_no);
        answerYes = (Button) findViewById(R.id.button_answer_yes);
        nextArrow = (ImageButton) findViewById(R.id.imageButton_nextq);
        prevArrow = (ImageButton) findViewById(R.id.imageButton_prevq);
        containerButtons = (LinearLayout) findViewById(R.id.container_buttons);
        containerBarText = (LinearLayout) findViewById(R.id.container_bar_text);

        // Blink scrollbar in scrollview for long questions (Only exists in landscape, do only once)
        if (questionContainer != null && !preferences.contains("blink")) {
            questionTextView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (questionTextView.getHeight() > questionContainer.getHeight() && questionContainer.getHeight() > 0) {
                        questionContainer.blinkScrollBar();

                        SharedPreferences.Editor editor = getPreferences(0).edit();
                        editor.putString("blink", "true");
                        editor.apply();
                        questionTextView.removeOnLayoutChangeListener(this);
                    }
                }
            });
        }

        questionArray = getResources().getStringArray(R.array.questions);

        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.app_name)
                .setMessage(R.string.dialog_quit_questionnaire)
                .setPositiveButton(R.string.dialog_return_home, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearPreferences();

                        Intent backToMenu = new Intent(QuizActivity.this, IndexActivity.class);
                        startActivity(backToMenu);
                        finish();
                    }
                }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        reset();

//        //Reinitialize state variables
//        if (preferences.contains(SAVE_TIMESTAMP)) {
//            startTimestamp = preferences.getString(SAVE_TIMESTAMP, getTimestamp());
//            Log.d(LOG_NAME, String.valueOf(preferences.getInt(SAVE_QUESTION_NUM, 1)));
//        } else {
            startTimestamp = getTimestamp();
//            // reset();
//        }

        // Set all buttons to onClickListener function here
        nextArrow.setOnClickListener(this);
        prevArrow.setOnClickListener(this);
        answerNo.setOnClickListener(this);
        answerYes.setOnClickListener(this);

        //Everything is set up, start quiz
        handleQuiz(true);

//        Log.d(LOG_NAME, startTimestamp);
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
    protected void onPause() {
        super.onPause();

        // Save answers and state variables
        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putString(SAVE_TIMESTAMP, startTimestamp);
        editor.putInt(SAVE_QUESTION_NUM, questionNumber);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        dialogBuilder.create().show();
    }

    private void reset() {
        clearPreferences();

        scores = new Scores();
        questionNumber = -1;
        answerBar.setProgress(0);
        containerButtons.setVisibility(View.GONE);
        containerBarText.setVisibility(View.VISIBLE);
        questionNumText.setText(String.format(numberString, 1));
    }

    // Calls all functions to change question. NextQuestion determines if questionNumber increases/decreases.
    private void handleQuiz(boolean nextQuestion) {
        if (nextQuestion) {
            questionNumber++;

            if (questionNumber == NUM_QUESTIONS)
                finishQuiz();
            else
                updateQuestions();
        } else {
            questionNumber--;
            updateQuestions();
        }
    }

    private void finishQuiz() {
        endTimestamp = getTimestamp();
        uploadToDatabase();

        clearPreferences();

        Intent results = new Intent(this, ResultsActivity.class);
        results.putExtra(ResultsActivity.SCORE, scores.getFinalScore());
        results.putExtra(ResultsActivity.RED_FLAG, scores.containsRedFlag());
        startActivity(results);
        finish();
    }

    private void updateQuestions() {
        questionTextView.setText(questionArray[questionNumber]);                    // Question text
        questionNumText.setText(String.format(numberString, questionNumber + 1));   // Question #

        if (questionNumber < RED_FLAG_QUESTION)         // Normal questions (Use bar)
            putSeekBar();
        else if (questionNumber >= RED_FLAG_QUESTION)   // Red flag questions (Use buttons)
            putButtons();

        // Show previously saved answer if previous button is clicked
        if (scores.questionIsVisited(questionNumber))
            answerBar.setAnswer(scores.getQuestionScore(questionNumber));

        // Hide previous button on first question
        if (questionNumber == 0)
            prevArrow.setVisibility(View.INVISIBLE);
        else if (prevArrow.getVisibility() != View.VISIBLE)
            prevArrow.setVisibility(View.VISIBLE);

        // Hide nextArrow button on red flag questions unless already answered
        if (questionNumber >= RED_FLAG_QUESTION
                && (questionNumber + 1 == NUM_QUESTIONS || !scores.questionIsVisited(questionNumber)))
            nextArrow.setVisibility(View.INVISIBLE);
        else if (nextArrow.getVisibility() != View.VISIBLE)
            nextArrow.setVisibility(View.VISIBLE);
    }

    private void putSeekBar() {
        if (answerBar.getVisibility() != View.VISIBLE) {
            answerBar.setProgress(0);
            answerBar.setVisibility(View.VISIBLE);
            containerBarText.setVisibility(View.VISIBLE);
            containerButtons.setVisibility(View.GONE);

            if (questionContainer != null) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) questionContainer.getLayoutParams();
                params.addRule(RelativeLayout.ABOVE, R.id.seekBar_quiz_answer);
            }
        }
    }

    private void putButtons() {
        if (answerBar.getVisibility() != View.INVISIBLE) {
            answerBar.setVisibility(View.INVISIBLE);
            containerBarText.setVisibility(View.INVISIBLE);
            containerButtons.setVisibility(View.VISIBLE);

            if (questionContainer != null) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) questionContainer.getLayoutParams();
                params.addRule(RelativeLayout.ABOVE, R.id.container_buttons);
            }
        }
    }

    private void addScore(int questionNumber, int value) {
        scores.putScore(questionNumber, value);
    }

    // Uploads score data to Firebase. If no user ID exists, creates and stores one
    private void uploadToDatabase() {
        Firebase firebaseRef = new Firebase(FirebaseExtras.DATA_URL);
        String userID = getSharedPreferences(IndexActivity.PREFS_NAME, MODE_PRIVATE)
                .getString(FirebaseExtras.USER_ID, null);

        if (userID != null) {
            gpsLock.lock();
            scores.uploadDataToDatabase(firebaseRef, userID, startTimestamp, endTimestamp,
                    latitude, longitude);
            gpsLock.unlock();
        }

        Log.d("QuizActivity", "Finished writing data");
    }

    // Which view was clicked: arrows (nextArrow/prevArrow) or buttons (yes/no)
    @Override
    public void onClick(View v) {
        if (v.equals(nextArrow)) {
            if (questionNumber < RED_FLAG_QUESTION)
                addScore(questionNumber, answerBar.getAnswer());

            handleQuiz(true);
        } else if (v.equals(prevArrow)) {
            handleQuiz(false);
        } else if (v.equals(answerNo)) {
            addScore(questionNumber, 0);
            handleQuiz(true);
        } else if (v.equals(answerYes)) {
            addScore(questionNumber, 1);
            handleQuiz(true);
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

    void clearPreferences() {
        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.remove(SAVE_TIMESTAMP);
        editor.remove(SAVE_QUESTION_NUM);
        editor.apply();
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
