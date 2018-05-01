package morningsignout.phq9transcendi.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.HelperClasses.Utils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IndexActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up layout and toolbar
        Utils.onActivityCreateSetTheme(this, Utils.GetTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        System.out.println("Start button");
        // 2 Buttons: How am I doing,settings
        Button startButton = (Button) findViewById(R.id.startButton);
        System.out.println("startButton listener");
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                beginQuiz();
            }
        });
        System.out.println("settings button");

        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        System.out.println("Settings button listener");
        if (settingsButton == null) {
            System.out.println("Null button");

        }
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("SettingsActivity","HERE");
                Intent intent = new Intent(IndexActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        System.out.println("about us button");
        Button aboutUsButton = (Button) findViewById(R.id.aboutUsButton);
        System.out.println("about us Listener");
        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("IndexActivity", "HERE");
                Intent intent = new Intent(IndexActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });
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
