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
        boolean hasError = app.getError().hasStopError();
        if (!hasError && timeslots != null) {
            hasError = !app.getDatabaseService().saveAll(timeslots, Timeslot.class);
            hasError = hasError || app.getError().hasStopError();
        }
        return !hasError;
    }

    private boolean pushTimeslotsChangesToWeb() {
        boolean hasError;
        List<Timeslot> timeslots = (List<Timeslot>) app.getDatabaseService().getAllDeleted(Timeslot.class);
        hasError = app.getError().hasStopError();
        for (Timeslot ts : timeslots) {
            app.getWebService().deleteTimeslot(ts);
            hasError = hasError || app.getError().hasStopError();
        }

        timeslots = (List<Timeslot>) app.getDatabaseService().getAllChanged(Timeslot.class);
        hasError = hasError || app.getError().hasStopError();
        for (Timeslot ts : timeslots) {
            app.getWebService().saveTimeslot(ts);
            hasError = hasError || app.getError().hasStopError();
        }
        return !hasError;
    }

    private boolean syncTasks() {
        boolean hasError;
        List<Task> tasks = app.getWebService().loadTasks(app.getLastSync());
        hasError = app.getError().hasStopError();
        if (!hasError && tasks != null) {
            hasError = !app.getDatabaseService().saveAll(tasks, Task.class);
            hasError = hasError || app.getError().hasStopError();
        }
        return !hasError;
    }

    private boolean pushTaskChangesToWeb() {
        boolean hasError;
        List<Task> tasks = (List<Task>) app.getDatabaseService().getAllDeleted(Task.class);
        hasError = app.getError().hasStopError();
        for (Task t : tasks) {
            app.getWebService().deleteTask(t);
            hasError = hasError || app.getError().hasStopError();
        }

        tasks = (List<Task>) app.getDatabaseService().getAllChanged(Task.class);
        hasError = hasError || app.getError().hasStopError();
        for (Task t : tasks) {
            app.getWebService().saveTask(t);
            hasError = hasError || app.getError().hasStopError();
        }
        return !hasError;
    }

    private boolean syncMembers() {
        boolean hasError;
        List<Member> members = app.getWebService().loadMembers();
        hasError = app.getError().hasStopError();
        if (!hasError && members != null) {
            hasError = !app.getDatabaseService().saveAll(members, Member.class);
            hasError = hasError || app.getError().hasStopError();
        }
        return !hasError;
    }
}
