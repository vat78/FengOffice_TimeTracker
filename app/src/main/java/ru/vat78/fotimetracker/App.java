package ru.vat78.fotimetracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.vat78.fotimetracker.database.*;
import ru.vat78.fotimetracker.fengoffice.IFengOfficeService;
import ru.vat78.fotimetracker.fengoffice.vatApi.*;
import ru.vat78.fotimetracker.model.Member;


/**
 * Created by vat on 24.11.2015.
 *
 * Main application
 */
public class App extends Application {

    private final String FOTT_DATE_FORMAT = "dd.MM.yyyy";
    private final String FOTT_TIME_FORMAT_24H = "HH:mm";
    private final String FOTT_TIME_FORMAT_AMPMH = "K:mm a";

    private IFengOfficeService webService;
    private DBService database;
    private boolean needFullSync;
    
    private MainActivity mainActivity;

    private long curMember;
    private long curTask;
    private long curTimeslot;

    private Date lastSync;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    private Preferences preferences;

    private IErrorsHandler errorsHandler;

    private AtomicBoolean syncing;

    @Override
    public void onCreate() {
        super.onCreate();

        //Create error handler
        errorsHandler = new ErrorsHandlerImpl(this);

        //Create web-service connection
        webService = new VatApi(errorsHandler);

        //Application preferences
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        preferences = new Preferences(pref);

        //Create database connection
        long db_version = preferences.getLong(getString(R.string.pref_db_version),0);
        database = new DBService(this, errorsHandler);
        if (db_version != database.databaseVersion()) needFullSync = true;
        preferences.set(getString(R.string.pref_db_version), database.databaseVersion());

        load_preferences();
    }

    private void load_preferences() {
        curMember = preferences.getLong(getString(R.string.pref_stored_member), 0);
        curTask = preferences.getLong(getString(R.string.pref_stored_task), 0);
        curTimeslot = preferences.getLong(getString(R.string.pref_stored_ts), 0);
        lastSync = new Date(preferences.getLong(getString(R.string.pref_stored_last_sync),0));

        setDateTimeFormat();

        webService.checkAndSetUrl(preferences.getString(getString(R.string.pref_sync_url), ""), preferences.getBoolean(getString(R.string.pref_sync_certs),false));
        webService.checkAndSetLogin(preferences.getString(getString(R.string.pref_sync_login), ""));
        webService.checkAndSetPassword(preferences.getString(getString(R.string.pref_sync_password), ""));
    }
    
    public void setDateTimeFormat() {
        String s = preferences.getString(getString(R.string.pref_date_format), FOTT_DATE_FORMAT);
        dateFormat = new SimpleDateFormat(s);
        dateFormat.setTimeZone(TimeZone.getDefault());

        if (preferences.getBoolean(getString(R.string.pref_time_format), false)) s = FOTT_TIME_FORMAT_24H; else s = FOTT_TIME_FORMAT_AMPMH;
        timeFormat = new SimpleDateFormat(s);
        timeFormat.setTimeZone(TimeZone.getDefault());
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public boolean isNeedFullSync() {
        return needFullSync;
    }

    public IFengOfficeService getWebService() {
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

    public boolean isSyncing() {return syncing.get();}

    public IErrorsHandler getError() {
        return errorsHandler;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public SimpleDateFormat getTimeFormat() {
        return timeFormat;
    }
    
    public MainActivity getMainActivity() {
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
        this.syncing.set(syncing);
    }

    public void setLastSync(Date lastSync) {
        this.lastSync = lastSync;
        preferences.set(getString(R.string.pref_stored_last_sync), lastSync.getTime());
    }

    public void setNeedFullSync(boolean value) {
        this.needFullSync = value;
    }
    
    public void setMainActivity(    MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean dataSynchronization() {
    /*
        long stamp = System.currentTimeMillis();
        error.reset_error();
        try {

            if (!getWebService().testConnection()) {
                setSyncing(false);
                return false;
            }

            boolean fullSync = isNeedFullSync() || mainActivity == null;
            Date d = (fullSync ? new Date(0) : getLastSync());

            //Sync members
            List<Member> members = foApi.loadMembers();
            if (getError().is_error()) {
                setSyncing(false);
                return false;
            }
            members.add(generateAnyMember());
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

        */
        return true;
    }
    
    public void redrawMainActivity() {
        /*
        int shift = 0;
        if (curMember != 0)
            if (!DaoMembers.isExistInDB(this, curMember)) {
                setCurMember(0);
                shift = 1;
            }
        if (curTask != 0)
            if (!DaoTasks.isExistInDB(this, curTask)) {
                setCurTask(0);
                shift = 2;
            }

        if (mainActivity != null) {
            if (shift == 0)
                mainActivity.redraw();
            else
                mainActivity.setCurrentFragment(shift - 1);
        }
        */
    }

    private Member generateAnyMember() {
        Member any = new Member(-1,getString(R.string.any_category));
        any.setPath("");
        any.setColorIndex(Color.TRANSPARENT);
        return any;
    }
}
