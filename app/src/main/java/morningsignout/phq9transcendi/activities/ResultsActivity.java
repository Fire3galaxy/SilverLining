package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import morningsignout.phq9transcendi.R;

/**
 * Created by pokeforce on 5/24/16.
 */
public class ResultsActivity extends AppCompatActivity implements View.OnClickListener {
    static public final String SCORE = "Score",
        RED_FLAG = "Red Flag";

    // For every screen
    Button finishUpButton;

    // First screen
    LinearLayout scoreContainer;
    TextView allDoneText;
    TextView scoreText;
    TextView redFlagText;

    // Second screen
    TextView detailsText;

    int totalScore;
    boolean redFlag;
    int screenNumber = 0;

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
        finishUpButton = (Button) findViewById(R.id.button_finish_up);  // Moves to next screen

        scoreContainer = (LinearLayout) findViewById(R.id.container_score); // First screen views
        allDoneText = (TextView) findViewById(R.id.textView_all_done);      // Shows main text
        scoreText = (TextView) findViewById(R.id.textView_score);           // Score #
        redFlagText = (TextView) findViewById(R.id.textView_red_flag);

        detailsText = (TextView) findViewById(R.id.textView_details);   // Second screen

        // If red flag question is answered yes/severe
        scoreText.setText(String.valueOf(totalScore));
        if (!redFlag)
            redFlagText.setVisibility(View.INVISIBLE);

        finishUpButton.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        // FIXME: dialog asking if user wants to return to home screen
    }

    @Override
    public void onClick(View v) {
        switch (screenNumber) {
            case 0:
                // Remove unneeded views
                scoreContainer.setVisibility(View.GONE);

                // Set new text
                detailsText.setText(getResult());
                finishUpButton.setText(getResources().getText(R.string.finish_up));

                screenNumber = 1;
                break;
            case 1:
                detailsText.setText(getResources().getText(R.string.wrap_up_text));
                finishUpButton.setText(getResources().getText(R.string.take_me_home));
                screenNumber = 2;
                break;
            case 2:
                Intent backHome = new Intent(this, IndexActivity.class);
                startActivity(backHome);
                finish();
                break;
            default:
                Log.e("ResultsActivity", "Should not happen");
                break;
        }
    }

    String getResult() {
        String[] scoreEval = getResources().getStringArray(R.array.scoreEval);
        String result = getResources().getString(R.string.scoreResult);

        if(totalScore == 0) {
            result = String.format(result, totalScore, scoreEval[0]);
        } else if(totalScore >= 1 && totalScore < 5) {
            result = String.format(result, totalScore, scoreEval[1]);
        } else if(totalScore >= 5 && totalScore < 10) {
            result = String.format(result, totalScore, scoreEval[2]);
        } else if(totalScore >= 10 && totalScore < 15) {
            result = String.format(result, totalScore, scoreEval[3]);
        } else if(totalScore >= 15 && totalScore < 20) {
            result = String.format(result, totalScore, scoreEval[4]);
        } else if(totalScore >= 20) {
            result = String.format(result, totalScore, scoreEval[5]);
        }

        String[] scoreDetails = getResources().getStringArray(R.array.scoreDetails);
        result += "\n\n";

        if(redFlag) {
            result += scoreDetails[2];
        } else if (totalScore == 0) {
            result += scoreDetails[0];
        } else {
            result += scoreDetails[1];
        }

        return result;
    }
}
