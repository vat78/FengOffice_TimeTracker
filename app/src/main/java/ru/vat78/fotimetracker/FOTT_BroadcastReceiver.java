package ru.vat78.fotimetracker;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by vat on 12.01.2016.
 */
public class FOTT_BroadcastReceiver extends BroadcastReceiver {
    public static final String BCommand = "ru.vat78.fotimetracker.COMMAND";
    public static final String BC_TimerAlarm = "ru.vat78.fotimetracker.ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {

        /*
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        String s = componentInfo.getPackageName();
        */

        FOTT_App app = (FOTT_App) context.getApplicationContext();
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

        /*
        if (app.getMainActivity() == null) {
            Intent i = new Intent(app, FOTT_MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(BCommand, BC_TimerAlarm);
            app.startActivity(i);

        } else {
            app.getMainActivity().alarmDialogShow();
        }
        */
    }

    public void SetAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context, FOTT_BroadcastReceiver.class);
        intent.putExtra(BCommand, BC_TimerAlarm);//Задаем параметр интента
        PendingIntent pi= PendingIntent.getBroadcast(context,0, intent,0);
//Устанавливаем интервал срабатывания в 5 секунд.
        am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),1000*5,pi);
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
        am.set(AlarmManager.RTC_WAKEUP,alarmTime,pi);
    }
}
