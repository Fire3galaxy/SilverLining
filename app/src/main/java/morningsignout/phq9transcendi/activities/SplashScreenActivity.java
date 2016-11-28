package morningsignout.phq9transcendi.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by Daniel on 11/27/2016.
 */

public class SplashScreenActivity extends Activity {
    public static final String PREFS_NAME = "PHQ9 Preference File";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void handleLogin() {
        final SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final Firebase ref = new Firebase(FirebaseExtras.DATA_URL);

        // if boolean exists, user does exist. If it doesn't, use anonymous login to get user id
        if (!preferences.contains(FirebaseExtras.HAS_LOGIN)) {
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

                    //Log.d("IndexActivity", "User ID: " + authData.getUid());
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // there was an error, just don't send demographics
                    //Log.e("PHQ9-Transcendi", "Failed connection to server.");
                }
            });
        }
        else {
            ref.child(preferences.getString(FirebaseExtras.USER_ID, ""))    // Update theme preference
                    .child("themePreference")
                    .setValue(Utils.THEME_NAMES[Utils.GetTheme(this)]);
        }
    }
}
