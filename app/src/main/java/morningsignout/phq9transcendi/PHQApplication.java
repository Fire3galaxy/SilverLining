package morningsignout.phq9transcendi;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by pokeforce on 7/10/16.
 */
public class PHQApplication extends Application {
    private static PHQApplication singleton;

    private static final boolean USE_DEBUG_DB = true; // See comment for getFirebaseAppInstance()

    public static PHQApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()   // Change default font for activities with wrapped context
                .setDefaultFontPath("Rubik-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    /* When using Firebase, the default instance uses the google.json file in the app module
     * But we need to use the debugging database instead if we're testing things. Always use the
     * USE_DEBUG_DB boolean to toggle between using the debug database and the public one.
     */
    static public FirebaseApp getFirebaseAppInstance() {
        if (USE_DEBUG_DB) {
            // Check list of existing databases in app
            boolean debugExists = false;
            for (FirebaseApp app : FirebaseApp.getApps(getInstance())) {
                if (app.getName().equals("DEBUG")) {
                    debugExists = true;
                    break;
                }
            }

            if (!debugExists) {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApiKey("AIzaSyDyD4M9-vTisSMBrxSN83uf4bQQnoH4LQ4")
                        .setApplicationId("1:389035711724:android:33e4a0d819b5b631")
                        .setDatabaseUrl("https://android-silver-lining-debug.firebaseio.com")
                        .build();
                return FirebaseApp.initializeApp(getInstance(), options, "DEBUG");
            }

            return FirebaseApp.getInstance("DEBUG");
        } else {
            /* FIXME: For some reason, google-services.json isn't loading this database
             * automatically. For now, just creating database from scratch.*/

            // Check list of existing databases in app
            boolean defaultExists = false;
            for (FirebaseApp app : FirebaseApp.getApps(getInstance())) {
                if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                    defaultExists = true;
                    break;
                }
            }

            if (!defaultExists) {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApiKey("AIzaSyAFRxgV1UO8n_4-AWrchXnup8PiUsl_qgs")
                        .setApplicationId("1:633092681994:android:33e4a0d819b5b631")
                        .setDatabaseUrl("https://android-phq-9-app.firebaseio.com")
                        .build();
                return FirebaseApp.initializeApp(getInstance(), options, FirebaseApp.DEFAULT_APP_NAME);
            }

            return FirebaseApp.getInstance(FirebaseApp.DEFAULT_APP_NAME);
        }
    }
}
