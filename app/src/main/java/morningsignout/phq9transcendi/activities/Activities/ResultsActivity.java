package morningsignout.phq9transcendi.activities.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DateFormat;
import java.util.BitSet;
import java.util.Calendar;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.activities.HelperClasses.BlinkScrollView;
import morningsignout.phq9transcendi.activities.HelperClasses.Utils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by pokeforce on 5/24/16.
 */
public class ResultsActivity extends AppCompatActivity implements View.OnClickListener {
    static public final String SCORE = "Score",
        RED_FLAG = "Red Flag",
        RED_FLAG_BITS = "Red Flag BitSet",
        FAM_OR_CULTURE_BITS = "Family or Cultural Background BitSet",
        FAMILY_UNDERSTANDS_ANSWER = "Family understands answer value",
        CAN_SEE_ANSWER = "Can see answer value";

    // For every screen
    Button finishUpButton;

    // First screen
    LinearLayout scoreContainer;
    TextView allDoneText;
    TextView scoreText;
    TextView redFlagText;
    ImageView scoreFan;

    // Second screen
    BlinkScrollView blinkScrollView;
    TextView detailsText;

    // Third Screen
    Button feedbackButton;
    Button emailresultsButton;
    int totalScore;
    boolean redFlag;
    BitSet redFlagBitSet;
    BitSet famOrCultureBitSet;
    int familyUnderstandsAnswer;
    int canSeeAnswer;
    int screenNumber = 0;
    AlertDialog.Builder dialogBuilder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this, Utils.GetTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent args = getIntent();
        if (args == null)
            throw new NullPointerException("Results activity started without arguments");

        // Results of quiz
        totalScore = args.getIntExtra(SCORE, 0);
        redFlag = args.getBooleanExtra(RED_FLAG, false);
        redFlagBitSet = (BitSet) args.getSerializableExtra(RED_FLAG_BITS);
        famOrCultureBitSet = (BitSet) args.getSerializableExtra(FAM_OR_CULTURE_BITS);
        familyUnderstandsAnswer = args.getIntExtra(FAMILY_UNDERSTANDS_ANSWER, 0);
        canSeeAnswer = args.getIntExtra(CAN_SEE_ANSWER, 0);

        // Views for results activity
        finishUpButton = (Button) findViewById(R.id.button_finish_up);      // Moves to next screen
        allDoneText = (TextView) findViewById(R.id.textView_all_done);      // First screen, shows main text
        scoreText = (TextView) findViewById(R.id.textView_score);           // Score #
        redFlagText = (TextView) findViewById(R.id.textView_red_flag);
        scoreFan = (ImageView) findViewById(R.id.imageView_score_fan);
        detailsText = (TextView) findViewById(R.id.textView_details);       // Second screen
        blinkScrollView = (BlinkScrollView) findViewById(R.id.container_detail);
        emailresultsButton = (Button) findViewById(R.id.button_email_results);
        emailresultsButton.setOnClickListener(this);

        // Third Screen
        feedbackButton = (Button) findViewById(R.id.button_feedback);
        feedbackButton.setOnClickListener(this);

        //change color of scoreFan
        Drawable fan = ContextCompat.getDrawable(getApplicationContext(), R.drawable.score_fan);
        if(Utils.GetTheme(this)== 0){
            fan.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.jungle_mist), PorterDuff.Mode.SRC_ATOP);
        }
        else{
            fan.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.wafer), PorterDuff.Mode.SRC_ATOP);
        }
        ((ImageView)findViewById(R.id.imageView_score_fan)).setImageDrawable(fan);
        // Blink when scrollable (once)
        detailsText.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (v.getHeight() != 0 && blinkScrollView.canScrollVertically()) {
                    blinkScrollView.blinkScrollBar();
                    v.removeOnLayoutChangeListener(this);   // Remove listener after blink
                }
            }
        });

        // Configuration-specific variables
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            scoreContainer = (LinearLayout) findViewById(R.id.container_score);

            String allDone = getResources().getString(R.string.all_done);
            allDone = String.format(allDone, totalScore);
            allDoneText.setText(allDone);

            if (!redFlag)
                redFlagText.setVisibility(View.INVISIBLE);
        } else {
            scoreContainer = null;

            String allDone = getResources().getString(R.string.all_done_land);
            allDone = String.format(allDone, totalScore);
            allDoneText.setText(allDone);

            if (!redFlag)
                redFlagText.setVisibility(View.GONE);
        }

        scoreText.setText(String.valueOf(totalScore));
        finishUpButton.setOnClickListener(this);
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(R.string.dialog_quit_results)
                .setPositiveButton(R.string.dialog_return_home, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent backToMenu = new Intent(ResultsActivity.this, IndexActivity.class);
                        backToMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(backToMenu);
                        finish();
                    }
                }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        // Restore state
        if (savedInstanceState != null) {
            screenNumber = savedInstanceState.getInt("Screen Number", 0);
            if (screenNumber > 0) {
                screenNumber--;
                onClick(null);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Screen Number", screenNumber);
    }

    @Override
    public void onBackPressed() {
        dialogBuilder.create().show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));   // For custom Rubik font
    }

    @Override
    public void onClick(View v) {
        if (v != null && v.equals(feedbackButton)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://goo.gl/forms/N7L6KslgWUWPzw1J3"));
            startActivity(intent);
        } else if(v != null && v.equals(emailresultsButton)) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + ""));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Results from the Silver Lining Questionnaire");
