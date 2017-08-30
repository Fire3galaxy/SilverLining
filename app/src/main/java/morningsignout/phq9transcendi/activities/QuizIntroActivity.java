package morningsignout.phq9transcendi.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.HelperClasses.BlinkScrollView;
import morningsignout.phq9transcendi.HelperClasses.Utils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by pokeforce on 5/6/16.
 */
public class QuizIntroActivity extends AppCompatActivity {
    final static private int PORTRAIT_HEIGHT_DP = 100, LANDSCAPE_HEIGHT_DP = 70;

    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this, Utils.GetTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_intro);

        start = (Button) findViewById(R.id.button_start_quiz);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent demographics = new Intent(QuizIntroActivity.this, DemographicsIntroActivity.class);
                startActivity(demographics);
                QuizIntroActivity.this.finish();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Set height of start button based on orientation
        final float scale = getResources().getDisplayMetrics().density; // Get the screen's density scale

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            start.setHeight((int) (PORTRAIT_HEIGHT_DP * scale + 0.5f)); // Formula to convert dp to pixels
        else
            start.setHeight((int) (LANDSCAPE_HEIGHT_DP * scale + 0.5f));

        // Blink scrollbar to indicate scrolling is possible
        BlinkScrollView container = (BlinkScrollView) findViewById(R.id.container_quiz_intro);

        if (container.canScrollVertically())
            container.blinkScrollBar();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));   // For custom Rubik font
    }
}
