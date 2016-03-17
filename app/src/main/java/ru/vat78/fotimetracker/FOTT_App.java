package ru.vat78.fotimetracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import ru.vat78.fotimetracker.connectors.database.FOTT_DB;
import ru.vat78.fotimetracker.connectors.database.FOTT_DBMembers;
import ru.vat78.fotimetracker.connectors.database.FOTT_DBTasks;
import ru.vat78.fotimetracker.connectors.database.FOTT_DBTimeslots;
import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Connector;
import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Exceptions;
import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Members;
import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Tasks;
import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Timeslots;
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
    private final String FOTT_TIME_FORMAT_24H = "HH:mm";
    private final String FOTT_TIME_FORMAT_AMPMH = "K:mm a";


    private FOAPI_Connector web_service;
    private FOTT_DB database;
    private boolean needFullSync;

    private FOTT_MainActivity mainActivity;

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
        long db_version = preferences.getLong(getString(R.string.pref_db_version), 0);
        database = new FOTT_DB(this,db_version);
        preferences.set(getString(R.string.pref_db_version), database.getDb_version());

        //Create error handler
        error = new FOTT_ErrorsHandler(this);

        load_preferences();
    }

    private void load_preferences() {
        curMember = preferences.getLong(getString(R.string.pref_stored_member), 0);
        curTask = preferences.getLong(getString(R.string.pref_stored_task), 0);
        curTimeslot = preferences.getLong(getString(R.string.pref_stored_ts), 0);
        lastSync = new Date(preferences.getLong(getString(R.string.pref_stored_last_sync),0));

        setDateTimeFormat();

        String s = preferences.getString(getString(R.string.pref_sync_url), "");
        if (!s.isEmpty()) web_service.setFO_Url(s);
        s = preferences.getString(getString(R.string.pref_sync_login), "");
        if (!s.isEmpty()) web_service.setFO_User(s);
        s = preferences.getString(getString(R.string.pref_sync_password), "");
        if (!s.isEmpty()) web_service.setFO_Pwd(s);
        web_service.canUseUntrustCert(preferences.getBoolean(getString(R.string.pref_sync_certs), false));
    }

    public void setDateTimeFormat() {
        String s = preferences.getString(getString(R.string.pref_date_format), FOTT_DATE_FORMAT);
        dateFormat = new SimpleDateFormat(s);
        dateFormat.setTimeZone(TimeZone.getDefault());

        if (preferences.getBoolean(getString(R.string.pref_time_format), false)) s = FOTT_TIME_FORMAT_24H; else s = FOTT_TIME_FORMAT_AMPMH;
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

    public FOTT_MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setCurMember(long curMember) {
        //ToDo check cur task and timeslot
        curTimeslot = 0;
        setCurTask(0);
        this.curMember = curMember;
        preferences.set(getString(R.string.pref_stored_member), curMember);
    }

    public void setCurTask(long curTask) {
        //ToDo check current timeslot
        curTimeslot = 0;
        this.curTask = curTask;
        preferences.set(getString(R.string.pref_stored_task), curTask);
    }

    public void setCurTimeslot(long time) {
        curTimeslot = time;
        preferences.set(getString(R.string.pref_stored_ts), curTimeslot);
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

    public void setMainActivity(FOTT_MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean dataSynchronization() {

        long stamp = System.currentTimeMillis();
        error.reset_error();

        if (!getWeb_service().testConnection()) {
            setSyncing(false);
            return false;
        }

        boolean fullSync = isNeedFullSync() || mainActivity == null;
        Date d = (fullSync ? new Date(0) : getLastSync());

        //Sync members
        FOAPI_Members apiMembers = FOAPI_Members.getInstance(web_service);
        ArrayList<FOTT_Member> members;
        try {
            members = apiMembers.loadObjects();
        } catch (FOAPI_Exceptions e) {
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
        boolean success;
        FOAPI_Tasks apiTasks = FOAPI_Tasks.getInstance(web_service);
        ArrayList<FOTT_Task> tasks = FOTT_DBTasks.getDeletedTasks(this);
        for (FOTT_Task t : tasks) {
            success = true;
            try {
                apiTasks.deleteObject(t);
            } catch (FOAPI_Exceptions e) {
                success = false;
            }
            if (success && !fullSync) FOTT_DBTasks.deleteTask(this, t);
        }
        tasks = FOTT_DBTasks.getChangedTasks(this, getLastSync());
        for (FOTT_Task t : tasks) {
            long id;
            try {
                id = apiTasks.saveObject(t);
            } catch (FOAPI_Exceptions e) {
                id = 0;
            }
            if (id != 0 && !fullSync) FOTT_DBTasks.deleteTask(this, t);
        }
        try {
            tasks = apiTasks.loadChangedObjects(d);
        } catch (FOAPI_Exceptions e) {
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
        FOAPI_Timeslots apiTS = FOAPI_Timeslots.getInstance(web_service);
        ArrayList<FOTT_Timeslot> timeslots = FOTT_DBTimeslots.getDeletedTS(this);
        for (FOTT_Timeslot ts : timeslots) {
            success = true;
            try {
                apiTS.deleteObject(ts);
            } catch (FOAPI_Exceptions e) {
                success = false;
            }
            if (success && !fullSync) FOTT_DBTimeslots.deleteTS(this, ts);
        }

        timeslots = FOTT_DBTimeslots.getChangedTS(this, getLastSync());
        for (FOTT_Timeslot ts : timeslots) {
            long id = 0;
            try {
                id = apiTS.saveObject(ts);
            } catch (FOAPI_Exceptions e) { }
            if (id != 0 && !fullSync) FOTT_DBTimeslots.deleteTS(this, ts);
        }

        try {
            timeslots = apiTS.loadChangedObjects(d);
        } catch (FOAPI_Exceptions e) {
            setSyncing(false);
            return false;
        }
        FOTT_DBTimeslots.save(this, timeslots, fullSync);
        if (getError().is_error()) {
            setSyncing(false);
            return false;
        }


        setLastSync(stamp);
        setSyncing(false);
        setNeedFullSync(false);
        return true;
    }

    public void redrawMainActivity() {
        int shift = 0;
        if (curMember != 0)
            if (!FOTT_DBMembers.isExistInDB(this, curMember)) {
                setCurMember(0);
                shift = 1;
            }
        if (curTask != 0)
            if (!FOTT_DBTasks.isExistInDB(this, curTask)) {
                setCurTask(0);
                shift = 2;
            }

        if (mainActivity != null) {
            if (shift == 0)
                mainActivity.redraw();
            else
                mainActivity.setCurrentFragment(shift - 1);
        }
    }
}
