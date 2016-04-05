package ru.vat78.fotimetracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;

import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.model.FOTT_TaskBuilder;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
import ru.vat78.fotimetracker.model.FOTT_TimeslotBuilder;
import ru.vat78.fotimetracker.views.FOTT_Parcel;

public class FOTT_TSEditActivity extends Activity {

    FOTT_App app;

    private FOTT_Timeslot ts;
    private FOTT_Task task;

    private long now;
    private Spinner vMinutes;
    private Spinner vHours;
    private TextView vDays;
    private TextView vStartDate;
    private TextView vStartTime;
    private TextView vEndDate;
    private TextView vEndTime;
    private TextView vTSDesc;

    private boolean tmove;
    private boolean tclose;

    private Calendar c;

    private Intent result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        app = (FOTT_App) getApplication();

        now = System.currentTimeMillis();

        c = Calendar.getInstance();

        setContentView(R.layout.activity_timeslot_edit);

        vMinutes = (Spinner)  findViewById(R.id.tsAddMinutes);
        vHours = (Spinner) findViewById(R.id.tsAddHours);
        vDays = (TextView) findViewById(R.id.tsDaysLabel);
        vStartDate = (TextView) findViewById(R.id.tsAddStartDate);
        vStartTime = (TextView) findViewById(R.id.tsAddStartTime);
        vEndDate = (TextView) findViewById(R.id.tsAddEndDate);
        vEndTime = (TextView) findViewById(R.id.tsAddEndTime);
        vTSDesc = (TextView) findViewById(R.id.tsAddDesc);

        getInitialData();
        vTSDesc.setText(ts.getDesc());

        fillTSFields();
        prepareTaskArea();

