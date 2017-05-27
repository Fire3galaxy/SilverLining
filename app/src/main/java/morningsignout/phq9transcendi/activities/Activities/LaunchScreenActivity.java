package morningsignout.phq9transcendi.activities.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.activities.HelperClasses.FirebaseExtras;
import morningsignout.phq9transcendi.activities.HelperClasses.Utils;

/**
 * Created by Daniel on 11/27/2016.
 */

public class LaunchScreenActivity extends Activity implements OnCompleteListener<AuthResult> {
    public static final String PREFS_NAME = "PHQ9 Preference File";
    long timeCreated;

    //FIXME: Next time, change onCreate to use different themed splash screen based on preferences
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
        handleLogin();
        timeCreated = System.currentTimeMillis();
    }

    private void handleLogin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            auth.signInAnonymously().addOnCompleteListener(this, this);
        } else {
            Log.d("LaunchScreenActivity", "Signed in already: " + user.getUid());
        }
    }

    void startApp() {
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(LaunchScreenActivity.this, IndexActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }, Math.max(3000 - (System.currentTimeMillis() - timeCreated), 0));
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful())
            updateThemePref();
        else
            Log.e("PHQ9-Transcendi", "Failed connection to server. " + task.getException().getMessage());
    }

    private void updateThemePref() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference themePref = FirebaseDatabase.getInstance()
                .getReference("users/" + userID + "/themePreference");
        themePref.setValue(Utils.THEME_NAMES[Utils.GetTheme(LaunchScreenActivity.this)]);
    }
}
