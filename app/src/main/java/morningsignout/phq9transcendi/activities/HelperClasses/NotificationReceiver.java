package morningsignout.phq9transcendi.activities.HelperClasses;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.activities.Activities.LaunchScreenActivity;
import morningsignout.phq9transcendi.activities.Activities.SettingsActivity;

/**
 * Created by jkapi on 6/24/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Quiz Reminder");
        //Acquire the lock
        wl.acquire();
        showNotification(context);
        //Release the lock
        wl.release();
    }


    public void showNotification(Context context) {
        int reqCode = 0; //unique number for the intent

        Intent intent = new Intent(context, LaunchScreenActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, reqCode, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.launch_icon_bw)
                .setContentTitle("Silver Lining")
                .setContentText("Would you like to take the PHQ-9 quiz?");

        // Note: After lollipop, android requires a certain kind of icon.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //mBuilder.setSmallIcon(R.drawable.icon_transparent);
        } else {
            mBuilder.setSmallIcon(R.drawable.launch_icon_bw);
        }

        // Example of long notification string. Notification should expand to allow longer text. But if this isn't supported
        // in older versions of Android (like JellyBean), then just short notification
                //.setContentText("It's good to regularly take the PHQ-9 to monitor yourself for symptoms of depression. Take the quiz now?");
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(reqCode, mBuilder.build());
    }
}
