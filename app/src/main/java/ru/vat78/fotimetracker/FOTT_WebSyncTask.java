package ru.vat78.fotimetracker;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

import ru.vat78.fotimetracker.connectors.database.FOTT_DBMembers;
import ru.vat78.fotimetracker.connectors.database.FOTT_DBTasks;
import ru.vat78.fotimetracker.connectors.database.FOTT_DBTimeslots;
import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Connector;
import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Members;
import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Tasks;
import ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Timeslots;
import ru.vat78.fotimetracker.controllers.FOTT_Exceptions;
import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;


public class FOTT_WebSyncTask extends AsyncTask<HashMap<String,String>, Void, FOTT_Exceptions> {
    private static final String CLASS_NAME = "FOTT_WebSyncTask";

    public static final String URL = "url";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "pwd";
    public static final String CERTIFICATES = "sa";
    public static final String TOKEN = "token";
    public static final String ONLY_TRUST = "";
    public static final String ANY_CERTS = "any";

    final private FOTT_App mainApp;
    final private FOTT_ActivityInterface parent;
    private FOAPI_Connector webService;
    private SQLiteDatabase db;
    private boolean fullSync;

    public FOTT_WebSyncTask(FOTT_ActivityInterface parent){
        mainApp = (FOTT_App) parent.getApplication();
        this.parent = parent;
    }

    @Override
    protected FOTT_Exceptions doInBackground(HashMap<String,String>... params) {
        fullSync = mainApp.isNeedFullSync();
        return dataSynchronization(params[0]);
    }

    @Override
    protected void onPostExecute(final FOTT_Exceptions result) {
        if (result == null) {
            mainApp.setNeedFullSync(false);
        }
        parent.onPostExecuteWebSyncing(result);
    }

    private FOTT_Exceptions dataSynchronization(HashMap<String,String> params){

        long stamp = System.currentTimeMillis();

        try {
            db = mainApp.getWritableDB();
            if (TextUtils.isEmpty(params.get(TOKEN))) {
                webService = new FOAPI_Connector(mainApp, params.get(URL), params.get(LOGIN),
                        params.get(PASSWORD), params.get(CERTIFICATES).equals(ONLY_TRUST));
            } else {
                webService = new FOAPI_Connector(mainApp, params.get(URL),params.get(TOKEN),
                        params.get(CERTIFICATES).equals(ONLY_TRUST));
            }

            syncMembers();
            syncTasks();
            syncTimeslots();

            db.close();
        } catch (FOTT_Exceptions e) {
            return e;
        }

        mainApp.setLastSync(stamp);
        return null;
    }

    private void syncMembers() throws FOTT_Exceptions{

        FOAPI_Members apiMembers = new FOAPI_Members(webService);
        ArrayList<FOTT_Member> members;
        members = apiMembers.loadObjects();

        FOTT_DBMembers membersDb = new FOTT_DBMembers(db);
        membersDb.rebuild();
        membersDb.saveObjects(members);

    }

    private void syncTasks() throws FOTT_Exceptions{

        FOAPI_Tasks apiTasks = new FOAPI_Tasks(webService);
        FOTT_DBTasks tasksDb = new FOTT_DBTasks(db);

        removeDeletedTasks(apiTasks, tasksDb);
        saveChangedTasks(apiTasks, tasksDb);

        ArrayList<FOTT_Task> tasks;
        if (fullSync) {
            tasks = apiTasks.loadObjects();
            tasksDb.rebuild();
        } else {
            tasks =  apiTasks.loadChangedObjects(mainApp.getLastSync());
        }

        tasksDb.saveObjects(tasks);

    }

    private void syncTimeslots() throws FOTT_Exceptions{

        FOAPI_Timeslots apiTS = new FOAPI_Timeslots(webService);
        FOTT_DBTimeslots tsDb = new FOTT_DBTimeslots(db);

        removeDeletedTimeslots(apiTS, tsDb);
        saveChangedTimeslots(apiTS, tsDb);

        ArrayList<FOTT_Timeslot> timeslots;
        if (fullSync) {
            timeslots = apiTS.loadObjects();
            tsDb.rebuild();
        } else {
            timeslots = apiTS.loadChangedObjects(mainApp.getLastSync());
        }

        tsDb.saveObjects(timeslots);
    }


    private void removeDeletedTasks(FOAPI_Tasks apiTasks, FOTT_DBTasks tasksDb) {

        ArrayList<FOTT_Task> tasks;
        try {
            tasks = (ArrayList<FOTT_Task>) tasksDb.getObjectsMarkedAsDeleted();
        } catch (FOTT_Exceptions e) {
            tasks = new ArrayList<>();
        }
        for (FOTT_Task t : tasks) {
            boolean success = true;
            try {
                apiTasks.deleteObject(t);
            } catch (FOTT_Exceptions e) {
                success = false;
            }
            if (success && !fullSync) tasksDb.deleteObject(t);
        }
    }

    private void saveChangedTasks(FOAPI_Tasks apiTasks, FOTT_DBTasks tasksDb) {

        ArrayList<FOTT_Task> tasks;
        try {
            tasks = (ArrayList<FOTT_Task>) tasksDb.loadChangedObjects(mainApp.getLastSync());
        } catch (FOTT_Exceptions e) {
            tasks = new ArrayList<>();
        }
        for (FOTT_Task t : tasks) {
            long id;
            try {
                id = apiTasks.saveObject(t);
            } catch (FOTT_Exceptions e) {
                id = 0;
            }
            if (id != 0 && !fullSync) tasksDb.deleteObject(t);
        }
    }

    private void removeDeletedTimeslots(FOAPI_Timeslots apiTS, FOTT_DBTimeslots tsDb) {

        ArrayList<FOTT_Timeslot> timeslots;
        try {
            timeslots = (ArrayList<FOTT_Timeslot>) tsDb.getObjectsMarkedAsDeleted();
        } catch (FOTT_Exceptions e) {
            timeslots = new ArrayList<>();
        }
        for (FOTT_Timeslot ts : timeslots) {
            boolean success = true;
            try {
                apiTS.deleteObject(ts);
            } catch (FOTT_Exceptions e) {
                success = false;
            }
            if (success && !fullSync) tsDb.deleteObject(ts);
        }
    }

    private void saveChangedTimeslots(FOAPI_Timeslots apiTS, FOTT_DBTimeslots tsDb) {

        ArrayList<FOTT_Timeslot> timeslots;
        try {
            timeslots = (ArrayList<FOTT_Timeslot>) tsDb.loadChangedObjects(mainApp.getLastSync());
        } catch (FOTT_Exceptions e) {
            timeslots = new ArrayList<>();
        }
        for (FOTT_Timeslot ts : timeslots) {
            long id;
            try {
                id = apiTS.saveObject(ts);
            } catch (FOTT_Exceptions e) {
                id = 0;
            }
            if (id != 0 && !fullSync) tsDb.deleteObject(ts);
        }
    }
}
