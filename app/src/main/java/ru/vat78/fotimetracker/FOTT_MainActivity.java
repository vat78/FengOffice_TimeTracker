package ru.vat78.fotimetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.Date;

import ru.vat78.fotimetracker.adapters.FOTT_MembersAdapter;
import ru.vat78.fotimetracker.adapters.FOTT_TasksAdapter;
import ru.vat78.fotimetracker.adapters.FOTT_TimeslotsAdapter;
import ru.vat78.fotimetracker.fo_api.FOAPI_Timeslots;
import ru.vat78.fotimetracker.views.FOTT_MembersFragment;
import ru.vat78.fotimetracker.views.FOTT_TasksFragment;
import ru.vat78.fotimetracker.views.FOTT_TimeslotsFragment;

public class FOTT_MainActivity extends AppCompatActivity {

    static final int PICK_LOGIN_REQUEST = 1;
    static final int PICK_TSEDIT_REQUEST = 2;

    static final String EXTRA_MESSAGE_TS_EDIT_ID = "ru.vat78.fotimetracker.TSID";
    static final String EXTRA_MESSAGE_TS_EDIT_DURATION = "ru.vat78.fotimetracker.TSDURATION";
    static final String EXTRA_MESSAGE_TS_EDIT_START = "ru.vat78.fotimetracker.TSSTART";
    static final String EXTRA_MESSAGE_TS_EDIT_DESC = "ru.vat78.fotimetracker.TSDESC";


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

    private FOTT_App MainApp;
    private FOTT_MembersAdapter members;
    private FOTT_TasksAdapter tasks;
    private FOTT_TimeslotsAdapter timeslots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainApp = (FOTT_App) getApplication();

        setContentView(R.layout.activity_fott__main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        //Connect lists adapters
        members = new FOTT_MembersAdapter(this,MainApp);
        CheckLogin();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_LOGIN_REQUEST) {
            if (resultCode != RESULT_OK) {
                finish();
            }
            members.load();
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

                    FOAPI_Timeslots.saveChangedTimeslots(MainApp,timeslots);
                    MainApp.syncFO();
                } else {
                    //Todo: save error
                }

            }
        }
    }

    private void CheckLogin(){
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(MainApp);
        //if (preferences.getString(getString(R.string.pref_sync_password),"").isEmpty()){
            Intent pickLogin = new Intent(this,FOTT_LoginActivity.class);
            startActivityForResult(pickLogin,PICK_LOGIN_REQUEST);
        //}
    }

    public FOTT_MembersAdapter getMembers() {
        return members;
    }

    public FOTT_TasksAdapter getTasks() {
        return tasks;
    }

    public FOTT_TimeslotsAdapter getTimeslots() {
        return timeslots;
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
        if (fragment == 1) {
            tasks.load();
            tasks.notifyDataSetChanged();
        }
        if (fragment == 2) {
            timeslots.load();
            timeslots.notifyDataSetChanged();
        }
        mViewPager.setCurrentItem(fragment,true);
    }

    public void editTimeslot(long tsId, long duration) {
        Intent pickTS = new Intent(this,FOTT_TSEditActivity.class);
        if (tsId == 0) {
            pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_ID, 0);
            if (duration == 0) {
                //ToDo: get default duration from preferences
                duration = 15;
            }
            pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_DURATION, duration);
        } else {
            pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_ID, tsId);
            pickTS.putExtra(EXTRA_MESSAGE_TS_EDIT_DURATION, duration);
        }

        startActivityForResult(pickTS,PICK_TSEDIT_REQUEST);
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
                    return "Categories";
                case 1:
                    return "Tasks";
                case 2:
                    return "Timeslots";
            }
            return null;
        }
    }
}
