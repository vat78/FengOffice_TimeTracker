package ru.vat78.fotimetracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;


/**
 * Created by vat on 12.01.2016.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    public static final String BCommand = "ru.vat78.fotimetracker.COMMAND";
    public static final String BC_TimerAlarm = "ru.vat78.fotimetracker.ALARM";
    public static final int NOTIFY_ID = 78;

    @Override
    public void onReceive(Context context, Intent intent) {

        /*
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        String s = componentInfo.getPackageName();
        */


        App app = (App) context.getApplicationContext();
        notification(app, intent);

        String c = intent.getStringExtra(BCommand);
        Intent i;
        if (app.getMainActivity() == null) {
            i = new Intent(app, MainActivity.class);
        } else {
            i = new Intent(app.getMainActivity(), MainActivity.class);
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra(BCommand, BC_TimerAlarm);
        app.startActivity(i);
    }

    public void CancelAlarm(Context context)
    {
        Intent intent=new Intent(context, MyBroadcastReceiver.class);
        PendingIntent sender= PendingIntent.getBroadcast(context,0, intent,0);
        AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);//Отменяем будильник, связанный с интентом данного класса
    }

    public void setOnetimeTimer(Context context){
        CancelAlarm(context);
        App app = (App) context.getApplicationContext();
        long l = Long.parseLong(app.getPreferences().getString(app.getString(R.string.pref_alert_frequency), "-1"));
        if (l>0) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, MyBroadcastReceiver.class);
            intent.putExtra(BCommand, BC_TimerAlarm);//Задаем параметр интента
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + l * 60 * 1000, pi);
        }
    }

    private void notification(App app, Intent intent){

        if (app.getCurTimeslot() == 0) return;
        String message = app.getString(R.string.alert_title);

        Intent notificationIntent;
        if (app.getMainActivity() == null) {
            notificationIntent = new Intent(app, MainActivity.class);
        } else {
            notificationIntent = new Intent(app.getMainActivity(), MainActivity.class);
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(app,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(app);

        builder.setContentIntent(contentIntent)
                .setTicker(app.getString(R.string.alert_title))
                .setContentTitle(app.getString(R.string.alert_title))
                .setContentText(message);

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_ALL;
        if (app.getPreferences().getBoolean(app.getString(R.string.pref_vibrate),false))
            notification.vibrate = new long[] {500};
        if (!app.getPreferences().getString(app.getString(R.string.pref_ringtone),"").isEmpty())
            notification.sound = Uri.parse(app.getPreferences().getString(app.getString(R.string.pref_ringtone),""));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(app);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        //notificationManager.cancel(NOTIFY_ID);
        notificationManager.notify(NOTIFY_ID, notification);
    }
}
