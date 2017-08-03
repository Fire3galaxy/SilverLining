package morningsignout.phq9transcendi.activities.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.activities.HelperClasses.NotificationReceiver;
import morningsignout.phq9transcendi.activities.HelperClasses.ThemesAdapter;
import morningsignout.phq9transcendi.activities.HelperClasses.Utils;
import morningsignout.phq9transcendi.activities.PHQApplication;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatActivity {
    Context context;

    //For notifications
    final static String NOTIF_PREF = "Notification_Preferences";

    //for themes settings
    ListView contents;
    ThemesAdapter themeCustomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this, Utils.GetTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;

        //populate the spinner with frequencies of notification
        final Spinner notification_spinner = (Spinner) findViewById(R.id.notification_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notifications_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notification_spinner.setAdapter(adapter);

        //set the default spinner text to the saved preferences
        SharedPreferences prefs = getSharedPreferences(NOTIF_PREF, MODE_PRIVATE);
        int savedFreq = prefs.getInt("frequency", 0);
        if (savedFreq != 0) { //now change spinner
            notification_spinner.setSelection(savedFreq);
        }

        //Set up notification Button and button listener (ok)
        Button notification_button = (Button) findViewById(R.id.notification_button);
        notification_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int frequency = notification_spinner.getSelectedItemPosition();
                NotificationReceiver.changeAlarmSettings(context, frequency);
            }
        });


        // --- SET UP THEMES SETTINGS ---
/*        contents = (ListView)findViewById(R.id.Contents);
        contents.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Utils.SaveTheme("theme", position, SettingsActivity.this);
                Utils.changeToTheme(SettingsActivity.this);
                updateThemePref();
            }
        });
*/

        final Spinner themes_spinner = (Spinner) findViewById(R.id.themes_spinner);
        ArrayAdapter<CharSequence> themesadapter = ArrayAdapter.createFromResource(this,
                R.array.themes, android.R.layout.simple_spinner_item);
        themesadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themes_spinner.setAdapter(themesadapter);

        //set the default spinner text to the saved preferences
        int savedTheme = prefs.getInt("theme", 0);
        if (savedTheme != 0) { //now change spinner
            themes_spinner.setSelection(savedTheme);
        }

        //Set up notification Button and button listener (ok)
        Button themes_button = (Button) findViewById(R.id.themes_button);
        themes_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int theme = themes_spinner.getSelectedItemPosition();

                //store theme
                SharedPreferences.Editor editor = context.getSharedPreferences(NOTIF_PREF, MODE_PRIVATE).edit();
                editor.putInt("theme", theme);
                editor.apply();

                //Set the theme
                Utils.SaveTheme("theme", theme, SettingsActivity.this);
                Utils.changeToTheme(SettingsActivity.this);
                updateThemePref();
            }
        });


        // Create the Custom Adapter Object
        themeCustomAdapter = new ThemesAdapter(this);

        //Set the Adapter
        //contents.setAdapter(themeCustomAdapter);
    }




    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));   // For custom Rubik font
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, IndexActivity.class);
        startActivity(intent);
        finish();
    }

    //Themes are stored in firebase
    private void updateThemePref() {
        String userID = FirebaseAuth.getInstance(PHQApplication.getFirebaseAppInstance())
                .getCurrentUser().getUid();
        DatabaseReference themePref = FirebaseDatabase.getInstance(PHQApplication.getFirebaseAppInstance())
                .getReference("users/" + userID + "/themePreference");
        themePref.setValue(Utils.THEME_NAMES[Utils.GetTheme(this)]);
    }

}
