package morningsignout.phq9transcendi.activities;

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
            Log.d(LOG_NAME, String.valueOf(toggle));
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
        String[] scoreEval = getResources().getStringArray(R.array.scoreEval);
        if(redFlag) {
            //alert
            question.setText("Your score is " + totalScore +", but one or more of your answers show that you may suffer from severe depression.");
        } else {
            //proceed normally with score
            question.setText("You're all done! Your score is " + totalScore);
        }
        if(totalScore == 0) {
            subtitle.setText(scoreEval[0]);
        } else if(totalScore >= 1 && totalScore < 5) {
            subtitle.setText(scoreEval[1]);
        } else if(totalScore >= 5 && totalScore < 10) {
            subtitle.setText(scoreEval[2]);
        } else if(totalScore >= 10 && totalScore < 15) {
            subtitle.setText(scoreEval[3]);
        } else if(totalScore >= 15 && totalScore < 20) {
            subtitle.setText(scoreEval[4]);
        } else if(totalScore >= 20) {
            subtitle.setText(scoreEval[5]);
        }
//        answer1.setVisibility(View.VISIBLE);
//        answer2.setVisibility(View.VISIBLE);
//        answer3.setVisibility(View.VISIBLE);
//        answer4.setVisibility(View.VISIBLE);
//
//        answer1.setText("Take quiz again");
//        answer2.setText("Go back");
//        answer3.setText("References");
//        answer4.setText("Resources");

//        answer1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                reset();
//                startQuiz();
//            }
//        });
//
//        answer2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent c = new Intent(QuizActivity.this, IndexActivity.class);
//                startActivity(c);
//            }
//        });
//        answer3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent a = new Intent(QuizActivity.this, ReferenceActivity.class);
//                a.putExtra("page_type", "Resources");
//                startActivity(a);
//            }
//        });
//
//        answer4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent b = new Intent(QuizActivity.this, ReferenceActivity.class);
//                b.putExtra("page_type", "References");
//                startActivity(b);
//            }
//        });
    }

    private void toggleQuestionsA() {
        appState = AppState.questionA;

//        answer1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scoreA = 0;
//                startQuiz();
//            }
//        });
//
//        answer2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scoreA = 1;
//                startQuiz();
//            }
//        });
//
//        answer3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scoreA = 2;
//                startQuiz();
//            }
//        });
//
//        answer4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scoreA = 3;
//                startQuiz();
//            }
//        });
    }

    private void toggleQuestionsB() {
        appState = AppState.questionB;

//        answer1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scoreB = 0;
//                calculateScore();
//                startQuiz();
//            }
//        });
//
//        answer2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scoreB = 1;
//                calculateScore();
//                startQuiz();
//            }
//        });
//
//        answer3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scoreB = 2;
//                calculateScore();
//                startQuiz();
//            }
//        });
//
//        answer4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scoreB = 3;
//                calculateScore();
//                startQuiz();
//            }
//        });
    }

    private void toggleFlagQuestions() {
        appState = AppState.questionFlag;

//        if(questionNumber == 18) {
//            answer1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startQuiz();
//                }
//            });
//
//            answer2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startQuiz();
//                }
//            });
//
//            answer3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startQuiz();
//                }
//            });
//
//            answer4.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    redFlag = true;
//                    startQuiz();
//                }
//            });
//        } else {
//
//            answer1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    redFlag = true;
//                    startQuiz();
//                }
//            });
//
//            answer2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startQuiz();
//                }
//            });
//        }
    }

    private void calculateScore() {
        if(scoreA >= scoreB) {
            totalScore += scoreA;
        } else {
            totalScore += scoreB;
        }
        Log.d(LOG_NAME, String.valueOf(totalScore));
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
                    Log.d(LOG_NAME, "Score A: " + String.valueOf(scoreA));
                    startQuiz();
                    break;
                case questionB:
                    scoreB = answerBar.getAnswer();
                    Log.d(LOG_NAME, "Score B: " + String.valueOf(scoreB));
                    calculateScore();
                    startQuiz();
                    break;
                case questionFlag:
                    if (questionNumber == 18 && answerBar.getAnswer() == 3)
                        redFlag = true;
                    Log.d(LOG_NAME, "Red Flag: " + String.valueOf(redFlag));
                    startQuiz();
                    break;
            }
        } else if (v.equals(prev)) {
            Log.d(LOG_NAME, "Prev");
        } else if (v.equals(answerNo)) {
            Log.d(LOG_NAME, "Red Flag: " + String.valueOf(redFlag));
            startQuiz();
        } else if (v.equals(answerYes)) {
            redFlag = true;
            Log.d(LOG_NAME, "Red Flag: " + String.valueOf(redFlag));
            startQuiz();
        }
    }
}
