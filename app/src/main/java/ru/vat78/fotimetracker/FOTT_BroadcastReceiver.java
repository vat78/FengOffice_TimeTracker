package ru.vat78.fotimetracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by vat on 12.01.2016.
 */
public class FOTT_BroadcastReceiver extends BroadcastReceiver {
    public static final String BCommand = "command";
    public static final String BC_TimerAlarm = "alarm";

    @Override
    public void onReceive(Context context, Intent intent) {

        String c = intent.getStringExtra(BCommand);
        Intent i = new Intent();
        i.setClassName(context, "ru.vat78.fotimetracker.FOTT_MainActivity");
        intent.putExtra(BCommand, BC_TimerAlarm);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
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
