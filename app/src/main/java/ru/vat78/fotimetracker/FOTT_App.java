package ru.vat78.fotimetracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import ru.vat78.fotimetracker.database.FOTT_DB;
import ru.vat78.fotimetracker.fo_api.FOAPI_Connector;
import ru.vat78.fotimetracker.views.FOTT_ErrorsHandler;


/**
 * Created by vat on 24.11.2015.
 *
 * Main application
 */
public class FOTT_App extends Application {

    private final String FOTT_DATE_FORMAT = "dd.MM.yyyy";
    private final String FOTT_TIME_FORMAT = "HH:mm";


    private FOAPI_Connector web_service;
    private FOTT_DB database;
    private boolean needFullSync;

    private long curMember;
    private long curTask;
    private long curTimeslot;

    private Date lastSync;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    private FOTT_Preferences preferences;

    private FOTT_ErrorsHandler error;

    private boolean syncing;

    @Override
    public void onCreate() {
        super.onCreate();

        //Create web-service connection
        web_service = new FOAPI_Connector(this);

        //Application preferences
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        preferences = new FOTT_Preferences(pref);

        //Create database connection
        long db_version = preferences.getLong(getString(R.string.pref_db_version),0);
        database = new FOTT_DB(this,db_version);
        preferences.set(getString(R.string.pref_db_version), database.getDb_version());

        //Create error handler
        error = new FOTT_ErrorsHandler();

        load_preferences();
    }

    private void load_preferences() {
        curMember = preferences.getLong(getString(R.string.pref_stored_member), 0);
        curTask = preferences.getLong(getString(R.string.pref_stored_task), 0);
        lastSync = new Date(preferences.getLong(getString(R.string.pref_stored_last_sync),0));

        String s = preferences.getString("date_format", FOTT_DATE_FORMAT);
        dateFormat = new SimpleDateFormat(s);
        dateFormat.setTimeZone(TimeZone.getDefault());

        s = preferences.getString("time_format", FOTT_TIME_FORMAT);
        timeFormat = new SimpleDateFormat(s);
        timeFormat.setTimeZone(TimeZone.getDefault());
    }

    public FOTT_DB getDatabase() {
        return database;
    }

    public FOTT_Preferences getPreferences() {
        return preferences;
    }

    public boolean isNeedFullSync() {
        return needFullSync;
    }

    public FOAPI_Connector getWeb_service() {
        return web_service;
    }

    public long getCurMember() {
        return curMember;
    }

    public long getCurTask() {
        return curTask;
    }

    public Date getLastSync() {return lastSync;}

    public boolean isSyncing() {return syncing;}

    public FOTT_ErrorsHandler getError() {
        return error;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public SimpleDateFormat getTimeFormat() {
        return timeFormat;
    }

    public void setCurMember(long curMember) {
        this.curMember = curMember;
        preferences.set(getString(R.string.pref_stored_member), curMember);
    }

    public void setCurTask(long curTask) {
        this.curTask = curTask;
        preferences.set(getString(R.string.pref_stored_task), curTask);
    }

    public void setLastSync(long lastSync) {
        this.lastSync = new Date(lastSync);
        preferences.set(getString(R.string.pref_stored_last_sync), lastSync);
    }

    public void setSyncing(boolean syncing) {
        this.syncing = syncing;
    }

    public void setLastSync(Date lastSync) {
        this.lastSync = lastSync;
        preferences.set(getString(R.string.pref_stored_last_sync), lastSync.getTime());
    }

    public void setNeedFullSync(boolean value) {
        this.needFullSync = value;
    }

}
