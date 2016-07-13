package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import morningsignout.phq9transcendi.R;

/**
 * Created by Stella on 5/1/2016.
 */
public class ResourceActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resource_view);

        Button homeButton = (Button)findViewById(R.id.back_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ResourceActivity.this, IndexActivity.class);
                startActivity(intent);
            }
        });
    }
}
