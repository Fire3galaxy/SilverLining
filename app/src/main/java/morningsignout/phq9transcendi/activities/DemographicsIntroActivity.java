package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import morningsignout.phq9transcendi.R;

/**
 * Created by Stella on 3/20/2016.
 */
public class DemographicsIntroActivity extends AppCompatActivity {

    private Button continue_button;
    private Button skip_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.demographics_intro);
        continue_button = (Button) findViewById(R.id.button);
        skip_button = (Button) findViewById(R.id.button2);
        continue_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DemographicsIntroActivity.this, DemographicsActivity.class);
                startActivity(intent);
            }
        });
        skip_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DemographicsIntroActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });
    }
}
