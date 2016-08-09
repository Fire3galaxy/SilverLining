package morningsignout.phq9transcendi.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import morningsignout.phq9transcendi.R;

/**
 * Created by Stella on 8/3/2016.
 */

public class Utils {

    public final static int THEME_DEFAULT = 0;
    public final static int THEME_WHITE = 1;
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
    public static void onActivityCreateSetTheme(AppCompatActivity activity, int sTheme) {
        switch (sTheme) {

            case THEME_WHITE:
                activity.setTheme(R.style.CashmereAppTheme_NoActionBar);
                break;

            case THEME_DEFAULT:
            default:
                activity.setTheme(R.style.IceAppTheme_NoActionBar);
                break;

        }
    }

}
