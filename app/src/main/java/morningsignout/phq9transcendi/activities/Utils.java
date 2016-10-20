package morningsignout.phq9transcendi.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import morningsignout.phq9transcendi.R;

/**
 * Created by Stella on 8/3/2016.
 */

public class Utils {


    public final static int THEME_DEFAULT = 0;
    public final static int THEME_CASH = 1;

    public final static String[] THEME_NAMES = {"ice", "cashmere"};

    //public final static int THEME_BLUE = 2;
    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity)
    {
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity, int sTheme) {
        switch (sTheme) {

            case THEME_CASH:
                activity.setTheme(R.style.CashmereAppTheme_NoActionBar);
                break;

            case THEME_DEFAULT:
            default:
                activity.setTheme(R.style.IceAppTheme_NoActionBar);
                break;

        }
    }

    public static void SaveTheme(String key, int theme, Activity activity){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putInt(key, theme);
        editor.apply();
    }

    public static int GetTheme(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("theme", 0);
    }



}
