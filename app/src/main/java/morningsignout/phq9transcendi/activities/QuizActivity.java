package morningsignout.phq9transcendi.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import morningsignout.phq9transcendi.R;

public class QuizActivity extends AppCompatActivity {

    private TextView question; //The text of the question
    private Button answer1; //Not at all || yes
    private Button answer2; //Few days a week || no
    private Button answer3; //More than half the week
    private Button answer4; //Everyday

    private int score; //Used for answering questions
    private boolean redFlag; //If a red flag question gets answered
    private boolean quizDone; //If all questions are answered
    private int questionNumber; //which question the user is on

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Grab and set content; inital setup
        question = (TextView) findViewById(R.id.questionView);
        answer1 = (Button) findViewById(R.id.answer1);
        answer2 = (Button) findViewById(R.id.answer2);
        answer3 = (Button) findViewById(R.id.answer3);
        answer4 = (Button) findViewById(R.id.answer4);

        score = 0;
        redFlag = false;
        quizDone = false;
        questionNumber = 0;

        //Everything is setup, start quiz
        startQuiz();

        //Fab stuff that's automatically included with a fresh activity? commented out
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void startQuiz() {
        if(!redFlag && !quizDone) {
            questionNumber++;
            if(questionNumber > 16) {
                quizDone = true;
            }
            updateQuestions();

        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {

    }

    private void updateQuestions() {
        switch(questionNumber) {
            case 1:
                question.setText("Do you find that you’ve lost a lot of interest in things used to be interested in? This can include hobbies, friends, work, food, or sex");
                break;
            case 2:
                question.setText("Q2");
                break;
            case 3:
                question.setText("Q3");
                break;
            case 4:
                question.setText("Q4");
                break;
            case 5:
                question.setText("Q5");
                break;
            case 6:
                question.setText("Q6");
                break;
            case 7:
                question.setText("Q7");
                break;
            case 8:
                question.setText("Q8");
                break;
            case 9:
                question.setText("Q9");
                break;
            case 10:
                question.setText("Q10");
                break;
            case 11:
                question.setText("Q11");
                break;
            case 12:
                question.setText("Q12");
                break;
            case 13:
                question.setText("Q13");
                break;
            case 14:
                question.setText("Q14");
                break;
            case 15:
                question.setText("Q15");
                break;
            case 16:
                question.setText("Q16");
                break;
            default:
                question.setText("Q17");
                break;
        }
    }



}
