package ru.vat78.fotimetracker;

import android.content.Intent;
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

import ru.vat78.fotimetracker.adapters.MembersAdapter;
import ru.vat78.fotimetracker.adapters.TasksAdapter;
import ru.vat78.fotimetracker.adapters.TimeslotsAdapter;
import ru.vat78.fotimetracker.model.Member;
import ru.vat78.fotimetracker.model.Task;
import ru.vat78.fotimetracker.views.MembersFragment;
import ru.vat78.fotimetracker.views.TasksFragment;
import ru.vat78.fotimetracker.views.TimeslotsFragment;

public class MainActivity extends AppCompatActivity {

    static final int PICK_LOGIN_REQUEST = 1;
    static final int PICK_TSEDIT_REQUEST = 2;

    static final String EXTRA_MESSAGE_TS_EDIT_ID = "ru.vat78.fotimetracker.TSID";
    static final String EXTRA_MESSAGE_TS_EDIT_DURATION = "ru.vat78.fotimetracker.TSDURATION";
    static final String EXTRA_MESSAGE_TS_EDIT_START = "ru.vat78.fotimetracker.TSSTART";
    static final String EXTRA_MESSAGE_TS_EDIT_DESC = "ru.vat78.fotimetracker.TSDESC";

    private SyncTask syncTask;

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

    private App MainApp;
    private MembersAdapter members;
    private TasksAdapter tasks;
    private TimeslotsAdapter timeslots;

    private Timer syncTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainApp = (App) getApplication();

        setContentView(R.layout.activity_fott__main);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        CheckLogin();

        syncTimer = new Timer(true);
        syncTimer.schedule(new SyncTimerTask(), 60000, 600000);
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

        if (MainApp != null)
        {
            int fragment = mViewPager.getCurrentItem();
            if (MainApp.getCurTimeslot() != 0 && fragment != 2) {
                //ToDo add question
                startStopTimer();
            }
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_exit) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        redraw();
    }

    private void redraw() {
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

                if (timeslots.saveTimeslot(id,d,l,s)){
                    timeslots.load();
                } else {
                    //Todo: save error
                }

            }
        }
    }

    private void CheckLogin(){

        if (MainApp.getPreferences().getString(getString(R.string.pref_sync_password),"").isEmpty()
                || MainApp.isNeedFullSync()){
            Intent pickLogin = new Intent(this,LoginActivity.class);
            startActivityForResult(pickLogin,PICK_LOGIN_REQUEST);
        }

    }

    public TasksAdapter getTasks() {
        return tasks;
    }

    public void setMembers(MembersAdapter members) {
        this.members = members;
    }

    public void setTasks(TasksAdapter tasks) {
        this.tasks = tasks;
    }

    public void setTimeslots(TimeslotsAdapter timeslots) {
        this.timeslots = timeslots;
    }

    public void setCurrentFragment(int fragment) {
        mViewPager.setCurrentItem(fragment,true);
        redraw();
    }

    public void syncWithFO(){

    }

    public void editTimeslot(long tsId, long start, long duration) {
        Intent pickTS = new Intent(this,TimeslotEditActivity.class);

        pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_ID, tsId);
        if (start != 0) pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_START, start);
        if (duration !=0) pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_DURATION, duration);

        startActivityForResult(pickTS, PICK_TSEDIT_REQUEST);
    }

    private void TimeslotsFragmentCaption() {

        View topArea = findViewById(R.id.tsTopContext);
        Member m = members.getMemberById(MainApp.getCurMember());
        topArea.setBackgroundColor(m.getColor());
        TextView top_title = (TextView)findViewById(R.id.tsTopTitle);
        TextView top_desc = (TextView) findViewById(R.id.tsTopDesc);
        if (MainApp.getCurTask() > 0) {
            Task t = getTasks().getTaskById(MainApp.getCurTask());
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

    public void startStopTimer() {
        ImageButton timer = (ImageButton) findViewById(R.id.tsTimerBtn);
        if (MainApp.getCurTimeslot() == 0){
            MainApp.setCurTimeslot(System.currentTimeMillis());
            timer.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause,getTheme()));
            oneMinuteTimer();
        } else {
            long dur = System.currentTimeMillis() - MainApp.getCurTimeslot();
            timer.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play, getTheme()));
            MainApp.setCurTimeslot(0);
            oneMinuteTimer();
            editTimeslot(0, 0, dur);
        }
    }

    public void oneMinuteTimer() {
        //ToDo: add reminder
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
                    res = new MembersFragment();
                    return res;
                case 1:
                    res = new TasksFragment();
                    return res;
                default:
                    return new TimeslotsFragment();
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

                if (fragment > 0 && MainApp.getCurMember() != 0) {
                    Member m = members.getMemberById(MainApp.getCurMember());
                    CharSequence s = getString(R.string.title_category) + ": " + m.getName();
                    mActionBarToolbar.setSubtitle(s);
                    mActionBarToolbar.setBackgroundColor(m.getColor());
                }
                else {
                    mActionBarToolbar.setSubtitle("");
                }
                if (fragment == 2) TimeslotsFragmentCaption();
            }
            if (MainApp != null)
            {
                if (MainApp.getCurTimeslot() != 0 && fragment != 2) {
                    //ToDo add question
                    startStopTimer();
                }
            }
        }
    }

    private class TimeslotMinuteTimer extends CountDownTimer {

        public TimeslotMinuteTimer(){
            super(60000,1000);
        }

        public void onTick(long millisUntilFinished) {
            TextView mTextDuration = (TextView) findViewById(R.id.tsCurDuration);
            String s = mTextDuration.getText().toString();
            if (s.endsWith("."))
                s = s.substring(0,s.length()-1) + ":";
            else
                s = s.substring(0,s.length()-1) + ".";
            mTextDuration.setText(s);
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
                    if (syncTask != null) return;
                    syncTask = new SyncTask(MainApp);
                    syncTask.execute();
                }
            });
        }
    }
}
