package morningsignout.phq9transcendi.activities;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import morningsignout.phq9transcendi.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

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
}
