package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import morningsignout.phq9transcendi.R;

public class MainActivity extends AppCompatActivity {

    private Button startButton, resrcButton, refButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startIntent();
            }
        });

        String page_type = "";
        resrcButton = (Button) findViewById(R.id.resrcButton);
        resrcButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResourceActivity.class);
                intent.putExtra("page_type", "Resources");
                startActivity(intent);
            }
        });

        refButton = (Button) findViewById(R.id.refButton);
        refButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResourceActivity.class);
                intent.putExtra("page_type", "References");
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startIntent() {
        Intent firstIntent = new Intent(this, DemographicsIntroActivity.class);
        startActivity(firstIntent);

    }
}
