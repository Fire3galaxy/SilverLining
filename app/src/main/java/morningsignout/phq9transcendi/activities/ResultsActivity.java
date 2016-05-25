package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import morningsignout.phq9transcendi.R;

/**
 * Created by pokeforce on 5/24/16.
 */
public class ResultsActivity extends AppCompatActivity {
    static public final String SCORE = "Score",
        RED_FLAG = "Red Flag";

    int totalScore;
    boolean redFlag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent args = getIntent();
        if (args == null)
            throw new NullPointerException("Results activity started without arguments");

        totalScore = args.getIntExtra(SCORE, 0);
        redFlag = args.getBooleanExtra(RED_FLAG, false);

        TextView detailsText = (TextView) findViewById(R.id.textView_details);
        String[] scoreEval = getResources().getStringArray(R.array.scoreEval);
//
//        if(redFlag) {
//            //alert
//            question.setText("Your score is " + totalScore +", but one or more of your answers show that you may suffer from severe depression.");
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
}
