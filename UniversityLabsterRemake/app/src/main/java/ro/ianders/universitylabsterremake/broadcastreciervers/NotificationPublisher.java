package ro.ianders.universitylabsterremake.broadcastreciervers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ro.ianders.universitylabsterremake.MainActivity;
import ro.ianders.universitylabsterremake.R;

/**
 * Created by paul.iusztin on 16.01.2018.
 */

public class NotificationPublisher extends BroadcastReceiver{

    public static final int REQUEST_CODE_PENDINGINTENT_ACTIVITY = 0;
    public static final String NOTIFICATION = "notification";
    public static final String NOTIFICATION_ID = "notification_id";

    @Override
    public void onReceive(Context context, Intent intent) {

        //we receive the broadcast from the alarm manager and send the notification to the system
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        // we need the id so we can have multiple notification at the same time

        if(notificationManager != null)
            notificationManager.notify(id, notification);

    }

    public static Notification getNotification(String content, String title, int drawable, Context context) {

        Intent sentToActivityIntent = new Intent(context, MainActivity.class);
        sentToActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE_PENDINGINTENT_ACTIVITY, sentToActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // this for going to a certain activity when you click the notification

        //we build the notification with all it's characteristics
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(drawable)
                .setContentIntent(pendingIntent)
                .setLights( 0xff0000,200, 50)
                .setVibrate(new long[]{500, 100, 500, 100, 500, 100})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        return builder.build();
    }

    public static void scheduleNotification(Context context, Notification notification, int hour, String date, int request_code) {

        //we create the intent to the receiver
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION, notification);
        notificationIntent.putExtra(NOTIFICATION_ID, request_code);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, request_code, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // we create a different request_code for every broadcast so notifications won't overlap, therefor we wont lose any data in the update process
        // if the receiver is not fast enough the pending intent will be updated with another notification so the last notification will be lost ( in the case
        // we have only one request_code)

        String[] dates = date.split("/"); // dd/MM/yyyy
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Integer.parseInt(dates[2]));
        c.set(Calendar.MONTH, Integer.parseInt(dates[1]));
        c.set(Calendar.DAY_OF_WEEK, Integer.parseInt(dates[0]));
        c.set(Calendar.HOUR_OF_DAY , hour); // Calendar.HOUR_OF_DAY is used when you have a 24 hour per day system
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0); //if we don't set the seconds and the milliseconds it will take them from getInstance() and we don't want that
        c.set(Calendar.MILLISECOND, 0);

        // we put the pending intent in the alarm manager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(alarmManager != null)
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }

}