//            String msg = "Your score was: " + totalScore + ". " + getResult();
            emailIntent.putExtra(Intent.EXTRA_TEXT, getEmailMessage());

            try {
                startActivity(Intent.createChooser(emailIntent, "Send email using..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            switch (screenNumber) {
                case 0:
                    // Remove unneeded views
                    if (scoreContainer != null)
                        scoreContainer.setVisibility(View.GONE);    // portrait
                    else {
                        allDoneText.setVisibility(View.GONE);       // landscape
                        scoreText.setVisibility(View.GONE);
                        redFlagText.setVisibility(View.GONE);
                        scoreFan.setVisibility(View.GONE);
                    }

                    // Set new text & blink to indicate scrolling
                    detailsText.setText(getResult());
                    finishUpButton.setText(getResources().getText(R.string.finish_up));
                    emailresultsButton.setVisibility(View.VISIBLE);
                    screenNumber = 1;
                    break;
                case 1:
                    // Remove unneeded views (in case of config changes, only screenNumber state is preserved)
                    if (scoreContainer != null)
                        scoreContainer.setVisibility(View.GONE);    // portrait
                    else {
                        allDoneText.setVisibility(View.GONE);       // landscape
                        scoreText.setVisibility(View.GONE);
                        redFlagText.setVisibility(View.GONE);
                        scoreFan.setVisibility(View.GONE);
                    }

                    feedbackButton.setVisibility(View.VISIBLE);
                    blinkScrollView.setScrollY(0);
                    detailsText.setText(getResources().getText(R.string.wrap_up_text));
                    finishUpButton.setText(getResources().getText(R.string.take_me_home));
                    emailresultsButton.setVisibility(View.GONE);
                    screenNumber = 2;
                    break;
                case 2:
                    Intent backHome = new Intent(this, IndexActivity.class);
                    backHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(backHome);
                    finish();
                    break;
                default:
                    //Log.e("ResultsActivity", "Should not happen");
                    break;
            }
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

    String getEmailMessage() {
        Resources res = getResources();

        // Today's date
        String todayDate =
                DateFormat.getDateInstance(DateFormat.LONG).format(Calendar.getInstance().getTime());

        // Level of depression in regular terms, not number
        String[] scoreEval = res.getStringArray(R.array.scoreEval);
        String scoreEvalResult;
        if(totalScore == 0) {
            scoreEvalResult = scoreEval[0];
        } else if(totalScore >= 1 && totalScore < 5) {
            scoreEvalResult = scoreEval[1];
        } else if(totalScore >= 5 && totalScore < 10) {
            scoreEvalResult = scoreEval[2];
        } else if(totalScore >= 10 && totalScore < 15) {
            scoreEvalResult = scoreEval[3];
        } else if(totalScore >= 15 && totalScore < 20) {
            scoreEvalResult = scoreEval[4];
        } else {
            scoreEvalResult = scoreEval[5];
        }

        // Red flag message
        String redFlagMessage = "";
        String[] emailRedFlag2 = res.getStringArray(R.array.email_red_flag_2);
        String[] emailRedFlag3 = res.getStringArray(R.array.email_red_flag_3);
        if (redFlag) {
            int lastTrueFlag = -1;

            // First sentence of red flag (priority to red flag 1)
            if (redFlagBitSet.get(1))
                redFlagMessage = res.getString(R.string.email_red_flag_1, emailRedFlag2[1]);
            else if (redFlagBitSet.get(0))
                redFlagMessage = res.getString(R.string.email_red_flag_1, emailRedFlag2[0]);
            else {
                for (lastTrueFlag = 2; lastTrueFlag < redFlagBitSet.size(); lastTrueFlag++)
                    if (redFlagBitSet.get(lastTrueFlag))
                        break;
                redFlagMessage = res.getString(R.string.email_red_flag_1, emailRedFlag2[lastTrueFlag]);
            }

            // Extra sentences if needed (only for red flags 2,3,4 (based on zero-index))
            if (lastTrueFlag == -1)
                lastTrueFlag = 2;
            else
                lastTrueFlag = lastTrueFlag + 1;
            for (; lastTrueFlag < redFlagBitSet.size(); lastTrueFlag++)
                if (redFlagBitSet.get(lastTrueFlag))
                    redFlagMessage += " " + emailRedFlag3[lastTrueFlag - 2]; // 2,3,4 -> 0,1,2
        }

        // Family or cultural background as cause?
        String[] emailFamOrCulture = res.getStringArray(R.array.email_family_or_cultural);
        String famOrCultureResult = "";
        if (!famOrCultureBitSet.isEmpty()) {
            if (!famOrCultureBitSet.get(0))
                famOrCultureResult = emailFamOrCulture[1];
            else if (!famOrCultureBitSet.get(1))
                famOrCultureResult = emailFamOrCulture[0];
            else
                famOrCultureResult = emailFamOrCulture[2];
        }

        // Family will understand if I tell them about depression?
        String[] emailFamily = res.getStringArray(R.array.email_family_understands);

        // Is willing or not willing to meet therapist
        String[] emailCanSee = res.getStringArray(R.array.email_can_see);

        return res.getString(R.string.email_message,
                todayDate, totalScore, scoreEvalResult, redFlagMessage, famOrCultureResult,
                emailFamily[familyUnderstandsAnswer], emailCanSee[canSeeAnswer]);
    }
}
