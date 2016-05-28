package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import morningsignout.phq9transcendi.R;

/**
 * Created by pokeforce on 5/6/16.
 */
public class QuizIntroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_intro);

        Button start = (Button) findViewById(R.id.button_start_quiz);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent demographics = new Intent(QuizIntroActivity.this, DemographicsIntroActivity.class);
                startActivity(demographics);
            }
        });
    }
}
