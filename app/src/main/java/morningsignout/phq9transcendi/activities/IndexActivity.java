package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.snapshot.Index;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import morningsignout.phq9transcendi.R;

public class IndexActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "PHQ9 Preference File";

    Button resourceButton, refButton, themeButton;

    private GoogleApiClient client;

    public static int sTheme = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up layout and toolbar
        Utils.SaveTheme("theme", sTheme, this);
        Utils.onActivityCreateSetTheme(this, sTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_index);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        handleLogin();

        // 3 Buttons: How am I doing, Resources, References
        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                beginQuiz();
            }
        });

        resourceButton = (Button) findViewById(R.id.resources_button);
        resourceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, ResourceActivity.class);
                startActivity(intent);
            }
        });

        refButton = (Button) findViewById(R.id.refButton);
        refButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, ReferenceActivity.class);
                startActivity(intent);
            }
        });

        themeButton = (Button) findViewById(R.id.themeButton);
        themeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sTheme = (sTheme + 1) % 2;
                Utils.SaveTheme("theme", sTheme, IndexActivity.this);
                Utils.changeToTheme(IndexActivity.this);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

        return super.onOptionsItemSelected(item);
    }

    private void beginQuiz() {
        Intent quizIntro = new Intent(this, QuizIntroActivity.class);
        startActivity(quizIntro);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Make both buttons the same width for aesthetic
        resourceButton.setWidth(refButton.getMeasuredWidth());
    }

    private void handleLogin() {
        final SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // if boolean exists, user does exist. If it doesn't, use anonymous login to get user id
        if (!preferences.contains(FirebaseExtras.HAS_LOGIN)) {
            Firebase ref = new Firebase(FirebaseExtras.DATA_URL);
            ref.authAnonymously(new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    // we've authenticated this session with your Firebase app
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putBoolean(FirebaseExtras.HAS_LOGIN, true);
                    editor.putString(FirebaseExtras.USER_ID, authData.getUid());
                    editor.putString(FirebaseExtras.PROVIDER, authData.getProvider());
                    editor.putString(FirebaseExtras.AUTH_TOKEN, authData.getToken());
                    editor.putLong(FirebaseExtras.EXPIRES, authData.getExpires());

                    editor.apply();

                    Log.d("IndexActivity", "User ID: " + authData.getUid());
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // there was an error, just don't send demographics
                    Log.e("PHQ9-Transcendi", "Failed connection to server.");
                }
            });
        } else {
            Log.d("IndexActivity", "Login already exists: " + preferences.getString(FirebaseExtras.USER_ID, ""));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Index Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://morningsignout.phq9transcendi/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Index Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://morningsignout.phq9transcendi/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }



}
