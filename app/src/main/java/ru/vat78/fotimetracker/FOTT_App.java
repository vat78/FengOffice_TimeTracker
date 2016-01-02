package ru.vat78.fotimetracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import ru.vat78.fotimetracker.database.FOTT_DB;
import ru.vat78.fotimetracker.database.FOTT_DBMembers;
import ru.vat78.fotimetracker.database.FOTT_DBTasks;
import ru.vat78.fotimetracker.database.FOTT_DBTimeslots;
import ru.vat78.fotimetracker.fo_api.FOAPI_Connector;
import ru.vat78.fotimetracker.fo_api.FOAPI_Members;
import ru.vat78.fotimetracker.fo_api.FOAPI_Tasks;
import ru.vat78.fotimetracker.fo_api.FOAPI_Timeslots;
import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
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

    public long getCurTimeslot() {return curTimeslot;}

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
        //ToDo check cur task and timeslot
        this.curMember = curMember;
        preferences.set(getString(R.string.pref_stored_member), curMember);
    }

    public void setCurTask(long curTask) {
        //ToDo check current timeslot
        this.curTask = curTask;
        preferences.set(getString(R.string.pref_stored_task), curTask);
    }

    public void setCurTimeslot(long time) {
        curTimeslot = time;
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

    public boolean dataSynchronization() {

        long stamp = System.currentTimeMillis();
        try {

            if (!getWeb_service().testConnection()) {
                setSyncing(false);
                return false;
            }

            boolean fullSync = isNeedFullSync();
            Date d = (fullSync ? new Date(0) : getLastSync());

            //Sync members
            ArrayList<FOTT_Member> members = FOAPI_Members.load(this);
            if (getError().is_error()) {
                setSyncing(false);
                return false;
            }
            FOTT_DBMembers.save(this, members);
            if (getError().is_error()) {
                setSyncing(false);
                return false;
            }
            members = null;

            //Sync task
            if (FOAPI_Tasks.delete(this,FOTT_DBTasks.getDeletedTasks(this)) && !fullSync) FOTT_DBTasks.clearDeletedTasks(this);
            if (FOAPI_Tasks.save(this, FOTT_DBTasks.getChangedTasks(this, getLastSync())) && !fullSync) FOTT_DBTasks.clearNewTasks(this);
            ArrayList<FOTT_Task> tasks = FOAPI_Tasks.load(this, d);
            if (getError().is_error()) {
                setSyncing(false);
                return false;
            }
            FOTT_DBTasks.save(this, tasks, fullSync);
            if (getError().is_error()) {
                setSyncing(false);
                return false;
            }
            tasks = null;

            //Sync timeslots
            if (FOAPI_Timeslots.delete(this,FOTT_DBTimeslots.getDeletedTS(this)) && !fullSync) FOTT_DBTimeslots.clearDeletedTS(this);
            if (FOAPI_Timeslots.save(this, FOTT_DBTimeslots.getChangedTS(this, getLastSync())) && !fullSync) FOTT_DBTimeslots.clearNewTS(this);
            ArrayList<FOTT_Timeslot> timeslots = FOAPI_Timeslots.load(this, d);
            if (getError().is_error()) {
                setSyncing(false);
                return false;
            }
            FOTT_DBTimeslots.save(this, timeslots, fullSync);
            if (getError().is_error()) {
                setSyncing(false);
                return false;
            }
        } catch (Exception e) {
            getError().error_handler(FOTT_ErrorsHandler.ERROR_SAVE_ERROR, "", e.getMessage());
            setSyncing(false);
            return false;
        }

        setLastSync(stamp);
        setSyncing(false);
        return true;
    }
}
