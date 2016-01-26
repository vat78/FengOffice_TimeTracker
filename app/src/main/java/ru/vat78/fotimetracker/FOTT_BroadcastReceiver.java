package ru.vat78.fotimetracker;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;

import java.util.List;

import ru.vat78.fotimetracker.database.FOTT_DBMembers;
import ru.vat78.fotimetracker.database.FOTT_DBTasks;

/**
 * Created by vat on 12.01.2016.
 */
public class FOTT_BroadcastReceiver extends BroadcastReceiver {
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


        FOTT_App app = (FOTT_App) context.getApplicationContext();
        notification(app, intent);

        /*
        String c = intent.getStringExtra(BCommand);
        Intent i;
        if (app.getMainActivity() == null) {
            i = new Intent(app, FOTT_MainActivity.class);
        } else {
            i = new Intent(app.getMainActivity(), FOTT_MainActivity.class);
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra(BCommand, BC_TimerAlarm);
        app.startActivity(i);
        */
    }

    public void CancelAlarm(Context context)
    {
        Intent intent=new Intent(context, FOTT_BroadcastReceiver.class);
        PendingIntent sender= PendingIntent.getBroadcast(context,0, intent,0);
        AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);//Отменяем будильник, связанный с интентом данного класса
    }

    public void setOnetimeTimer(Context context, long alarmTime){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context, FOTT_BroadcastReceiver.class);
        intent.putExtra(BCommand, BC_TimerAlarm);//Задаем параметр интента
        PendingIntent pi= PendingIntent.getBroadcast(context,0, intent,0);
        am.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
    }

    private void notification(FOTT_App app, Intent intent){

        if (app.getCurTimeslot() == 0) return;
        String message = "You are working";
        if (app.getCurTask() != 0 ) {
            message += " on task '" + FOTT_DBTasks.getTaskById(app,app.getCurTask()).getName() + "'";
        }
        if (app.getCurMember() != 0) {
            message += " in category '" + FOTT_DBMembers.getMemberById(app,app.getCurMember()).getName() + "'";
        }
        message += ". Are you wish to stop this work?";

        Intent notificationIntent;
        if (app.getMainActivity() == null) {
            notificationIntent = new Intent(app, FOTT_MainActivity.class);
        } else {
            notificationIntent = new Intent(app.getMainActivity(), FOTT_MainActivity.class);
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra(BCommand, BC_TimerAlarm);

        PendingIntent contentIntent = PendingIntent.getActivity(app,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(app);

        builder.setContentIntent(contentIntent)
                .setTicker("Test")
                .setContentTitle("Alert")
                .setSmallIcon(R.drawable.ic_sync_24dp).setContentIntent(contentIntent)
                .setContentText(message)
                .addAction(new Notification.Action.Builder(R.drawable.ic_expand_more_24dp,"Finish",contentIntent).build())
                .addAction(new Notification.Action.Builder(R.drawable.ic_expand_more_24dp,"Continue",contentIntent).build());

        Notification notification = new Notification.BigTextStyle(builder).bigText(message).build();
        notification.defaults = Notification.DEFAULT_ALL;
        if (app.getPreferences().getBoolean(app.getString(R.string.pref_vibrate),false))
            notification.vibrate = new long[] { 200};
        if (!app.getPreferences().getString(app.getString(R.string.pref_ringtone),"").isEmpty())
            notification.sound = Uri.parse(app.getPreferences().getString(app.getString(R.string.pref_ringtone),""));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(app);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        //notificationManager.cancel(NOTIFY_ID);
        notificationManager.notify(NOTIFY_ID, notification);
    }
}
