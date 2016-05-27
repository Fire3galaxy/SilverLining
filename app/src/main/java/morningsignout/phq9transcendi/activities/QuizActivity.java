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
    private static final int RED_FLAG_QUESTION = 17;
    private static final int NUM_QUESTIONS = 21;

    private TextView question, subtitle; //The text of the question
    private AnswerSeekBar answerBar;
    private Button answerNo, answerYes;
    private ImageButton next, prev;
    private LinearLayout containerButtons, containerBarText;

    private String[] questions;
    private String[] answersNormal;
    private String[] answersRedFlag;

    private Scores scores;  // Used for answering questions
    private boolean quizDone; //If all questions are answered
    private int questionNumber; //which question the user is on

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
        scores = new Scores();
        quizDone = false;
        questionNumber = 1;
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
            questionNumber++;
            if(questionNumber > NUM_QUESTIONS) {
                quizDone = true;
            }

        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        Intent results = new Intent(this, ResultsActivity.class);
        results.putExtra(ResultsActivity.SCORE, scores.getTotalScore());
        results.putExtra(ResultsActivity.RED_FLAG, scores.containsRedFlag());
        startActivity(results);
    }

    private void addScore(int value) {
        // Question - 2 because the number is set to the next question and starts at base 1
        scores.putScore(Scores.questions[questionNumber - 2], value);
    }

    private void updateQuestions() {
        question.setText(questions[questionNumber - 1]);

        if (questionNumber < RED_FLAG_QUESTION && answerBar.getVisibility() != View.VISIBLE)
            putSeekBar();
        else if (questionNumber >= RED_FLAG_QUESTION
                && containerButtons.getVisibility() != View.VISIBLE)
            putButtons();
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

    @Override
    public void onClick(View v) {
        // Which view was clicked: arrows (next/prev) or buttons (yes/no)
        if (v.equals(next)) {
            if (questionNumber < RED_FLAG_QUESTION
                    && scores.getScore(questionNumber - 1) != answerBar.getAnswer())
                addScore(answerBar.getAnswer());

            startQuiz();
        } else if (v.equals(prev)) {
            // Question - 2 because the number is set to the next question, not the current question
            questionNumber = Math.max(1, questionNumber - 2);

            // Set answer bar to previously saved answer
            if (questionNumber < RED_FLAG_QUESTION)
                answerBar.setAnswer(scores.getScore(questionNumber - 1));

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
}
