package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import morningsignout.phq9transcendi.R;

public class QuizActivity extends AppCompatActivity {

    private TextView question, subtitle; //The text of the question
    private Button answer1; //Not at all || yes
    private Button answer2; //Few days a week || no
    private Button answer3; //More than half the week
    private Button answer4; //Everyday

    private String[] questions;

    private int totalScore; //Used for answering questions
    public int[] scoreTracker; //Used to keep track of highest score for each section
    private int sectionNum; //Used to keep track of what section we're on
    private int scoreA;
    private int scoreB;
    private boolean redFlag; //If a red flag question gets answered
    private boolean redFlagQ; //If a question is a red flag question
    private boolean quizDone; //If all questions are answered
    private int questionNumber; //which question the user is on
    private boolean toggle; //For what section a question belongs in

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Grab and set content; inital setup
        question = (TextView) findViewById(R.id.questionView);
        subtitle = (TextView) findViewById(R.id.additionalText);
        answer1 = (Button) findViewById(R.id.answer1);
        answer2 = (Button) findViewById(R.id.answer2);
        answer3 = (Button) findViewById(R.id.answer3);
        answer4 = (Button) findViewById(R.id.answer4);
        questions = getResources().getStringArray(R.array.questions);
        sectionNum = 1;
        scoreTracker = new int[13]; //9 sections
        reset();

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

    private void reset() {
        totalScore = 0;
        sectionNum = 1;
        scoreA = 0;
        scoreB = 0;
        scoreTracker = new int[13];
        redFlag = false;
        redFlagQ = false;
        quizDone = false;
        questionNumber = 1;
        toggle = true;
        subtitle.setText("");
        answer1.setText("Not at all");
        answer2.setText("Few days a week");
        answer3.setText("More than half the week");
        answer4.setText("Everyday");
    }

    private void startQuiz() {
        if(!quizDone) {
            updateQuestions();
            if(toggle) {
                if(!redFlagQ) {
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
        answer1.setVisibility(View.VISIBLE);
        answer2.setVisibility(View.VISIBLE);
        answer3.setVisibility(View.VISIBLE);
        answer4.setVisibility(View.VISIBLE);

        answer1.setText("Take quiz again");
        answer2.setText("Go back");
        answer3.setText("References");
        answer4.setText("Resources");

        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                startQuiz();
            }
        });

        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c = new Intent(QuizActivity.this, IndexActivity.class);
                startActivity(c);
            }
        });
        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(QuizActivity.this, ReferenceActivity.class);
                a.putExtra("page_type", "Resources");
                startActivity(a);
            }
        });

        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b = new Intent(QuizActivity.this, ReferenceActivity.class);
                b.putExtra("page_type", "References");
                startActivity(b);
            }
        });
    }

    private void toggleQuestionsA() {

        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreA = 0;
                startQuiz();
            }
        });

        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreA = 1;
                startQuiz();
            }
        });

        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreA = 2;
                startQuiz();
            }
        });

        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreA = 3;
                startQuiz();
            }
        });
    }

    private void toggleQuestionsB() {

        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreB = 0;
                calculateScore();
                startQuiz();
            }
        });

        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreB = 1;
                calculateScore();
                startQuiz();
            }
        });

        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreB = 2;
                calculateScore();
                startQuiz();
            }
        });

        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreB = 3;
                calculateScore();
                startQuiz();
            }
        });
    }

    private void toggleFlagQuestions() {
        if(questionNumber == 18) {
            answer1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuiz();
                }
            });

            answer2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuiz();
                }
            });

            answer3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuiz();
                }
            });

            answer4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    redFlag = true;
                    startQuiz();
                }
            });
        } else {

            answer1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    redFlag = true;
                    startQuiz();
                }
            });

            answer2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuiz();
                }
            });
        }
    }

    private void calculateScore() {
        if(scoreA >= scoreB) {
            totalScore += scoreA;
            scoreTracker[sectionNum - 1] = scoreA;
            sectionNum++;
        } else {
            totalScore += scoreB;
            scoreTracker[sectionNum - 1] = scoreB;
            sectionNum++;
        }
    }

    private void updateQuestions() {
        question.setText(questions[questionNumber - 1]);
        switch(questionNumber) {
            //SECTION 1
            case 1:
                toggle = false;
                break;
            case 2:
                toggle = true;
                break;
            //SECTION 2
            case 3:
                toggle = false;
                break;
            case 4:
                break;
            //SECTION 3
            case 5:
                toggle = false;
                break;
            case 6:
                break;
            //SECTION 4
            case 7:
                toggle = true;
                break;
            //SECTION 5
            case 8:
                toggle = false;
                break;
            case 9:
                break;
            //SECTION 6
            case 10:
                toggle = true;
                break;
            //SECTION 7
            case 11:
                toggle = false;
                break;
            case 12:
                toggle = true;
                break;
            //SECTION 8
            case 13:
                toggle = false;
                break;
            case 14:
                toggle = true;
                break;
            //SECTION 9
            case 15:
                toggle = false;
                break;
            case 16:
                toggle = true;
                break;
            //RED FLAG QUESTIONS
            case 17:
                redFlagQ = true;
                toggle = true;
                answer1.setText("Yes");
                answer2.setText("No");
                answer3.setVisibility(View.INVISIBLE);
                answer4.setVisibility(View.INVISIBLE);
                break;
            case 18:
                toggle = true;
                answer1.setText("Doesn't affect me");
                answer2.setText("A little");
                answer3.setText("Somewhat");
                answer4.setText("Crippling");
                answer3.setVisibility(View.VISIBLE);
                answer4.setVisibility(View.VISIBLE);
                break;
            case 19:
                toggle = true;
                answer1.setText("Yes");
                answer2.setText("No");
                answer3.setVisibility(View.INVISIBLE);
                answer4.setVisibility(View.INVISIBLE);
                break;
            case 20:
                toggle = true;
                answer1.setText("Yes");
                answer2.setText("No");
                answer3.setVisibility(View.INVISIBLE);
                answer4.setVisibility(View.INVISIBLE);
                break;
            //default
            default:
                question.setText("Ipsum Lorem");
                break;
        }
    }



}
