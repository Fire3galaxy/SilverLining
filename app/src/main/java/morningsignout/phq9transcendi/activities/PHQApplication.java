package morningsignout.phq9transcendi.activities;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.firebase.client.Firebase;

/**
 * Created by pokeforce on 7/10/16.
 */
public class PHQApplication extends Application {
    private static PHQApplication singleton;

    public static PHQApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        Firebase.setAndroidContext(this);                           // ----Firebase setup----
        Firebase.getDefaultConfig().setPersistenceEnabled(true);    // Keep sync data if app closes
    }

    // To avoid the 64K Build limit on my emulator
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
