package morningsignout.phq9transcendi.activities.HelperClasses;

/**
 * Created by pokeforce on 6/25/16.
 */
public class FirebaseExtras {
    private final static boolean DEBUG = true;
    private final static String DATA_URL_DEBUG = "https://android-silver-lining-debug.firebaseio.com/";
    private final static String DATA_URL = "https://android-phq-9-app.firebaseio.com/";
    public final static String HAS_LOGIN = "Has login";
    public final static String USER_ID = "User ID";
    public final static String PROVIDER = "Provider";
    public final static String AUTH_TOKEN = "Authentication token";
    public final static String EXPIRES = "Expiration";
    public final static String TEST_ID = "Test ID";

    public static String getDataURL() {
        if (DEBUG)
            return DATA_URL_DEBUG;
        return DATA_URL;
    }
}
