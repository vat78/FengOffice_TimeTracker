package ru.vat78.fotimetracker;

import android.os.AsyncTask;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Async task for syncing data from remote system to local db
 */
public class SyncTask extends AsyncTask<App, Integer, Boolean> {
    private static final String CLASS_NAME = "SyncTask";
    protected static AtomicBoolean syncInProgress = new AtomicBoolean(false);

    private App app;

    @Override
    protected Boolean doInBackground(App... params) {
        boolean result = false;
        if (params.length > 0) {
            app = params[0];
            if (!syncInProgress.getAndSet(true)) {
                if (app.getMainActivity() == null) app.setNeedFullSync(true);
                result = dataSynchronization();
                syncInProgress.set(false);
            }
        }
        return  result;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            app.setNeedFullSync(false);
            app.redrawMainActivity();
        }
    }

    @Override
    protected void onCancelled() {
        syncInProgress.set(false);
    }

    protected boolean dataSynchronization() {
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
}
