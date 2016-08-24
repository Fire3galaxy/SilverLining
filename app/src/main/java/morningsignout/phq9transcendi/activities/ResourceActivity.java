package morningsignout.phq9transcendi.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import morningsignout.phq9transcendi.R;

/**
 * Created by Stella on 5/1/2016.
 */
public class ResourceActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this, Utils.GetTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resource_view);

        Button homeButton = (Button)findViewById(R.id.back_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
