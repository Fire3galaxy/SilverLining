package morningsignout.phq9transcendi.activities.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.activities.HelperClasses.FirebaseExtras;
import morningsignout.phq9transcendi.activities.HelperClasses.QuestionData;
import morningsignout.phq9transcendi.activities.HelperClasses.Scores;
import morningsignout.phq9transcendi.activities.HelperClasses.Utils;
import morningsignout.phq9transcendi.activities.RangeSliderCustom.RangeSliderView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/* What to update when question is added: If new answer type is added, add string array to
 * QuestionData allAnswers in constructor
 */
public class QuizActivity extends AppCompatActivity
        implements ImageButton.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String LOG_NAME = "QuizActivity";
    private static final String SAVE_TIMESTAMP = "Timestamp", SAVE_QUESTION_NUM = "Question Number",
        SAVE_SCORES_A = "Score Values", SAVE_SCORES_B = "Visit values";

    // Use String.format() with this to display current question
    private final String numberString = "%1$d/" + String.valueOf(QuestionData.NUM_QUESTIONS);

    private ScrollView questionContainer;
    private TextView questionTextView, questionNumText; //The text of the question
    private Button answerNo, answerYes;
    private ImageButton nextArrow, prevArrow;
    private LinearLayout containerButtons, containerBarText;
    RangeSliderView answerSliderView;

    private String[] questionArray;
    QuestionData allAnswers;
    int currentSeekbarChoice;
    int currentButtonChoice;
    private String startTimestamp, endTimestamp;
    private double latitude = 0, longitude = 0;
    private Scores scores;                          // Used for keeping track of score
    private int questionNumber;                     // Which question the user is on (zero-based)
    private boolean aboveButtonsFlag;               // Landscape: change height of question view
    private boolean aboveSeekbarFlag;               // Landscape: change height of question view
    private boolean isFinishingFlag;                // Used in onPause() to save/not save
    private boolean isFirstTimeFlag;                // Used in onCreate() and onStart() for continue dialog
                                                    // Note: Currently not using "continue" feature except for saving state
    private AlertDialog.Builder dialogBuilder;      // To confirm user wants to quit
    private GoogleApiClient mGoogleApiClient;
    private ReentrantLock gpsLock = new ReentrantLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d("QuizActivity", "In onCreate");

        int theme = Utils.GetTheme(this);
        Utils.onActivityCreateSetTheme(this, theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        if (mGoogleApiClient == null) { // Create an instance of GoogleAPIClient.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Resources res = getResources();
        isFirstTimeFlag = (savedInstanceState == null);

        // Grab and set content; inital setup (Use restoreInstanceState() for "continue where you last left off" code)
//        SharedPreferences preferences = getPreferences(0);
        questionTextView = (TextView) findViewById(R.id.questionView);
        questionNumText = (TextView) findViewById(R.id.textView_question_number);
        answerNo = (Button) findViewById(R.id.button_answer_no);
        answerYes = (Button) findViewById(R.id.button_answer_yes);
        nextArrow = (ImageButton) findViewById(R.id.imageButton_nextq);
        prevArrow = (ImageButton) findViewById(R.id.imageButton_prevq);
        containerButtons = (LinearLayout) findViewById(R.id.container_buttons);
        containerBarText = (LinearLayout) findViewById(R.id.container_bar_text);
        answerSliderView = (RangeSliderView) findViewById(R.id.range_slider);

        //change color according to theme
        Drawable arrows = ContextCompat.getDrawable(getApplicationContext(), R.drawable.green_arrow);
        ImageView whiteLine = (ImageView) findViewById(R.id.imageView_white_line);
        if(Utils.GetTheme(this)== 0){
            arrows.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.jungle_mist), PorterDuff.Mode.SRC_ATOP);
            whiteLine.setBackgroundColor(getResources().getColor(R.color.jungle_mist));
        }
        else{
            arrows.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.wafer), PorterDuff.Mode.SRC_ATOP);
            whiteLine.setBackgroundColor(getResources().getColor(R.color.wafer));
        }
        ((ImageButton)findViewById(R.id.imageButton_nextq)).setImageDrawable(arrows);
        ((ImageButton)findViewById(R.id.imageButton_prevq)).setImageDrawable(arrows);

        // Auto-scroll up from bottom of scroll view (Landscape only)
        if (res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            questionContainer = (ScrollView) findViewById(R.id.question_container);
            questionTextView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (v.getHeight() > questionContainer.getHeight() && questionContainer.getHeight() > 0) {
                        questionContainer.setScrollY(questionContainer.getMaxScrollAmount());
                        questionContainer.fullScroll(View.FOCUS_UP);
                    }
                }
            });
        }
        // Text size changes when body of text is too big (Portrait only)
        else {
            questionTextView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (questionTextView.getLineCount() < 3) {
                        questionTextView.setTextSize(24);
                    } else if (bottom - top > 0) {
                        Rect bounds = new Rect();
                        questionTextView.getLineBounds(1, bounds);
                        float lineHeight = 24 * getResources().getDisplayMetrics().scaledDensity;
                        float spacingHeight = bounds.height() - lineHeight;
                        float textHeight = (lineHeight + spacingHeight)
                                * questionTextView.getLineCount();
                        float neededTextHeight = questionTextView.getHeight()
                                - questionTextView.getPaddingTop()
                                - questionTextView.getPaddingBottom();

                        if (textHeight <= neededTextHeight) {
                            if (questionTextView.getTextSize() != lineHeight) {
                                questionTextView.setTextSize(24);
                            }
                        } else {
                            float neededLineHeight = (neededTextHeight
                                    / questionTextView.getLineCount()) - spacingHeight;
                            questionTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, neededLineHeight);
                        }
                    }
                }
            });
        }

        aboveButtonsFlag = false;
        aboveSeekbarFlag = false;
        isFinishingFlag = false;
        questionArray = res.getStringArray(R.array.questions);
        allAnswers = new QuestionData();
        allAnswers.answerChoices[QuestionData.NORMAL] = res.getStringArray(R.array.answers_normal);
        allAnswers.answerChoices[QuestionData.FLAG] = res.getStringArray(R.array.answers_flag);
        allAnswers.answerChoices[QuestionData.DEPRESSION] = res.getStringArray(R.array.answers_depression);
        allAnswers.answerChoices[QuestionData.SITUATION] = res.getStringArray(R.array.answers_situation);
        allAnswers.answerChoices[QuestionData.APPOINTMENT] = res.getStringArray(R.array.answers_appointment);
        allAnswers.answerChoices[QuestionData.YES_NO] = res.getStringArray(R.array.answers_yes_no);
        currentSeekbarChoice = QuestionData.NORMAL;
        currentButtonChoice = QuestionData.YES_NO;
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(R.string.dialog_quit_quiz)
                .setPositiveButton(R.string.dialog_return_home, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent backToMenu = new Intent(QuizActivity.this, IndexActivity.class);
                        startActivity(backToMenu);
                        finish();
                    }
                }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


        startTimestamp = getTimestamp();
        questionNumber = -1;
        scores = new Scores();
        answerSliderView.setIndex(0);

        handleQuiz(true);   // Everything is set up, start quiz

        // Set all buttons to onClickListener function here
        nextArrow.setOnClickListener(this);
        prevArrow.setOnClickListener(this);
        answerNo.setOnClickListener(this);
        answerYes.setOnClickListener(this);
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
        if (!isFinishingFlag) {
            Pair<String, String> scoreState = scores.getScoreStateStrings();

            SharedPreferences.Editor editor = getPreferences(0).edit();
            editor.putString(SAVE_TIMESTAMP, startTimestamp);
            editor.putInt(SAVE_QUESTION_NUM, questionNumber);
            editor.putString(SAVE_SCORES_A, scoreState.first);
            editor.putString(SAVE_SCORES_B, scoreState.second);
            editor.apply();
//            Log.d(LOG_NAME, "Saving!");
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("QuizActivity", "restoring state");

        // Reinitialize state variables
        SharedPreferences preferences = getPreferences(0);
        if (savedInstanceState != null && preferences.contains(SAVE_TIMESTAMP)) {
            startTimestamp = preferences.getString(SAVE_TIMESTAMP, getTimestamp());
            int questionNumber = preferences.getInt(SAVE_QUESTION_NUM, 0);
            String scoresA = preferences.getString(SAVE_SCORES_A, null);
            String scoresB = preferences.getString(SAVE_SCORES_B, null);
            scores = new Scores(scoresA, scoresB);

//            Log.d(LOG_NAME, String.valueOf(startTimestamp));
//            Log.d(LOG_NAME, String.valueOf(questionNumber));
//            Log.d(LOG_NAME, String.valueOf(scoresA));
//            Log.d(LOG_NAME, String.valueOf(scoresB));
//            scores.getFinalScore();

            setQuestion(questionNumber);    // Everything is set up, start quiz
        }
    }

    @Override
    public void onBackPressed() {
        dialogBuilder.create().show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));   // For custom Rubik font
    }

    // Calls all functions to change question. NextQuestion determines if questionNumber increases/decreases.
    private void handleQuiz(boolean nextQuestion) {
        if (nextQuestion) {
            questionNumber++;

            if (questionNumber == QuestionData.NUM_QUESTIONS)
                finishQuiz();
            else
                updateQuestions();
        } else {
            questionNumber--;
            updateQuestions();
        }
    }

    private void setQuestion(int questionNumber) {
        if (questionNumber >= 0 && questionNumber < QuestionData.NUM_QUESTIONS) {
            this.questionNumber = questionNumber;
            updateQuestions();
        }
    }

    private void finishQuiz() {
        endTimestamp = getTimestamp();
//        uploadToDatabase();

        clearPreferences();
        isFinishingFlag = true;

        Intent results = new Intent(this, ResultsActivity.class);
        results.putExtra(ResultsActivity.SCORE, scores.getFinalScore());
        results.putExtra(ResultsActivity.RED_FLAG, scores.containsRedFlag());
        startActivity(results);
        finish();
    }

    private void updateQuestions() {
        questionTextView.setText(questionArray[questionNumber]);                    // Question text
        questionNumText.setText(String.format(numberString, questionNumber + 1));   // Question #

        // Seekbar is for 3+ answers, Buttons for 2 answers
        if (QuestionData.USES_SLIDER[questionNumber])
            putSeekBar();
        else
            putButtons();

        // Possible string array options for answers are listed in QuestionData
        changeAnswerText(QuestionData.ANSW_CHOICE[questionNumber]);

        // Show previously saved answer if previous button is clicked
        if (scores.questionIsVisited(questionNumber))
            answerSliderView.setIndex(scores.getQuestionScore(questionNumber));

        // Hide previous button on first question
        if (questionNumber == 0)
            prevArrow.setVisibility(View.INVISIBLE);
        else if (prevArrow.getVisibility() != View.VISIBLE)
            prevArrow.setVisibility(View.VISIBLE);

        // Hide nextArrow button on red flag questions unless already answered or is interference
//        if (questionNumber >= QuestionData.RED_FLAG_QUESTION && questionNumber != QuestionData.INTERFERENCE_QUESTION
        if (!QuestionData.USES_SLIDER[questionNumber]
                && (questionNumber + 1 == QuestionData.NUM_QUESTIONS || !scores.questionIsVisited(questionNumber)))
            nextArrow.setVisibility(View.INVISIBLE);
        else
            nextArrow.setVisibility(View.VISIBLE);
    }

    private void putSeekBar() {
        if (answerSliderView.getVisibility() != View.VISIBLE)
            answerSliderView.setVisibility(View.VISIBLE);
        if (containerBarText.getVisibility() != View.VISIBLE)
            containerBarText.setVisibility(View.VISIBLE);
        if (containerButtons.getVisibility() != View.GONE)
            containerButtons.setVisibility(View.GONE);

        // Landscape only: above seekbar
        if (questionContainer != null && !aboveSeekbarFlag) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) questionContainer.getLayoutParams();
            params.addRule(RelativeLayout.ABOVE, R.id.range_slider);

            aboveSeekbarFlag = true;
            aboveButtonsFlag = false;
        }
    }

    private void putButtons() {
        if (answerSliderView.getVisibility() != View.INVISIBLE)
            answerSliderView.setVisibility(View.INVISIBLE);
        if (containerBarText.getVisibility() != View.INVISIBLE)
            containerBarText.setVisibility(View.INVISIBLE);
        if (containerButtons.getVisibility() != View.VISIBLE)
            containerButtons.setVisibility(View.VISIBLE);

        // Landscape only: above buttons
        if (questionContainer != null && !aboveButtonsFlag) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) questionContainer.getLayoutParams();
            params.addRule(RelativeLayout.ABOVE, R.id.container_buttons);

            aboveButtonsFlag = true;
            aboveSeekbarFlag = false;
        }
    }

    private void changeAnswerText(int answerIndex) {
//        Log.d("QuizActivity", String.valueOf(toInterference));
        String[] newText = allAnswers.answerChoices[answerIndex];

        if (QuestionData.USES_SLIDER[questionNumber]) {
            if (answerIndex != currentSeekbarChoice) {
//            Log.d("QuizActivity", "" + currentSeekbarChoice);


                for (int i = 0; i < containerBarText.getChildCount(); i++) {
                    View child = containerBarText.getChildAt(i);

                    if (child instanceof TextView)
                        ((TextView) child).setText(newText[i]);
                }

                // Exception: "Somewhat" sometimes gets sent to 2nd line (interference text)
                if (answerIndex == QuestionData.FLAG) {
//                Log.d("QuizActivity", "Might change somewhat");
                    View somewhatView = containerBarText.getChildAt(1);
                    if (somewhatView instanceof TextView && ((TextView) somewhatView).getLineCount() >= 2) {
                        String dashedSomewhat = getResources().getString(R.string.answer_somewhat_2);
                        ((TextView) somewhatView).setText(dashedSomewhat);
                    }
                }
                
                currentSeekbarChoice = answerIndex;
            }
        } else {
            if (answerIndex != currentButtonChoice) {
                answerNo.setText(newText[0]);
                answerYes.setText(newText[1]);

                currentButtonChoice = answerIndex;
            }
        }
    }

    private void addScore(int questionNumber, int value) {
        scores.putScore(questionNumber, value);
    }

    // Uploads score data to Firebase. If no user ID exists, creates and stores one
    private void uploadToDatabase() {
        Firebase firebaseRef = new Firebase(FirebaseExtras.DATA_URL);
        String userID = getSharedPreferences(LaunchScreenActivity.PREFS_NAME, MODE_PRIVATE)
                .getString(FirebaseExtras.USER_ID, null);

        if (userID != null) {
            gpsLock.lock();
            scores.uploadDataToDatabase(firebaseRef, userID, startTimestamp, endTimestamp,
                    latitude, longitude);
            gpsLock.unlock();
        }

        //Log.d("QuizActivity", "Finished writing data");
    }

    // Which view was clicked: arrows (nextArrow/prevArrow) or buttons (yes/no)
    @Override
    public void onClick(View v) {
        if (v.equals(nextArrow)) {
//            if (questionNumber < QuestionData.RED_FLAG_QUESTION || questionNumber == QuestionData.INTERFERENCE_QUESTION)
            // Clicked next for a slider question, not a yes/no question
            if (QuestionData.USES_SLIDER[questionNumber])
                addScore(questionNumber, answerSliderView.getCurrentIndex());

            handleQuiz(true);
        } else if (v.equals(prevArrow)) {
            handleQuiz(false);
        } else if (v.equals(answerNo)) {
            // Value is from yes/no button
            addScore(questionNumber, 0);
            handleQuiz(true);
        } else if (v.equals(answerYes)) {
            // Value is from yes/no button
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
        timestamp += day + " at ";
        timestamp += hour + ":";
        timestamp += minute;

        return timestamp;
    }

    void clearPreferences() {
        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.remove(SAVE_TIMESTAMP);
        editor.remove(SAVE_QUESTION_NUM);
        editor.remove(SAVE_SCORES_A);
        editor.remove(SAVE_SCORES_B);
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
            //Log.e("QuizActivity", "Not given permission to access location");

            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            // Race condition between callback and access in uploadToDatabase()
            gpsLock.lock();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            //Log.d("QuizActivity", latitude + " " + longitude);
            gpsLock.unlock();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Log.e("QuizActivity", "Failed to connect to google play services");

        /* FIXME: If we can't connect to google, should we force the user to update? It kinda
         * FIXME: says "Hey, we're sending data about you!" */

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
