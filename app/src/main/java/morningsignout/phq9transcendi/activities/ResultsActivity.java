package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import morningsignout.phq9transcendi.R;

/**
 * Created by pokeforce on 5/24/16.
 */
public class ResultsActivity extends AppCompatActivity implements View.OnClickListener {
    static public final String SCORE = "Score",
        RED_FLAG = "Red Flag";

    TextView detailsText;
    Button finishUpButton;

    // First screen only
    FrameLayout scoreContainer;
    TextView scoreText;
    TextView redFlagText;

    int totalScore;
    boolean redFlag;
    boolean firstScreen = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent args = getIntent();
        if (args == null)
            throw new NullPointerException("Results activity started without arguments");

        // Results of quiz
        totalScore = args.getIntExtra(SCORE, 0);
        redFlag = args.getBooleanExtra(RED_FLAG, false);

        // Views for results activity
        detailsText = (TextView) findViewById(R.id.textView_details);   // Shows main text
        finishUpButton = (Button) findViewById(R.id.button_finish_up);  // Moves to next screen

        // Views that will be removed once unneeded
        scoreContainer = (FrameLayout) findViewById(R.id.container_score);
        scoreText = (TextView) findViewById(R.id.textView_score);
        redFlagText = (TextView) findViewById(R.id.textView_red_flag);

        // If red flag question is answered yes/severe
        scoreText.setText(String.valueOf(totalScore));
        if (!redFlag)
            redFlagText.setVisibility(View.INVISIBLE);

        finishUpButton.setOnClickListener(this);

        String[] scoreEval = getResources().getStringArray(R.array.scoreEval);
//        if(redFlag) {
//            //alert
//            detailsText.setText("Your score is " + totalScore +", but one or more of your answers show that you may suffer from severe depression.");
//        } else {
//            //proceed normally with score
//            question.setText("You're all done! Your score is " + totalScore);
//        }
//        if(totalScore == 0) {
//            subtitle.setText(scoreEval[0]);
//        } else if(totalScore >= 1 && totalScore < 5) {
//            subtitle.setText(scoreEval[1]);
//        } else if(totalScore >= 5 && totalScore < 10) {
//            subtitle.setText(scoreEval[2]);
//        } else if(totalScore >= 10 && totalScore < 15) {
//            subtitle.setText(scoreEval[3]);
//        } else if(totalScore >= 15 && totalScore < 20) {
//            subtitle.setText(scoreEval[4]);
//        } else if(totalScore >= 20) {
//            subtitle.setText(scoreEval[5]);
//        }
    }

    @Override
    public void onClick(View v) {
        if (firstScreen) {
            // Remove unneeded views
            scoreContainer.setVisibility(View.GONE);
            redFlagText.setVisibility(View.GONE);

            // Set new text
            finishUpButton.setText(getResources().getText(R.string.finish_up));

            firstScreen = false;
        }
    }
}
