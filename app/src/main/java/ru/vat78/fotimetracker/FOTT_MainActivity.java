package ru.vat78.fotimetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ru.vat78.fotimetracker.adapters.FOTT_MembersAdapter;
import ru.vat78.fotimetracker.adapters.FOTT_TasksAdapter;
import ru.vat78.fotimetracker.adapters.FOTT_TimeslotsAdapter;
import ru.vat78.fotimetracker.database.FOTT_DBTasks;
import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.views.FOTT_MembersFragment;
import ru.vat78.fotimetracker.views.FOTT_TasksFragment;
import ru.vat78.fotimetracker.views.FOTT_TimeslotsFragment;

public class FOTT_MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static final int PICK_LOGIN_REQUEST = 1;
    static final int PICK_TSEDIT_REQUEST = 2;

    static final String EXTRA_MESSAGE_TS_EDIT_ID = "ru.vat78.fotimetracker.TSID";
    static final String EXTRA_MESSAGE_TS_EDIT_DURATION = "ru.vat78.fotimetracker.TSDURATION";
    static final String EXTRA_MESSAGE_TS_EDIT_START = "ru.vat78.fotimetracker.TSSTART";
    static final String EXTRA_MESSAGE_TS_EDIT_DESC = "ru.vat78.fotimetracker.TSDESC";
    static final String EXTRA_MESSAGE_TS_EDIT_TASK_STATUS = "ru.vat78.fotimetracker.TASKSTATUS";
    static final String EXTRA_MESSAGE_TS_EDIT_TASK_DUE = "ru.vat78.fotimetracker.TASKDUE";
    static final String EXTRA_MESSAGE_TS_EDIT_TASK_NAME = "ru.vat78.fotimetracker.TASKNAME";

    static final String SYNC_TIMER_NAME = "ru.vat78.fotimetracker.SYNCTIMER";

    private FOTT_SyncTask syncTask;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

        /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar mActionBarToolbar;

    private FOTT_App MainApp;
    private FOTT_MembersAdapter members;
    private FOTT_TasksAdapter tasks;
    private FOTT_TimeslotsAdapter timeslots;

    private Timer syncTimer;

    private FOTT_BroadcastReceiver alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainApp = (FOTT_App) getApplication();
        MainApp.setMainActivity(this);
        MainApp.getPreferences().registerOnSharedPreferenceChangeListener(this);

        setContentView(R.layout.activity_fott__main);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        checkLogin();

        setSyncTimer();

        alarm = new FOTT_BroadcastReceiver();

    }

    private void setSyncTimer() {
        if (syncTimer != null){
            syncTimer.cancel();
        }
        syncTimer = new Timer(SYNC_TIMER_NAME, true);
        long freq = Long.valueOf(MainApp.getPreferences().getString(getString(R.string.pref_sync_frequency),"180")) ;
        if (freq > 0) {
            freq = freq * 60 * 1000;
            syncTimer.schedule(new SyncTimerTask(), 1000, freq);
        } else {
            syncTimer.schedule(new SyncTimerTask(), 1000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fott__main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        int fragment = mViewPager.getCurrentItem();
        if (MainApp != null)
        {
            if (MainApp.getCurTimeslot() != 0 && fragment != 2) {
                //ToDo add question
                startStopTimer();
            }
        }

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home && fragment > 0) {
            setCurrentFragment(fragment - 1);
        }

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_fullsync) {
            MainApp.setNeedFullSync(true);
            setSyncTimer();
        }

        if (id == R.id.action_exit) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.pref_sync_url)) ||
                key.equals(getString(R.string.pref_sync_login)) ||
                key.equals(getString(R.string.pref_sync_password)) ||
                key.equals(getString(R.string.pref_sync_certs))){
            MainApp.setNeedFullSync(true);
            //checkLogin();
        }
        if (key.equals(getString(R.string.pref_sync_save_creds))){
            if (!MainApp.getPreferences().getBoolean(key,false))
                MainApp.getPreferences().set(getString(R.string.pref_sync_password),"");
        }

        if (key.equals(getString(R.string.pref_sync_frequency))) {
            setSyncTimer();
        }
        if (key.equals(getString(R.string.pref_alert_frequency))) {
            alarm.setOnetimeTimer(MainApp);
        }
        if (key.equals(getString(R.string.pref_date_format)) ||
            key.equals(getString(R.string.pref_time_format))) {
            MainApp.setDateTimeFormat();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MainApp.setMainActivity(this);
        if (MainApp.getCurTimeslot() != 0){
            setCurrentFragment(2);
        } else {
            redraw();
        }
        if (MainApp.isNeedFullSync()) checkLogin();
    }

    @Override
    protected void onStop() {
        //MainApp.setMainActivity(null);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        MainApp.setMainActivity(null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        int fragment = mViewPager.getCurrentItem();
        if (fragment > 0) {
            setCurrentFragment(fragment - 1);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected  void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        String c = intent.getStringExtra(FOTT_BroadcastReceiver.BCommand);
        if (c.equals(FOTT_BroadcastReceiver.BC_TimerAlarm)) alarmDialogShow();
    }

    public void redraw() {
        int fragment = mViewPager.getCurrentItem();
        switch (fragment) {
            case 0:
                if (members != null) {
                    members.load();
                    members.notifyDataSetChanged();
                }
                break;
            case 1:
                if (tasks != null) {
                    tasks.load();
                    tasks.notifyDataSetChanged();
                }
                break;
            case 2:
                if (timeslots != null) {
                    timeslots.load();
                    timeslots.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_LOGIN_REQUEST) {
            if (resultCode != RESULT_OK) {
                finish();
            }
        }
        if (requestCode == PICK_TSEDIT_REQUEST) {
            if (resultCode == RESULT_OK) {
                long l = data.getLongExtra(EXTRA_MESSAGE_TS_EDIT_START,0);
                Date d = new Date(l);
                l = data.getLongExtra(EXTRA_MESSAGE_TS_EDIT_DURATION, 0);
                long id = data.getLongExtra(EXTRA_MESSAGE_TS_EDIT_ID,0);
                String s = data.getStringExtra(EXTRA_MESSAGE_TS_EDIT_DESC);
                if (MainApp.getCurTask() > 0) taskChangesHandler(data);

                if (timeslots.saveTimeslot(id,d,l,s)){
                    setSyncTimer();
                } else {
                    //Todo: save error
                }

            }
        }
    }


    private void taskChangesHandler(Intent intent) {
        boolean tclose = MainApp.getPreferences().getBoolean(getString(R.string.pref_can_close_task), false);
        boolean tmove = MainApp.getPreferences().getBoolean(getString(R.string.pref_can_change_task), false);

        if (MainApp.getCurTask() > 0)
        {
            FOTT_Task t = tasks.getTaskById(MainApp.getCurTask());
            int status = intent.getIntExtra(EXTRA_MESSAGE_TS_EDIT_TASK_STATUS, t.getStatus());
            long duedate = intent.getLongExtra(EXTRA_MESSAGE_TS_EDIT_TASK_DUE, t.getDueDate().getTime());
            if (tclose && status != t.getStatus()) {
                t.setStatus(status);
                t.setChanged(System.currentTimeMillis());
                FOTT_DBTasks.save(MainApp,t);
            } else if (tmove && duedate != t.getDueDate().getTime()) {
                t.setDuedate(duedate);
                t.setChanged(System.currentTimeMillis());
                FOTT_DBTasks.save(MainApp, t);
            }
        }
    }

    private void checkLogin(){

        if (MainApp.getPreferences().getString(getString(R.string.pref_sync_password),"").isEmpty()
                || MainApp.isNeedFullSync()){
            Intent pickLogin = new Intent(this,FOTT_LoginActivity.class);
            startActivityForResult(pickLogin, PICK_LOGIN_REQUEST);
        }

    }

    public FOTT_TasksAdapter getTasks() {
        return tasks;
    }

    public void setMembers(FOTT_MembersAdapter members) {
        this.members = members;
    }

    public void setTasks(FOTT_TasksAdapter tasks) {
        this.tasks = tasks;
    }

    public void setTimeslots(FOTT_TimeslotsAdapter timeslots) {
        this.timeslots = timeslots;
    }

    public void setCurrentFragment(int fragment) {
        mViewPager.setCurrentItem(fragment,true);
        redraw();
    }


    public void editTimeslot(long tsId, long start, long duration, String text) {
        Intent pickTS = new Intent(this,FOTT_TSEditActivity.class);

        pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_ID, tsId);
        pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_DESC, text);
        if (start != 0) pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_START, start);
        if (duration !=0) pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_DURATION, duration);
        if (MainApp.getCurTask() != 0){
            FOTT_Task t = tasks.getTaskById(MainApp.getCurTask());
            pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_TASK_NAME, t.getName());
            pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_TASK_STATUS, t.getStatus());
            pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_TASK_DUE, t.getDueDate().getTime());
        }

        startActivityForResult(pickTS, PICK_TSEDIT_REQUEST);
    }

    private void timeslotsFragmentCaption() {

        View topArea = findViewById(R.id.tsTopContext);
        if (topArea != null) {
            FOTT_Member m = members.getMemberById(MainApp.getCurMember());
            topArea.setBackgroundColor(m.getColor());
            TextView top_title = (TextView) findViewById(R.id.tsTopTitle);
            TextView top_desc = (TextView) findViewById(R.id.tsTopDesc);
            if (MainApp.getCurTask() > 0) {
                FOTT_Task t = getTasks().getTaskById(MainApp.getCurTask());
                top_title.setText(t.getName());
                top_desc.setText(t.getDesc());
            } else {
                if (MainApp.getCurMember() > 0) {
                    top_title.setText(R.string.title_no_tasks);
                    top_desc.setText(R.string.title_category_description);
                } else {
                    top_title.setText(R.string.title_no_active_task_and_category);
                    top_desc.setText("");
                }
            }
        }
    }

    public void continueTimer(){
        ImageButton timer = (ImageButton) findViewById(R.id.tsTimerBtn);
        if (timer != null)
            timer.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause, getTheme()));
        //alarm.setOnetimeTimer(MainApp);
        TimeslotMinuteTimer newTimer = new TimeslotMinuteTimer();
        newTimer.start();
    }

    public void startStopTimer() {
        ImageButton timer = (ImageButton) findViewById(R.id.tsTimerBtn);
        alarm.CancelAlarm(this.getApplicationContext());
        if (MainApp.getCurTimeslot() == 0) {
            MainApp.setCurTimeslot(System.currentTimeMillis());
            timer.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause, getTheme()));
            alarm.setOnetimeTimer(MainApp);
            oneMinuteTimer();
        } else {
            long dur = System.currentTimeMillis() - MainApp.getCurTimeslot();
            timer.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play, getTheme()));
            MainApp.setCurTimeslot(0);
            oneMinuteTimer();
            editTimeslot(0, 0, dur, "");
        }
    }

    public void oneMinuteTimer() {
        //ToDo: add reminder
        if (MainApp.getMainActivity() == null) return;
        if (MainApp.getCurTimeslot() != 0 ){
            long dur = System.currentTimeMillis() - MainApp.getCurTimeslot();
            showTimeslotDuration(dur);
            TimeslotMinuteTimer newTimer = new TimeslotMinuteTimer();
            newTimer.start();
        } else {
            TextView mTextDuration = (TextView) findViewById(R.id.tsCurDuration);
            mTextDuration.setVisibility(View.INVISIBLE);
        }

    }

    private void showTimeslotDuration(long duration) {
        int d = Math.round(duration / 24 / 3600 / 1000);
        long tmp = duration - d * 24 * 3600 * 1000;
        int h = Math.round(tmp / 3600 / 1000);
        tmp = tmp - h * 3600 * 1000;
        int m = Math.round(tmp / 60 / 1000);

        String res = "";
        if (d > 0) res = "" + d + " d. ";
        if (h > 0) res += "" + h + " h. ";
        else if (d > 0) res += "0 h. ";
        res += "" + m + " m.";

        TextView mTextDuration = (TextView) findViewById(R.id.tsCurDuration);
        mTextDuration.setText(res);
        mTextDuration.setVisibility(View.VISIBLE);
    }

    public void alarmDialogShow() {
        if (MainApp.getCurTimeslot() == 0) return;

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        String message = "You are working";
        if (MainApp.getCurTask() != 0 ) {
            message += " on task '" + tasks.getTaskById(MainApp.getCurTask()).getName() + "'";
        }
        if (MainApp.getCurMember() != 0) {
            message += " in category '" + members.getMemberById(MainApp.getCurMember()).getName() + "'";
        }
        message += ". Are you wish to stop this work?";
        ad.setMessage(message);
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                startStopTimer();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                alarm.setOnetimeTimer(MainApp);
            }
        });
        ad.setCancelable(false);
        ad.show();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment res;

            switch (position) {
                case 0:
                    res = new FOTT_MembersFragment();
                    return res;
                case 1:
                    res = new FOTT_TasksFragment();
                    return res;
                default:
                    return new FOTT_TimeslotsFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_categories);
                case 1:
                    return getString(R.string.title_tasks);
                case 2:
                    return getString(R.string.title_timeslots);
            }
            return null;
        }

        @Override
        public void startUpdate(ViewGroup container){
            super.startUpdate(container);
            int fragment = mViewPager.getCurrentItem();

            if (mActionBarToolbar != null) {
                mActionBarToolbar.setTitle(getPageTitle(fragment));
                getSupportActionBar().setDisplayHomeAsUpEnabled(fragment > 0);

                if (fragment > 0 && MainApp.getCurMember() != 0) {
                    FOTT_Member m = members.getMemberById(MainApp.getCurMember());
                    CharSequence s = getString(R.string.title_category) + ": " + m.getName();
                    mActionBarToolbar.setSubtitle(s);
                    mActionBarToolbar.setBackgroundColor(m.getColor());
                }
                else {
                    mActionBarToolbar.setSubtitle("");
                }
                if (fragment == 2) timeslotsFragmentCaption();
            }
            /*
            if (MainApp != null)
            {
                if (MainApp.getCurTimeslot() != 0 && fragment != 2) {
                    //ToDo add question
                    startStopTimer();
                }
            } */
        }
    }

    private class TimeslotMinuteTimer extends CountDownTimer {

        public TimeslotMinuteTimer(){
            super(60000,1000);
        }

        public void onTick(long millisUntilFinished) {
            TextView mTextDuration = (TextView) findViewById(R.id.tsCurDuration);
            if (mTextDuration != null) {
                String s = mTextDuration.getText().toString();
                if (s.isEmpty()) {
                    this.cancel();
                    oneMinuteTimer();
                } else {
                    if (s.endsWith("."))
                        s = s.substring(0, s.length() - 1) + ":";
                    else
                        s = s.substring(0, s.length() - 1) + ".";
                    mTextDuration.setText(s);
                }
            }
        }

        public void onFinish() {
            oneMinuteTimer();
        }

    }

    private class SyncTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (syncTask != null)
                        if (syncTask.getStatus() != AsyncTask.Status.RUNNING && !MainApp.isSyncing()) syncTask = null;
                    if (syncTask == null) {
                        syncTask = new FOTT_SyncTask(MainApp);
                        syncTask.execute();
                    }
                }
            });
        }

    }

}
