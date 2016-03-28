package morningsignout.phq9transcendi.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Michael on 3/27/2016.
 */
public class DBSource {

    SQLiteOpenHelper dbhelper;
    SQLiteDatabase db;

    public DBSource(Context context) { dbhelper = new DBHelper(context); }

    public void open() {
        db = dbhelper.getWritableDatabase();
    }

    public void close() {
        dbhelper.close();
    }

    public boolean createNewUser(String username, String password) {
        if(!userExists(username)) {
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            values.put("email", "");
            return true;
        }
        return false;
    }

    public boolean createNewUser(String username, String password, String email) {
        if(!userExists(username)) {
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            values.put("email", email);
        }
        return false;
    }

    public boolean userExists(String username) {
        return true;
    }

}
