package morningsignout.phq9transcendi.activities.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.activities.HelperClasses.NotificationReceiver;

public class SettingsActivity extends AppCompatActivity {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private boolean justCreated;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;
        justCreated = true; //this prevents the spinner from firing upon creation
        Spinner notification_spinner = (Spinner) findViewById(R.id.notification_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notifications_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notification_spinner.setAdapter(adapter);
        notification_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int frequency, long id) {

                //TODO store settings so that when page is opened again,
                // it correctly shows what the user chose
                if (justCreated) {
                    justCreated = false;
                    return;
                }
                Toast.makeText(context, "On selected", Toast.LENGTH_SHORT).show();
                switch (frequency) {
                    case 0: //no alarm
                        cancelAlarm(false);
                        break;
                    case 1: //weekly alarm
                        cancelAlarm(true);
                        setAlarm(alarmMgr.INTERVAL_DAY*7);
                        Toast.makeText(context, "Weekly reminder is turned on", Toast.LENGTH_SHORT).show();
                        break;
                    case 2: //biweekly alarm
                        cancelAlarm(true);
                        setAlarm(alarmMgr.INTERVAL_DAY*7*2);
                        Toast.makeText(context, "Biweekly reminder is turned on", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setAlarm(long frequency) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Set the alarm to start at 10:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 30);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                frequency, alarmIntent);

    }


    private void cancelAlarm(boolean switching) {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
            if (!switching) {
                Toast.makeText(context, "Reminder is turned off", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!switching) {
                Toast.makeText(context, "Reminder was already off", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
