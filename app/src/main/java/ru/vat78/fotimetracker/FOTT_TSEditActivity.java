package ru.vat78.fotimetracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by vat on 14.12.2015.
 */
public class FOTT_TSEditActivity extends AppCompatActivity {

    FOTT_App app;

    private long now;
    private Spinner minutes;
    private Spinner hours;
    private TextView days;
    private TextView start_date;
    private TextView start_time;
    private TextView end_date;
    private TextView end_time;

    private Date start;
    private Date finish;
    long duration;
    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (FOTT_App) getApplication();

        now = System.currentTimeMillis();

        c = Calendar.getInstance();

        setContentView(R.layout.activity_timeslot_edit);

        Intent intent = getIntent();
        duration = intent.getLongExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_DURATION,15 * 60 * 1000);
        long l = intent.getLongExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_START,now - duration);
        start = new Date(l);
        l += duration;
        finish = new Date(l);

        minutes = (Spinner)  findViewById(R.id.tsAddMinutes);
        hours = (Spinner) findViewById(R.id.tsAddHours);
        days = (TextView) findViewById(R.id.tsDaysLabel);
        start_date = (TextView) findViewById(R.id.tsAddStartDate);
        start_time = (TextView) findViewById(R.id.tsAddStartTime);
        end_date = (TextView) findViewById(R.id.tsAddEndDate);
        end_time = (TextView) findViewById(R.id.tsAddEndTime);

        fillFields(true);

        AdapterView.OnItemSelectedListener onSelect = new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                setDuration();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        minutes.setOnItemSelectedListener(onSelect);
        hours.setOnItemSelectedListener(onSelect);


        Button cancel = (Button) findViewById(R.id.tsAddCancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        Button save = (Button) findViewById(R.id.tsAddSaveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView desc = (TextView) findViewById(R.id.tsAddDesc);

                Intent intent = new Intent();
                intent.putExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_START, start.getTime());
                intent.putExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_DURATION, duration);
                intent.putExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_DESC, desc.getText().toString());
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateChanges(v);
            }
        });

        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateChanges(v);
            }
        });

        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimeChanges(v);
            }
        });

        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimeChanges(v);
            }
        });
    }

    private void setStartAndFinish(){
        int[] d = convertDateToArray(end_date.getText());
        if (d[0] == 0){
            finish = new Date(now);
        } else {
            int[] t = convertTimeToArray(end_time.getText());
            c.set(d[0],d[1],d[2],t[0],t[1]);

            finish = c.getTime();
        }

        d = convertDateToArray(start_date.getText());
        if (d[0] == 0){
            start = new Date(now - duration);
        } else {
            int[] t = convertTimeToArray(start_time.getText());
            c.set(d[0],d[1],d[2],t[0],t[1]);

            start = c.getTime();
        }
    }

    private void setDuration(){

        int d=0;
        String s = days.getText().toString();
        if (!s.isEmpty()){
            s = s.substring(6);
            d = Integer.valueOf(s);
        }
        int h;
        if (hours.getSelectedItemPosition() >=0)
            h = Integer.valueOf(hours.getSelectedItem().toString());
        else
            h=0;

        int m;
        if (minutes.getSelectedItemPosition() >=0)
            m = Integer.valueOf(minutes.getSelectedItem().toString());
        else
            m = 0;

        duration = (long) ((d * 24 + h) * 60 + m) * 60 * 1000;
        fillFields(true);
    }

    private void fillFields(boolean useDuration){

        if (useDuration){
            if (finish == null) finish = new Date(now);
            start = new Date(finish.getTime() - duration);
        } else {
            duration = Math.round((finish.getTime() - start.getTime()));
            if (duration <= 0) duration = 0;
        }

        long l;
        if (duration >= 24 * 3600 * 1000) {
            int d = Math.round(duration / 24 / 3600 / 1000);
            days.setText("Days: " + d);
            l = duration - d * 24 * 3600 * 1000;
        } else {
            days.setText("");
            l = duration;
        }

        int h = Math.round(l / 3600 / 1000);
        l = l - h * 3600 * 1000;
        int m = Math.round(l / 60 / 1000);

        hours.setSelection(findPositionInSpinner(0,h));;
        minutes.setSelection(findPositionInSpinner(1,m));

        start_date.setText(app.getDateFormat().format(start));
        start_time.setText(app.getTimeFormat().format(start));
        end_date.setText(app.getDateFormat().format(finish));
        end_time.setText(app.getTimeFormat().format(finish));
    }

    private void onDateChanges(View v){

        final TextView date = (TextView) v;
        int[] dt = convertDateToArray(date.getText());
        new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year,monthOfYear,dayOfMonth);
                date.setText(app.getDateFormat().format(c.getTime()));
                setStartAndFinish();
                fillFields(false);
            }
        },dt[0],dt[1],dt[2]).show();
    }

    private int[] convertDateToArray(CharSequence date) {
        int[] res = new int[3];
        String s = date.toString();
        if (s.isEmpty()) return res;
        ParsePosition pos = new ParsePosition(0);
        Date d = app.getDateFormat().parse(s, pos);
        c.setTime(d);
        res[0] = c.get(Calendar.YEAR);
        res[1] = c.get(Calendar.MONTH);
        res[2] = c.get(Calendar.DAY_OF_MONTH);

        return res;
    }

    private void onTimeChanges(View v){

        final TextView time = (TextView) v;
        int[] dt = convertTimeToArray(time.getText());
        new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minute) {
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                time.setText(app.getTimeFormat().format(c.getTime()));
                setStartAndFinish();
                fillFields(false);
            }
        },dt[0],dt[1],true).show();
    }

    private int[] convertTimeToArray(CharSequence time) {
        int[] res = new int[2];
        String s = time.toString();
        if (s.isEmpty()) return res;
        ParsePosition pos = new ParsePosition(0);
        Date d = app.getTimeFormat().parse(s, pos);
        c.setTime(d);
        res[0] = c.get(Calendar.HOUR_OF_DAY);
        res[1] = c.get(Calendar.MINUTE);

        return res;
    }

    private int findPositionInSpinner(int type, int value) {
        int res = 0;
        int i = 0;
        if (value ==0) return res;

        String[] data;
        if (type == 0)
            data = getResources().getStringArray(R.array.addform_hours_values);
        else
            data = getResources().getStringArray(R.array.addform_minutes_values);
        for (String m: data) {
            i = Integer.valueOf(m);
            if (i >= value) break;
            res++;
        }
        if (i > value) res++;
        return res;
    }
}
