package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import morningsignout.phq9transcendi.R;

public class QuizActivity extends AppCompatActivity implements ImageButton.OnClickListener {

    private static final String LOG_NAME = "QuizActivity";

    private TextView question, subtitle; //The text of the question
    private AnswerSeekBar answerBar;
    private Button answerNo, answerYes;
    private ImageButton next, prev;
    private LinearLayout containerButtons, containerBarText;

    private String[] questions;
    private String[] answersNormal;
    private String[] answersRedFlag;

    private int totalScore; //Used for answering questions
    private int scoreA;
    private int scoreB;
    private boolean redFlag; //If a red flag question gets answered
    private boolean redFlagQuestion; //If a question is a red flag question
    private boolean quizDone; //If all questions are answered
    private int questionNumber; //which question the user is on
    private boolean toggle; //For what section a question belongs in

    private enum AppState {questionA, questionB, questionFlag};
    AppState appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Grab and set content; inital setup
        question = (TextView) findViewById(R.id.questionView);
        subtitle = (TextView) findViewById(R.id.additionalText);
        answerBar = (AnswerSeekBar) findViewById(R.id.seekBar_quiz_answer);
        answerNo = (Button) findViewById(R.id.button_answer_no);
        answerYes = (Button) findViewById(R.id.button_answer_yes);
        next = (ImageButton) findViewById(R.id.imageButton_nextq);
        prev = (ImageButton) findViewById(R.id.imageButton_prevq);
        containerButtons = (LinearLayout) findViewById(R.id.container_buttons);
        containerBarText = (LinearLayout) findViewById(R.id.container_bar_text);

        questions = getResources().getStringArray(R.array.questions);
        answersNormal = getResources().getStringArray(R.array.answers_normal);
        answersRedFlag = getResources().getStringArray(R.array.answers_flag);

        reset();

        // Set all buttons to onClickListener function here
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        answerNo.setOnClickListener(this);
        answerYes.setOnClickListener(this);

        //Everything is set up, start quiz
        startQuiz();
    }

    private void reset() {
        totalScore = 0;
        scoreA = 0;
        scoreB = 0;
        redFlag = false;
        redFlagQuestion = false;
        quizDone = false;
        questionNumber = 1;
        toggle = true;
        appState = AppState.questionA;
        answerBar.setProgress(0);
        containerButtons.setVisibility(View.GONE);
        containerBarText.setVisibility(View.VISIBLE);
        subtitle.setText("");

        for (int i = 0; i < containerBarText.getChildCount(); i++)
            ((TextView) containerBarText.getChildAt(i)).setText(answersNormal[i]);
    }

    private void startQuiz() {
        if(!quizDone) {
            updateQuestions();
            if(toggle) {
                if(!redFlagQuestion) {
                    toggleQuestionsA();
                } else {
                    toggleFlagQuestions();
                }
            } else {
                toggleQuestionsB();
            }
            questionNumber++;
            if(questionNumber > 20) {
                quizDone = true;
            }

        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        Intent results = new Intent(this, ResultsActivity.class);
        results.putExtra(ResultsActivity.SCORE, totalScore);
        results.putExtra(ResultsActivity.RED_FLAG, redFlag);
        startActivity(results);
    }

    private void toggleQuestionsA() {
        appState = AppState.questionA;
    }

    private void toggleQuestionsB() {
        appState = AppState.questionB;
    }

    private void toggleFlagQuestions() {
        appState = AppState.questionFlag;
    }

    private void calculateScore() {
        if(scoreA >= scoreB) {
            totalScore += scoreA;
        } else {
            totalScore += scoreB;
        }
    }

    private void updateQuestions() {
        question.setText(questions[questionNumber - 1]);
        switch(questionNumber) {
            //SECTION 1
            case 1:
                toggle = true;
                break;
            case 2:
                toggle = false;
                break;
            //SECTION 2
            case 3:
                toggle = true;
                break;
            case 4:
                toggle = false;
                break;
            //SECTION 3
            case 5:
                toggle = true;
                break;
            case 6:
                toggle = false;
                break;
            //SECTION 4 - No toggle A answer b/c 1 question
            case 7:
                scoreA = 0;
                toggle = false;
                break;
            //SECTION 5
            case 8:
                toggle = true;
                break;
            case 9:
                toggle = false;
                break;
            //SECTION 6 - No toggle A answer b/c 1 question
            case 10:
                scoreA = 0;
                toggle = false;
                break;
            //SECTION 7
            case 11:
                toggle = true;
                break;
            case 12:
                toggle = false;
                break;
            //SECTION 8
            case 13:
                toggle = true;
                break;
            case 14:
                toggle = false;
                break;
            //SECTION 9
            case 15:
                toggle = true;
                break;
            case 16:
                toggle = false;
                break;
            //RED FLAG QUESTIONS
            case 17:
                redFlagQuestion = true;
                toggle = true;
                answerBar.setVisibility(View.GONE);
                containerBarText.setVisibility(View.GONE);
                containerButtons.setVisibility(View.VISIBLE);
                break;
            case 18:
                toggle = true;
                for (int i = 0; i < containerBarText.getChildCount(); i++)
                    ((TextView) containerBarText.getChildAt(i)).setText(answersRedFlag[i]);
                answerBar.setProgress(0);
                answerBar.setVisibility(View.VISIBLE);
                containerBarText.setVisibility(View.VISIBLE);
                containerButtons.setVisibility(View.GONE);
                break;
            case 19:
                toggle = true;
                answerBar.setVisibility(View.GONE);
                containerBarText.setVisibility(View.GONE);
                containerButtons.setVisibility(View.VISIBLE);
                break;
            case 20:
                toggle = true;
                answerBar.setVisibility(View.GONE);
                containerBarText.setVisibility(View.GONE);
                containerButtons.setVisibility(View.VISIBLE);
                break;
            //default
            default:
                question.setText("Ipsum Lorem");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(next)) {
            switch (appState) {
                case questionA:
                    scoreA = answerBar.getAnswer();
                    startQuiz();
                    break;
                case questionB:
                    scoreB = answerBar.getAnswer();
                    calculateScore();
                    startQuiz();
                    break;
                case questionFlag:
                    if (questionNumber == 18 && answerBar.getAnswer() == 3)
                        redFlag = true;
                    startQuiz();
                    break;
            }
        } else if (v.equals(prev)) {
            Log.d(LOG_NAME, "Prev");
        } else if (v.equals(answerNo)) {
            startQuiz();
        } else if (v.equals(answerYes)) {
            redFlag = true;
            startQuiz();
        }
    }
}
