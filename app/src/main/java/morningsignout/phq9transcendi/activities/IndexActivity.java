package morningsignout.phq9transcendi.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.HelperClasses.Utils;
import morningsignout.phq9transcendi.PHQApplication;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IndexActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up layout and toolbar
        Utils.onActivityCreateSetTheme(this, Utils.GetTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        // 2 Buttons: How am I doing,settings
        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                beginQuiz();
            }
        });
        Button settingsButton = (Button) findViewById(R.id.settingsButton);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button aboutUsButton = (Button) findViewById(R.id.aboutUsButton);
        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("IndexActivity", "HERE");
                Intent intent = new Intent(IndexActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        // Debug Mode: Special title to alert developer
        if (PHQApplication.USE_DEBUG_DB) {
            TextView title = (TextView) findViewById(R.id.title);
            title.setText(R.string.debug_app_name);
            title.setTextColor(Color.RED);
        }
    }

    private void beginQuiz() {
        Intent quizIntro = new Intent(this, QuizIntroActivity.class);
        startActivity(quizIntro);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));   // For custom Rubik font
    }
}
