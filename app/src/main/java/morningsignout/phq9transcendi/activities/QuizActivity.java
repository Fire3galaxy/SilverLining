package morningsignout.phq9transcendi.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.HelperClasses.QuestionData;
import morningsignout.phq9transcendi.HelperClasses.Scores;
import morningsignout.phq9transcendi.HelperClasses.Utils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/* What to update when question is added: If new answer type is added, add string array to
 * QuestionData allAnswers in constructor
 */
public class QuizActivity extends AppCompatActivity implements ImageButton.OnClickListener {
    private static final String LOG_NAME = "QuizActivity";
    private static final String SAVE_TIMESTAMP = "Timestamp", SAVE_QUESTION_NUM = "Question Number",
        SAVE_SCORES_A = "Score Values", SAVE_SCORES_B = "Visit values";
    private static final int DFLT_QUESTION_FONT_SIZE = 24; // in Pixels

    // Use String.format() with this to display current question
    private final String numberString = "%1$d/" + String.valueOf(QuestionData.NUM_QUESTIONS);

    private TextView questionTextView, questionNumText; //The text of the question
    private Button answerNo, answerYes;
    private ImageButton nextArrow, prevArrow;
    private LinearLayout containerButtons;
    private RadioGroup radioButtonGroup;

    private String[] questionArray;
    QuestionData allAnswers;
    int currentAnswerChoice;
    int currentButtonChoice;
    private String startTimestamp, endTimestamp;
    private Scores scores;                          // Used for keeping track of score
    private int questionNumber;                     // Which question the user is on (zero-based)
    private boolean isFinishingFlag;                // Used in onPause() to save/not save
    private boolean isFirstTimeFlag;                // Used in onCreate() and onStart() for continue dialog
                                                    // Note: Currently not using "continue" feature except for saving state
    private AlertDialog.Builder dialogBuilder;      // To confirm user wants to quit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int theme = Utils.GetTheme(this);
        Utils.onActivityCreateSetTheme(this, theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

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
        radioButtonGroup = (RadioGroup) findViewById(R.id.answer_choices);


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

        // Autofit setup for Question textView
        questionTextView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom - top > 0) {
                    Rect bounds = new Rect();
                    questionTextView.getLineBounds(1, bounds);
                    float lineHeight = DFLT_QUESTION_FONT_SIZE
                            * getResources().getDisplayMetrics().scaledDensity;
                    float spacingHeight = bounds.height() - lineHeight;
                    float maxTextHeight = questionTextView.getHeight()
                            - questionTextView.getPaddingTop()
                            - questionTextView.getPaddingBottom();
                    int maxLines = (int) (maxTextHeight / (lineHeight + spacingHeight));
                    questionTextView.setMaxLines(maxLines);
                    v.removeOnLayoutChangeListener(this);
                }
            }
        });

        isFinishingFlag = false;
        questionArray = res.getStringArray(R.array.questions);
        allAnswers = new QuestionData();
        allAnswers.answerChoices[QuestionData.NORMAL] = res.getStringArray(R.array.answers_normal);
        allAnswers.answerChoices[QuestionData.FLAG] = res.getStringArray(R.array.answers_flag);
        allAnswers.answerChoices[QuestionData.DEPRESSION] = res.getStringArray(R.array.answers_depression);
        allAnswers.answerChoices[QuestionData.SITUATION] = res.getStringArray(R.array.answers_situation);
        allAnswers.answerChoices[QuestionData.APPOINTMENT] = res.getStringArray(R.array.answers_appointment);
        allAnswers.answerChoices[QuestionData.YES_NO] = res.getStringArray(R.array.answers_yes_no);
        allAnswers.answerChoices[QuestionData.STRANGER] = res.getStringArray(R.array.answers_stranger);
        allAnswers.answerChoices[QuestionData.SUPPORTIVE] = res.getStringArray(R.array.answers_supportive);
        currentAnswerChoice = -1;
        currentButtonChoice = -1;
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
        //answerSliderView.setIndex(0);

        handleQuiz(true);   // Everything is set up, start quiz

        // Set all buttons to onClickListener function here
        nextArrow.setOnClickListener(this);
        prevArrow.setOnClickListener(this);
        answerNo.setOnClickListener(this);
        answerYes.setOnClickListener(this);
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
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Reinitialize state variables
        SharedPreferences preferences = getPreferences(0);
        if (savedInstanceState != null && preferences.contains(SAVE_TIMESTAMP)) {
            startTimestamp = preferences.getString(SAVE_TIMESTAMP, getTimestamp());
            int questionNumber = preferences.getInt(SAVE_QUESTION_NUM, 0);
            String scoresA = preferences.getString(SAVE_SCORES_A, null);
            String scoresB = preferences.getString(SAVE_SCORES_B, null);
            scores = new Scores(scoresA, scoresB);

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

    // Calls all functions to change question.
    // NextQuestion determines if questionNumber increases/decreases.
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
        uploadToDatabase();

        clearPreferences();
        isFinishingFlag = true;

        Intent results = new Intent(this, ResultsActivity.class);
        results.putExtra(ResultsActivity.SCORE, scores.getFinalScore());
        results.putExtra(ResultsActivity.RED_FLAG, scores.containsRedFlag());
        results.putExtra(ResultsActivity.RED_FLAG_BITS, scores.getRedFlagBits());
        results.putExtra(ResultsActivity.FAM_OR_CULTURE_BITS, scores.getFamOrCultureBits());
        results.putExtra(ResultsActivity.FAMILY_UNDERSTANDS_ANSWER, scores.getFamilyUnderstandsAnswer());
        results.putExtra(ResultsActivity.CAN_SEE_ANSWER, scores.getiAppointmentAnswer());
        startActivity(results);
        finish();
    }

    //changes the question text, answer text, and answer method
    //also checks the radiobutton that was previously chosen
    private void updateQuestions() {
        Log.d("UpdateQuestion", "Q#:" + questionNumber + ", Text: "+questionArray[questionNumber] );
        questionTextView.setText(questionArray[questionNumber]);                    // Question text
        questionNumText.setText(String.format(numberString, questionNumber + 1));   // Question #

        // Possible string array options for answers are listed in QuestionData
        changeAnswerText(QuestionData.ANSW_CHOICE[questionNumber]);

        // Shows either the radio buttons or regular yes/no buttons
        if (QuestionData.USES_SLIDER[questionNumber]) {
            putRadioButtons(questionNumber);
        } else {
            putButtons();
        }

        // Show previously saved answer if previous button is clicked
        if (scores.questionIsVisited(questionNumber)) {
            //TODO set radoi button to be the score saved
            //answerSliderView.setIndex(scores.getQuestionScore(questionNumber));
            int qnScore = scores.getQuestionScore(questionNumber);
            //if this answer has been chosen, we can check the correct option
            //TODO: check to see that index is <= number of radio buttons
            if (qnScore > -1) {
                System.out.println("ANSWERED: qnscore is " + qnScore);
                radioButtonGroup.clearCheck();
                RadioButton rb = ((RadioButton)radioButtonGroup.getChildAt(qnScore));
                rb.setChecked(true);
            }
        }
        // Hide previous button on first question
        if (questionNumber == 0)
            prevArrow.setVisibility(View.INVISIBLE);
        else if (prevArrow.getVisibility() != View.VISIBLE)
            prevArrow.setVisibility(View.VISIBLE);

        // Hide nextArrow button on red flag questions unless already answered or is interference
        if (!QuestionData.USES_SLIDER[questionNumber]
                && (questionNumber + 1 == QuestionData.NUM_QUESTIONS || !scores.questionIsVisited(questionNumber)))
            nextArrow.setVisibility(View.INVISIBLE);
        else
            nextArrow.setVisibility(View.VISIBLE);
    }

    //sets the radio buttons to be visible and buttons to be invisible
    //Number of buttons is determined by what question it is
    //question number is passed in
    private void putRadioButtons(int answerIndex) {
        Log.d("SettingRadioButtons", "AnswerIndex" + answerIndex);
        int answerType = QuestionData.ANSW_CHOICE[answerIndex];
        int numButtons = (allAnswers.answerChoices[answerType]).length;
        Log.d("SettingRadioButtons","NUMBUTTONS:" + numButtons);
        radioButtonGroup.setVisibility(View.VISIBLE);
        int i;
        for (i= 0; i < numButtons && i < 5; i++) {
            RadioButton rb = (RadioButton)radioButtonGroup.getChildAt(i);
            rb.setVisibility(View.VISIBLE);
        }
        for (i = i ; i < 5; i++) {
            RadioButton rb = (RadioButton)radioButtonGroup.getChildAt(i);
            rb.setVisibility(View.GONE);
        }

        if (containerButtons.getVisibility() != View.GONE) {
            containerButtons.setVisibility(View.GONE);
        }
    }

    private void putButtons() {
        if (radioButtonGroup.getVisibility() != View.INVISIBLE)
            radioButtonGroup.setVisibility(View.INVISIBLE);
        if (containerButtons.getVisibility() != View.VISIBLE)
            containerButtons.setVisibility(View.VISIBLE);
    }

    private void changeAnswerText(int answerIndex) {
        String[] newText = allAnswers.answerChoices[answerIndex];

        if (QuestionData.USES_SLIDER[questionNumber]) {

            radioButtonGroup.clearCheck();
            //loop through and set each button
            //TODO only loop through number of questions
            int count = radioButtonGroup.getChildCount();
            for (int i=0;i<count;i++) {
                RadioButton rb = (RadioButton) radioButtonGroup.getChildAt(i);
                if (i < newText.length) {
                    rb.setText(newText[i]);
                }
            }
        } else {
            if (answerIndex != currentButtonChoice) {
                answerYes.setText(newText[0]);
                answerNo.setText(newText[1]);

                currentButtonChoice = answerIndex;
            }
        }
    }

    private void addScore(int questionNumber, int value) {
        scores.putScore(questionNumber, value);
    }

    // Uploads score data to Firebase. If no user ID exists, creates and stores one
    private void uploadToDatabase() {
        scores.uploadDataToDatabase(endTimestamp);
    }

    // Which view was clicked: arrows (nextArrow/prevArrow) or buttons (yes/no)
    @Override
    public void onClick(View v) {
        // clicked for a radio button question, not a yes/no question
        //Move onto the next question
        if (v.equals(nextArrow)) {
            saveCurrentScore();
            System.out.println("NEXT");
            handleQuiz(true);
        } else if (v.equals(prevArrow)) {
            saveCurrentScore();
            System.out.println("PREV");
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

    public void saveCurrentScore(){
        //update currentAnswerChoice
        int count = radioButtonGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            RadioButton rb = (RadioButton) radioButtonGroup.getChildAt(i);
            if (rb.isChecked()) {
                currentAnswerChoice = i;
            }
        }
        // Clicked next for a slider question, not a yes/no question
        // save the score for this question
        if (QuestionData.USES_SLIDER[questionNumber]) {
            System.out.println("ANSWER IS BEING SAVED");
            addScore(questionNumber, currentAnswerChoice);
        }
        currentAnswerChoice = -1;

    }

    // FIXME: Use a class like DateFormat next time
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
}