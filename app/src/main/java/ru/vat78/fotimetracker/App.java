package ru.vat78.fotimetracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ru.vat78.fotimetracker.database.DB;
import ru.vat78.fotimetracker.database.DaoTasks;
import ru.vat78.fotimetracker.database.DaoTimeslots;
import ru.vat78.fotimetracker.database.DaoMembers;
import ru.vat78.fotimetracker.fengoffice.FengOfficeApi;
import ru.vat78.fotimetracker.fengoffice.vatApi.*;
import ru.vat78.fotimetracker.model.Member;
import ru.vat78.fotimetracker.model.Task;
import ru.vat78.fotimetracker.model.Timeslot;
import ru.vat78.fotimetracker.views.ErrorsHandler;


/**
 * Created by vat on 24.11.2015.
 *
 * Main application
 */
public class App extends Application {

    private final String FOTT_DATE_FORMAT = "dd.MM.yyyy";
    private final String FOTT_TIME_FORMAT = "HH:mm";

    private ApiConnector webService;
    private FengOfficeApi foApi;
    private DB database;
    private boolean needFullSync;

    private long curMember;
    private long curTask;
    private long curTimeslot;

    private Date lastSync;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    private Preferences preferences;

    private ErrorsHandler error;

    private boolean syncing;

    @Override
    public void onCreate() {
        super.onCreate();

        //Create web-service connection
        webService = new ApiConnector(this);
        foApi = new VatApi(this);

        //Application preferences
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        preferences = new Preferences(pref);

        //Create database connection
        long db_version = preferences.getLong(getString(R.string.pref_db_version),0);
        database = new DB(this,db_version);
        preferences.set(getString(R.string.pref_db_version), database.getDb_version());

        //Create error handler
        error = new ErrorsHandler();

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

        s = preferences.getString(getString(R.string.pref_sync_url), "");
        if (!s.isEmpty()) webService.setFO_Url(s);
        s = preferences.getString(getString(R.string.pref_sync_login), "");
        if (!s.isEmpty()) webService.setFO_User(s);
        s = preferences.getString(getString(R.string.pref_sync_password), "");
        if (!s.isEmpty()) webService.setFO_Pwd(s);
        webService.canUseUntrustCert(preferences.getBoolean(getString(R.string.pref_sync_certs),false));
    }

    public DB getDatabase() {
        return database;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public boolean isNeedFullSync() {
        return needFullSync;
    }

    public ApiConnector getWebService() {
        return webService;
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

    public ErrorsHandler getError() {
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

            if (!getWebService().testConnection()) {
                setSyncing(false);
                return false;
            }

            boolean fullSync = isNeedFullSync();
            Date d = (fullSync ? new Date(0) : getLastSync());

            //Sync members
            List<Member> members = foApi.loadMembers();
            if (getError().is_error()) {
                setSyncing(false);
                return false;
            }
            DaoMembers.save(this, members);
            if (getError().is_error()) {
                setSyncing(false);
                return false;
            }
            members = null;

            //Sync task
            List<Task> tasks = DaoTasks.getDeletedTasks(this);
            for (Task t: tasks){
                if (foApi.deleteTask(t) && !fullSync) DaoTasks.deleteTask(this, t);
            }
            tasks = DaoTasks.getChangedTasks(this, getLastSync());
            for (Task t: tasks){
                long id = foApi.saveTask(t);
                if (id !=0 && !fullSync) DaoTasks.deleteTask(this, t);
            }
            tasks = foApi.loadTasks(d);
            if (getError().is_error()) {
                setSyncing(false);
                return false;
            }
            DaoTasks.save(this, tasks, fullSync);
            if (getError().is_error()) {
                setSyncing(false);
                return false;
            }
            tasks = null;

            //Sync timeslots
            List<Timeslot> timeslots = DaoTimeslots.getDeletedTS(this);
            for (Timeslot ts: timeslots){
                if (foApi.deleteTimeslot(ts) && !fullSync) DaoTimeslots.deleteTS(this, ts);
            }

            timeslots = DaoTimeslots.getChangedTS(this, getLastSync());
            for (Timeslot ts: timeslots){
                long id = foApi.saveTimeslot(ts);
                if (id != 0 && !fullSync) DaoTimeslots.deleteTS(this, ts);
            }

            timeslots = foApi.loadTimeslots(d);
            if (getError().is_error()) {
                setSyncing(false);
                return false;
            }
            DaoTimeslots.save(this, timeslots, fullSync);
            if (getError().is_error()) {
                setSyncing(false);
                return false;
            }
        } catch (Exception e) {
            getError().error_handler(ErrorsHandler.ERROR_SAVE_ERROR, "", e.getMessage());
            setSyncing(false);
            return false;
        }

        setLastSync(stamp);
        setSyncing(false);
        setNeedFullSync(false);
        return true;
    }
}