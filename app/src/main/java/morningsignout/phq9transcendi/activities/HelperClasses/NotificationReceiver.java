package morningsignout.phq9transcendi.activities.HelperClasses;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.activities.Activities.LaunchScreenActivity;
import morningsignout.phq9transcendi.activities.Activities.SettingsActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jkapi on 6/24/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {
    //For notifications
    private static final int ALARM_ID = 3;
    final static String NOTIF_PREF = "Notification_Preferences";
    public final static String FREQ_PREF = "frequency";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Quiz Reminder");
        //Acquire the lock
        wl.acquire();

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            restartAlarm(context);
        } else {
            showNotification(context);
        }
        //Release the lock
        wl.release();
    }


    //This method changes what alarm is called.Stores preferences.
    public static void changeAlarmSettings(Context context, int frequency) {
        long timeSet = System.currentTimeMillis();
        //Store preferences for next time
        SharedPreferences.Editor editor = context.getSharedPreferences(NOTIF_PREF, MODE_PRIVATE).edit();
        editor.putInt(FREQ_PREF, frequency);
        editor.putLong("Time set", timeSet);
        editor.apply();

        // it correctly shows what the user chose
        switch (frequency) {
            case 0: //no alarm
                cancelAlarm(context, false, false);
                break;
            case 1: //weekly alarm
                cancelAlarm(context, true, false);
                setAlarm(context, AlarmManager.INTERVAL_DAY * 7, timeSet);
                Toast.makeText(context, "Weekly reminder is turned on", Toast.LENGTH_SHORT).show();
                break;
            case 2: //biweekly alarm
                cancelAlarm(context, true, false);
                setAlarm(context, AlarmManager.INTERVAL_DAY * 7 * 2, timeSet);
                Toast.makeText(context, "Biweekly reminder is turned on", Toast.LENGTH_SHORT).show();
                break;
            case 3: //tester
                cancelAlarm(context, true, false);
                setAlarm(context, 1000*60*5, timeSet);
                Toast.makeText(context, "Tester reminder is turned on", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /** Name: setAlarm
     * This method sets the notification schedule. Does not create a notification upon creation
     * of alarm. Next notification is timeSet+frequency. Since there is only one alarm, all
     * alarm intents are set with ALARM_ID as the id of the intent.
     * @param context - generally going to be SettingsActivity
     * @param frequency - frequency of Alarm.
     * @param timeSet - when alarm was set
     */
    public static void setAlarm(Context context, long frequency, long timeSet) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, timeSet + frequency,
                frequency, alarmIntent);

    }

    /** Name: cancelAlarm
     * Cancels alarm if alarm exists.
     * @param context
     * @param switching represents if method is being called due to user switching alarm modes from
     *                  weekly to biweekly or vice versa
     * @param restart repesents if method is being called due to phone having been rebooted
     */
    private static void cancelAlarm(Context context, boolean switching, boolean restart) {
        // If alarmIntent == null, app or activity has just been reopened.
        // Create the alarmIntent so that it may be canceled.
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0);

        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(alarmIntent);
        if (!switching && !restart) {
            Toast.makeText(context, "Reminder is turned off", Toast.LENGTH_SHORT).show();
        }
    }

    /** Name: restartAlarm
     *  Is called when phone is rebooted. Sets up the old alarm manager
     * @param context
     */
    private static void restartAlarm(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(NOTIF_PREF, Context.MODE_PRIVATE);
        int frequency = prefs.getInt(FREQ_PREF, 0);
        long timeSet = prefs.getLong("Time set",0);
        // it correctly shows what the user chose
        switch (frequency) {
            case 0: //no alarm
                cancelAlarm(context, false, true);
                break;
            case 1: //weekly alarm
                cancelAlarm(context, false, true);
                setAlarm(context, AlarmManager.INTERVAL_DAY * 7, timeSet);
                break;
            case 2: //biweekly alarm
                cancelAlarm(context, false, true);
                setAlarm(context, AlarmManager.INTERVAL_DAY * 7 * 2, timeSet);
                break;
            case 3: //tester
                cancelAlarm(context, false, true);
                setAlarm(context, 1000*60*5, timeSet);
                break;
            default:
                break;
        }

    }

    public void showNotification(Context context) {
        int reqCode = 0; //unique number for the intent

        Intent intent = new Intent(context, LaunchScreenActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, reqCode, intent, 0);
        String shortmessage = "Would you like to take the PHQ-9 quiz?";
        String longmessage = "It's good to regularly take the PHQ-9 to monitor yourself for symptoms of depression. Take the quiz now?";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Silver Lining Reminder")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(longmessage))
                .setContentText(longmessage);

        // Note: After lollipop, android requires a certain kind of icon. Therefore,
        //in app.gradle, targetSDKVersion is set to 20 for now

        mBuilder.setSmallIcon(R.drawable.ic_transparent);
        //mBuilder.setColor(context.getResources().getColor(R.color.icon_blue, null));
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(reqCode, mBuilder.build());
    }
}