        AdapterView.OnItemSelectedListener onSelect = new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                getDurationFromForm();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                getDurationFromForm();
            }
        };

        vMinutes.setOnItemSelectedListener(onSelect);
        vHours.setOnItemSelectedListener(onSelect);


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

                saveMadeChanges();
            }
        });

        vStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateChanges(v);
            }
        });

        vEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateChanges(v);
            }
        });

        vStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimeChanges(v);
            }
        });

        vEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimeChanges(v);
            }
        });

    }

    private void getInitialData() {

        Intent intent = getIntent();
        ts = FOTT_Parcel.unpackTimeslot(intent.getStringArrayExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_TIMESLOT)).buildObject();

        task = FOTT_Parcel.unpackTask(intent.getStringArrayExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_TASK)).buildObject();

        tclose = intent.getBooleanExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_CLOSE_TASK, false);
        tmove = intent.getBooleanExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_MOVE_TASK, false);
    }


    private void prepareTaskArea() {

        RelativeLayout vTaskEdit = (RelativeLayout) findViewById(R.id.tsAddTaskArea);
        if ((tclose || tmove) && task.getName() != null) {
            vTaskEdit.setVisibility(View.VISIBLE);
            TextView vTaskCloseDesc = (TextView) findViewById(R.id.tsAddTaskCompleteDesc);
            CheckBox vTaskClose = (CheckBox) findViewById(R.id.tsAddTaskComplete);
            if (tclose) {
                vTaskClose.setVisibility(View.VISIBLE);
                vTaskCloseDesc.setVisibility(View.VISIBLE);
                vTaskClose.setChecked(task.getStatus() == FOTT_Task.STATUS_COMPLETED);
            } else {
                vTaskClose.setVisibility(View.INVISIBLE);
                vTaskCloseDesc.setVisibility(View.INVISIBLE);
            }

            TextView vTaskMove = (TextView) findViewById(R.id.tsAddTaskDue);
            TextView vTaskMoveDesc = (TextView) findViewById(R.id.tsAddTaskDueDesc);
            if (tmove) {
                vTaskMove.setVisibility(View.VISIBLE);
                vTaskMoveDesc.setVisibility(View.VISIBLE);
                vTaskMove.setText(app.getDateFormat().format(task.getDueDate()));
                vTaskMove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDateChanges(v);
                    }
                });

            } else {
                vTaskMove.setVisibility(View.GONE);
                vTaskMoveDesc.setVisibility(View.GONE);
            }

        } else vTaskEdit.setVisibility(View.GONE);
    }


    private void saveMadeChanges() {

        FOTT_TimeslotBuilder tsb = new FOTT_TimeslotBuilder(ts);
        tsb.setDesc(vTSDesc.getText().toString());

        result = new Intent();
        result.putExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_TIMESLOT, FOTT_Parcel.parcelTimeslot(tsb.buildObject()));

        if ((tclose || tmove) && task.getName() != null) {
            handleTaskChanges();
        }
        else {
            setResult(RESULT_OK, result);
            finish();
        }
    }

    private void handleTaskChanges() {

        int newTaskStatus = getTaskStatusFromForm();
        long newTaskDueDate = getTaskDueDateFromForm();
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(newTaskDueDate);
        long tzone = (long) cl.getTimeZone().getOffset(newTaskDueDate);

        StringBuffer message = new StringBuffer();
        if (newTaskStatus != task.getStatus()) {
            message.append(getString(R.string.addform_close_question));
            if (newTaskStatus == FOTT_Task.STATUS_ACTIVE) {
                message.append(getString(R.string.addform_resume));
            } else {
                message.append(getString(R.string.addform_finish));
            }
            message.append(getString(R.string.addform_task))
                .append(task.getName())
                .append(getString(R.string.addform_qestion));
        }
        else if (newTaskDueDate != task.getDueDate().getTime() && newTaskDueDate != (task.getDueDate().getTime() - tzone)) {
            message.append(getString(R.string.addform_move_date))
                .append(app.getDateFormat().format(new Date(newTaskDueDate)))
                .append(getString(R.string.addform_for_task))
                .append(task.getName())
                .append(getString(R.string.addform_qestion));
        }
        showConfirmationTaskDialog(message.toString());
    }

    private int getTaskStatusFromForm() {

        int result = task.getStatus();

        CheckBox vTaskClose;
        if (tclose) {
            vTaskClose = (CheckBox) findViewById(R.id.tsAddTaskComplete);
            if (vTaskClose.isChecked()) {
                result = FOTT_Task.STATUS_COMPLETED;
            } else {
                result = FOTT_Task.STATUS_ACTIVE;
            }
        }
        return result;
    }

    private long getTaskDueDateFromForm() {

        Date result = task.getDueDate();
        TextView vTaskDue = (TextView) findViewById(R.id.tsAddTaskDue);

        if (tmove){
            ParsePosition pos = new ParsePosition(0);
            result = app.getDateFormat().parse(vTaskDue.getText().toString(), pos);
        }
        return result.getTime();
    }

    private void getStartAndFinishFromForm(){

        FOTT_TimeslotBuilder newTS = new FOTT_TimeslotBuilder(ts);
        Date start;
        Date finish;

        int[] d = convertDateToArray(vEndDate.getText());
        if (d[0] == 0){
            finish = new Date(now);
        } else {
            int[] t = convertTimeToArray(vEndTime.getText());
            c.set(d[0],d[1],d[2],t[0],t[1]);

            finish = c.getTime();
        }

        d = convertDateToArray(vStartDate.getText());
        if (d[0] == 0){
            start = new Date(now - ts.getDuration());
        } else {
            int[] t = convertTimeToArray(vStartTime.getText());
            c.set(d[0],d[1],d[2],t[0],t[1]);

            start = c.getTime();
        }

        newTS.setStart(start);
        newTS.setDuration(finish.getTime() - start.getTime());
        ts = newTS.buildObject();
    }

    private void getDurationFromForm(){

        Date start;
        Date finish = new Date(ts.getStart().getTime() + ts.getDuration());
        long duration;

        int d=0;
        String s = vDays.getText().toString();
        if (!s.isEmpty()){
            s = s.substring(getString(R.string.addform_days).length());
            d = Integer.valueOf(s);
        }
        int h;
        if (vHours.getSelectedItemPosition() >=0)
            h = Integer.valueOf(vHours.getSelectedItem().toString());
        else
            h=0;

        int m;
        if (vMinutes.getSelectedItemPosition() >=0)
            m = Integer.valueOf(vMinutes.getSelectedItem().toString());
        else
            m = 0;

        duration = (long) ((d * 24 + h) * 60 + m) * 60 * 1000;
        start = new Date(finish.getTime() - duration);

        FOTT_TimeslotBuilder newTS = new FOTT_TimeslotBuilder(ts);
        newTS.setStart(start);
        newTS.setDuration(duration);
        ts = newTS.buildObject();
        fillTSFields();
    }

    private void fillTSFields(){

        Date start = ts.getStart();
        Date finish = new Date(ts.getStart().getTime() + ts.getDuration());

        fillDurationFields(ts.getDuration());

        vStartDate.setText(app.getDateFormat().format(start));
        vStartTime.setText(app.getTimeFormat().format(start));
        vEndDate.setText(app.getDateFormat().format(finish));
        vEndTime.setText(app.getTimeFormat().format(finish));
    }

    private void fillDurationFields(long duration) {

        long tmp;
        if (duration >= 24 * 3600 * 1000) {
            int d = Math.round(duration / 24 / 3600 / 1000);
            vDays.setText(getString(R.string.addform_days) + d);
            tmp = duration - d * 24 * 3600 * 1000;
        } else {
            vDays.setText("");
            tmp = duration;
        }

        int h = Math.round(tmp / 3600 / 1000);
        tmp = tmp - h * 3600 * 1000;
        int m = Math.round(tmp / 60 / 1000);

        vHours.setSelection(findPositionInSpinner(0, h));
        vMinutes.setSelection(findPositionInSpinner(1, m));
    }

    private void onDateChanges(View v){

        final TextView date = (TextView) v;
        int[] dt = convertDateToArray(date.getText());
        new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year,monthOfYear,dayOfMonth);
                date.setText(app.getDateFormat().format(c.getTime()));
                getStartAndFinishFromForm();
                fillTSFields();
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
                getStartAndFinishFromForm();
                fillTSFields();
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
        int i;
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
        return res;
    }

    private void showConfirmationTaskDialog(String question) {

        if (question.isEmpty()){
            dialogResultHandler(0);
        } else {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setMessage(question);
            ad.setPositiveButton(R.string.addform_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    dialogResultHandler(1);
                }
            });
            ad.setNegativeButton(R.string.addform_no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    dialogResultHandler(0);
                }
            });
            ad.setCancelable(false);
            ad.show();
        }
    }

    private void dialogResultHandler(int res){

        int newTaskStatus = getTaskStatusFromForm();
        long newTaskDueDate = getTaskDueDateFromForm();

        if (res != 0) {

            FOTT_TaskBuilder newTask = new FOTT_TaskBuilder(task);
            newTask.setStatus(newTaskStatus);
            newTask.setDueDate(newTaskDueDate);
            result.putExtra(FOTT_MainActivity.EXTRA_MESSAGE_TS_EDIT_TASK,
                    FOTT_Parcel.parcelTask(newTask.buildObject()));
        }

        setResult(RESULT_OK, result);
        finish();
    }
}
