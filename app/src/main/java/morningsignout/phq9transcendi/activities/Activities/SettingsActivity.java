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
    private final int ALARM_ID = 3;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    String NOTIF_PREF = "Notification_Preferences";

    //for themes settings
    ListView contents;
    Button homeButton;
    ThemesAdapter themeCustomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this, Utils.GetTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;

        //----- SET UP ALARMS SETTING -----
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

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
                changeAlarmSettings(frequency);
            }
        });


        // --- SET UP THEMES SETTINGS ---
        contents = (ListView)findViewById(R.id.Contents);
        contents.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Utils.SaveTheme("theme", position, SettingsActivity.this);
                Utils.changeToTheme(SettingsActivity.this);
                updateThemePref();
            }
        });

        homeButton = (Button)findViewById(R.id.back_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, IndexActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Create the Custom Adapter Object
        themeCustomAdapter = new ThemesAdapter(this);

        //Set the Adapter
        contents.setAdapter(themeCustomAdapter);
    }

    //This method changes what alarm is called.Stores preferences.
    private void changeAlarmSettings(int frequency) {
        //Store preferences for next time
        SharedPreferences.Editor editor = getSharedPreferences(NOTIF_PREF, MODE_PRIVATE).edit();
        editor.putInt("frequency", frequency);
        editor.apply();

        // it correctly shows what the user chose
        switch (frequency) {
            case 0: //no alarm
                cancelAlarm(false);
                break;
            case 1: //weekly alarm
                cancelAlarm(true);
                setAlarm(AlarmManager.INTERVAL_DAY * 7);
                Toast.makeText(context, "Weekly reminder is turned on", Toast.LENGTH_SHORT).show();
                break;
            case 2: //biweekly alarm
                cancelAlarm(true);
                setAlarm(AlarmManager.INTERVAL_DAY * 7 * 2);
                Toast.makeText(context, "Biweekly reminder is turned on", Toast.LENGTH_SHORT).show();
                break;
            case 3: //tester
                cancelAlarm(true);
                setAlarm(1000*30);
                Toast.makeText(context, "Tester reminder is turned on", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }


    }

    private void setAlarm(long frequency) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + frequency,
                frequency, alarmIntent);

    }


    private void cancelAlarm(boolean switching) {
            // If alarmIntent == null, app or activity has just been reopened.
            // Create the alarmIntent so that it may be canceled.
            if(alarmIntent == null) {
                Intent intent = new Intent(context, NotificationReceiver.class);
                alarmIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0);
            }

            alarmMgr.cancel(alarmIntent);
            if (!switching) {
                Toast.makeText(context, "Reminder is turned off", Toast.LENGTH_SHORT).show();
            }
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
