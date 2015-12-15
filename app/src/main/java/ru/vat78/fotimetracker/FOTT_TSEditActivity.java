package ru.vat78.fotimetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by vat on 14.12.2015.
 */
public class FOTT_TSEditActivity extends AppCompatActivity {

    FOTT_App app;

    private long now;
    private Spinner minutes;
    private EditText hours;
    private TextView start_date;
    private TextView start_time;
    private TextView end_date;
    private TextView end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (FOTT_App) getApplication();

        now = System.currentTimeMillis();

        setContentView(R.layout.activity_timeslot_edit);

        Intent intent = getIntent();
        long duration = intent.getLongExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_DURATION,15);
        duration = 5 * Math.round(duration / 5);

        minutes = (Spinner)  findViewById(R.id.tsAddMinutes);
        hours = (EditText) findViewById(R.id.tsAddHours);
        start_date = (TextView) findViewById(R.id.tsAddStartDate);
        start_time = (TextView) findViewById(R.id.tsAddStartTime);
        end_date = (TextView) findViewById(R.id.tsAddEndDate);
        end_time = (TextView) findViewById(R.id.tsAddEndTime);

        minutes.setSelection(Math.round(duration / 5));

        minutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                fillFields(true);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        hours.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                fillFields(true);
                return true;
            }
        });


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

                long start = 0;
                long duration = 0;

                try {
                    String s = start_date.getText().toString();
                    if (s.isEmpty()) {start = 0;}
                    else {start = app.getDateFormat().parse(s).getTime();}

                    s = start_time.getText().toString();
                    if (!s.isEmpty()) {start += app.getTimeFormat().parse(s).getTime();}

                    int m = Integer.parseInt(minutes.getSelectedItem().toString());
                    int h = Integer.parseInt(hours.getText().toString());
                    duration = 60* (h * 60 + m);

                } catch (Exception e) {}

                TextView desc = (TextView) findViewById(R.id.tsAddDesc);

                Intent intent = new Intent();
                intent.putExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_START,start);
                intent.putExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_DURATION,duration);
                intent.putExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_DESC, desc.getText().toString());
                setResult(RESULT_OK, intent);

                //tsAdapter.saveNewTimeslot(start, duration, text);
                finish();
            }
        });

        fillFields(true);
    }

    private void fillFields(boolean useDuration){

        long start = 0;
        long finish = 0;
        long duration = 15;
        String s;


        try {
            s = start_date.getText().toString();
            if (s.isEmpty()) {start = 0;}
              else {start = app.getDateFormat().parse(s).getTime();}

            s = start_time.getText().toString();
            if (!s.isEmpty()) {start += app.getTimeFormat().parse(s).getTime();}

            s  = end_date.getText().toString();
            if (s.isEmpty()) {finish = 0;}
              else {finish = app.getDateFormat().parse(s).getTime();}

            s = end_time.getText().toString();
            if (!s.isEmpty()) {finish += app.getTimeFormat().parse(s).getTime();}

            int m = Integer.parseInt(minutes.getSelectedItem().toString());
            int h = Integer.parseInt(hours.getText().toString());
            duration = h * 60 + m;

        } catch (Exception e) {}

        if (useDuration){
            if (finish == 0) finish = now;
            start = finish - duration * 60 * 1000;
        } else {
            duration = Math.round((finish - start)/1000);
            if (duration <= 0) duration = 1;
        }

        int h = Math.round(duration / 60);
        int m = Math.round((duration - h*60) / 5);

        hours.setText(String.valueOf(h));
        minutes.setSelection(m);

        start_date.setText(app.getDateFormat().format(new Date(start)));
        start_time.setText(app.getTimeFormat().format(new Date(start)));
        end_date.setText(app.getDateFormat().format(new Date(finish)));
        end_time.setText(app.getTimeFormat().format(new Date(finish)));

    }
}
