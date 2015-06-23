package appsandmaps.temple.edu.content_provider;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by nickcarmen on 6/19/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    @Override
    public void onReceive(Context context, Intent intent) {

        Cursor cur = context.getContentResolver().query(ContractClass.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String title = cur.getString(cur.getColumnIndex(ContractClass.FitNessTable.STEPS));

                if (Float.valueOf(title) < 5000) {

                    CalendarEvent calendarEvent = new CalendarEvent(context);

                    if(calendarEvent.isSlotAvailable()) {

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH");
                        String Time = sdf.format(c.getTime());
                        Log.d("This is the time: ", Time);
                        if (Integer.valueOf(Time) >= 8 && Integer.valueOf(Time) <= 21) {


                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Workout Alert")
                                    .setContentText("You are free for the next hour, take a walk");
                            Intent resultIntent = new Intent(context, MainActivity.class);
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addParentStack(MainActivity.class);
                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);
                            int mNotificationId = 001;
                            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                            mNotifyMgr.notify(mNotificationId, mBuilder.build());


                        }
                    }
                }else{
                    Toast.makeText(context, "I'm not running", Toast.LENGTH_LONG).show();

                }


            }

            ComponentName receiver = new ComponentName(context, ReBootNotif.class);
            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }

    }
    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
          alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                  AlarmManager.INTERVAL_HALF_HOUR,
                 AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
       // alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
       //         SystemClock.elapsedRealtime() +
       //                 60 * 1000 , alarmIntent);

    }
}
