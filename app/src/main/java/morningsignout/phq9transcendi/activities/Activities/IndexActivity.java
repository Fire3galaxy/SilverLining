package morningsignout.phq9transcendi.activities.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.activities.HelperClasses.Themes;
import morningsignout.phq9transcendi.activities.HelperClasses.Utils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IndexActivity extends AppCompatActivity {
    Button resourceButton, refButton, themeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up layout and toolbar
        Utils.onActivityCreateSetTheme(this, Utils.GetTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

//        handleLogin(); // Gives user a firebase id

        // 2 Buttons: How am I doing, Theme
        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                beginQuiz();
            }
        });

        refButton = (Button) findViewById(R.id.themeButton);
        refButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, Themes.class);
                startActivity(intent);
            }
        });

//        themeButton = (Button) findViewById(R.id.themeButton);
//        themeButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                Intent intent = new Intent(IndexActivity.this, Themes.class);
//                startActivity(intent);
//            }
//        });
    }

    private void beginQuiz() {
        Intent quizIntro = new Intent(this, QuizIntroActivity.class);
        startActivity(quizIntro);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Make both buttons the same width for aesthetic
        //resourceButton.setWidth(refButton.getMeasuredWidth());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));   // For custom Rubik font
    }

//    private void handleLogin() {
//        final SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        final Firebase ref = new Firebase(FirebaseExtras.DATA_URL);
//
//        // if boolean exists, user does exist. If it doesn't, use anonymous login to get user id
//        if (!preferences.contains(FirebaseExtras.HAS_LOGIN)) {
//            ref.authAnonymously(new Firebase.AuthResultHandler() {
//                @Override
//                public void onAuthenticated(AuthData authData) {
//                    // we've authenticated this session with your Firebase app
//                    SharedPreferences.Editor editor = preferences.edit();
//
//                    editor.putBoolean(FirebaseExtras.HAS_LOGIN, true);
//                    editor.putString(FirebaseExtras.USER_ID, authData.getUid());
//                    editor.putString(FirebaseExtras.PROVIDER, authData.getProvider());
//                    editor.putString(FirebaseExtras.AUTH_TOKEN, authData.getToken());
//                    editor.putLong(FirebaseExtras.EXPIRES, authData.getExpires());
//
//                    editor.apply();
//
//                    //Log.d("IndexActivity", "User ID: " + authData.getUid());
//                }
//
//                @Override
//                public void onAuthenticationError(FirebaseError firebaseError) {
//                    // there was an error, just don't send demographics
//                    //Log.e("PHQ9-Transcendi", "Failed connection to server.");
//                }
//            });
//        }
//        else {
//            ref.child(preferences.getString(FirebaseExtras.USER_ID, ""))    // Update theme preference
//                    .child("themePreference")
//                    .setValue(Utils.THEME_NAMES[Utils.GetTheme(this)]);
//        }
//    }
}
