package ru.vat78.fotimetracker;

import android.os.AsyncTask;
import ru.vat78.fotimetracker.model.Member;
import ru.vat78.fotimetracker.model.Task;
import ru.vat78.fotimetracker.model.Timeslot;

import java.util.List;
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
                if (app.getMainActivity() == null) app.setNeedFullSync();
                result = dataSynchronization();
                syncInProgress.set(false);
            }
        }
        return  result;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            app.redrawMainActivity();
        }
    }

    @Override
    protected void onCancelled() {
        syncInProgress.set(false);
    }

    protected boolean dataSynchronization() {

        long timestamp = System.currentTimeMillis();

        boolean allOk = false;
        if (app.getWebService().testConnection()) {

            //Sync members
            allOk = syncMembers();

            if (allOk) {
                //Sync tasks
                if (!pushTaskChangesToWeb()) {
                    //Something wrong. Let's try agayn in next sync operation
                    timestamp = app.getLastSync().getTime();
                }
                allOk = syncTasks();
            }

            if (allOk) {
                //Sync timeslots
                if (!pushTimeslotsChangesToWeb()) {
                    //Something wrong. Let's try agayn in next sync operation
                    timestamp = app.getLastSync().getTime();
                }
                allOk = syncTimeslots();
            }
        }

        if (allOk) {
            app.setLastSync(timestamp);
        }
        return allOk;
    }

    private boolean syncTimeslots() {
        List<Timeslot> timeslots = app.getWebService().loadTimeslots(app.getLastSync());
        boolean result = app.getError().hasStopError();
        if (result && timeslots != null) {
            result = result && app.getDatabaseService().saveAll(timeslots, Timeslot.class);
            result = result && app.getError().hasStopError();
        }
        return result;
    }

    private boolean pushTimeslotsChangesToWeb() {
        boolean result;
        List<Timeslot> timeslots = (List<Timeslot>) app.getDatabaseService().getAllDeleted(Timeslot.class);
        result = app.getError().hasStopError();
        for (Timeslot ts : timeslots) {
            app.getWebService().deleteTimeslot(ts);
            result = result && app.getError().hasStopError();
        }

        timeslots = (List<Timeslot>) app.getDatabaseService().getAllChanged(Timeslot.class);
        result = result && app.getError().hasStopError();
        for (Timeslot ts : timeslots) {
            app.getWebService().saveTimeslot(ts);
            result = result && app.getError().hasStopError();
        }
        return result;
    }

    private boolean syncTasks() {
        boolean allOk;
        List<Task> tasks = app.getWebService().loadTasks(app.getLastSync());
        allOk = app.getError().hasStopError();
        if (allOk && tasks != null) {
            allOk = allOk && app.getDatabaseService().saveAll(tasks, Task.class);
            allOk = allOk && app.getError().hasStopError();
        }
        return allOk;
    }

    private boolean pushTaskChangesToWeb() {
        boolean result;
        List<Task> tasks = (List<Task>) app.getDatabaseService().getAllDeleted(Task.class);
        result = app.getError().hasStopError();
        for (Task t : tasks) {
            app.getWebService().deleteTask(t);
            result = result && app.getError().hasStopError();
        }

        tasks = (List<Task>) app.getDatabaseService().getAllChanged(Task.class);
        result = result && app.getError().hasStopError();
        for (Task t : tasks) {
            app.getWebService().saveTask(t);
            result = result && app.getError().hasStopError();
        }
        return result;
    }

    private boolean syncMembers() {
        boolean result;
        List<Member> members = app.getWebService().loadMembers();
        result = app.getError().hasStopError();
        if (result && members != null) {
            result = result && app.getDatabaseService().saveAll(members, Member.class);
            result = result && app.getError().hasStopError();
        }
        return result;
    }
}
