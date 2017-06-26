package morningsignout.phq9transcendi.activities.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.activities.HelperClasses.NotificationReceiver;
import morningsignout.phq9transcendi.activities.HelperClasses.Utils;

public class SettingsActivity extends AppCompatActivity {
    private final int ALARM_ID = 3;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this, Utils.GetTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //populate the spinner with frequencies of notification
        final Spinner notification_spinner = (Spinner) findViewById(R.id.notification_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notifications_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notification_spinner.setAdapter(adapter);

        //Button
        Button notification_button = (Button) findViewById(R.id.notification_button);

        notification_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int frequency = notification_spinner.getSelectedItemPosition();

                //TODO store settings so that when page is opened again,
                // it correctly shows what the user chose

                switch (frequency) {
                    case 0: //no alarm
                        cancelAlarm(false);
                        break;
                    case 1: //weekly alarm
                        cancelAlarm(true);
                        setAlarm(alarmMgr.INTERVAL_DAY * 7);
                        Toast.makeText(context, "Weekly reminder is turned on", Toast.LENGTH_SHORT).show();
                        break;
                    case 2: //biweekly alarm
                        cancelAlarm(true);
                        setAlarm(alarmMgr.INTERVAL_DAY * 7 * 2);
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
        });

    }

    private void setAlarm(long frequency) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + frequency,
                frequency, alarmIntent);

    }


    private void cancelAlarm(boolean switching) {
        // If the alarm has been set, cancel it.

            if(alarmIntent == null) {
                Intent intent = new Intent(context, NotificationReceiver.class);
                alarmIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0);
            }

            alarmMgr.cancel(alarmIntent);
            if (!switching) {
                Toast.makeText(context, "Reminder is turned off", Toast.LENGTH_SHORT).show();
            }


    }



}
