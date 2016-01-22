package ru.vat78.fotimetracker;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.database.FOTT_DBMembers;
import ru.vat78.fotimetracker.database.FOTT_DBTasks;
import ru.vat78.fotimetracker.database.FOTT_DBTimeslots;
import ru.vat78.fotimetracker.fo_api.FOAPI_Members;
import ru.vat78.fotimetracker.fo_api.FOAPI_Tasks;
import ru.vat78.fotimetracker.fo_api.FOAPI_Timeslots;
import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
import ru.vat78.fotimetracker.views.FOTT_ErrorsHandler;

/**
 * Created by vat on 21.12.2015.
 */
public class FOTT_SyncTask extends AsyncTask<Void, Void, Boolean> {
    private static final String CLASS_NAME = "FOTT_SyncTask";

    private FOTT_App MainApp;
    private boolean appIsFree;

    public FOTT_SyncTask(FOTT_App app){
        MainApp = app;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        appIsFree = !MainApp.isSyncing();
        MainApp.setSyncing(true);
        if (!appIsFree) return false;
        return MainApp.dataSynchronization();
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            MainApp.setNeedFullSync(false);
            MainApp.redrawMainActivity();
        }
        if (appIsFree) MainApp.setSyncing(false);
    }
}
