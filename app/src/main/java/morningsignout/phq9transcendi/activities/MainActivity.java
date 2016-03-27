package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Text;

import morningsignout.phq9transcendi.R;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, newButton, quickButton;
    private EditText username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startIntent();
            }
        });

        String page_type = "";
        newButton = (Button) findViewById(R.id.newButton);
        newButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewUserActivity.class);
                startActivity(intent);
            }
        });

        quickButton = (Button) findViewById(R.id.quickButton);
        quickButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DemographicsIntroActivity.class);
                startActivity(intent);
            }
        });

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        username.setHint("Username");
        password.setHint("Password");

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
        Intent firstIntent = new Intent(this, IndexActivity.class);
        startActivity(firstIntent);
    }
}
