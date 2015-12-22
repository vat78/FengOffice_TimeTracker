package ru.vat78.fotimetracker;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.database.FOTT_DBContract;
import ru.vat78.fotimetracker.database.FOTT_DBMembers;
import ru.vat78.fotimetracker.database.FOTT_DBTasks;
import ru.vat78.fotimetracker.database.FOTT_DBTimeslots;
import ru.vat78.fotimetracker.fo_api.FOAPI_Members;
import ru.vat78.fotimetracker.fo_api.FOAPI_Tasks;
import ru.vat78.fotimetracker.fo_api.FOAPI_Timeslots;
import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;

/**
 * Created by vat on 21.12.2015.
 */
public class FOTT_SyncTask extends AsyncTask<FOTT_App, Void, Boolean> {

    @Override
    protected Boolean doInBackground(FOTT_App... apps) {

        if (apps.length == 0) {return false;}
        FOTT_App MainApp = apps[0];
        if (!MainApp.getWeb_service().testConnection()) { return false;}

        long stamp = System.currentTimeMillis();
        boolean fullSync = MainApp.isNeedFullSync();
        Date d = (fullSync ? new Date(0) : MainApp.getLastSync());

        //Sync members
        ArrayList<FOTT_Member> members = FOAPI_Members.load(MainApp);
        if (MainApp.getError().is_error()) {return false;}
        FOTT_DBMembers.save(MainApp, members);
        if (MainApp.getError().is_error()) {return false;}

        //Sync task
        ArrayList<FOTT_Task> tasks = FOAPI_Tasks.load(MainApp, d);
        if (MainApp.getError().is_error()) {return false;}
        FOTT_DBTasks.save(MainApp,tasks,fullSync);
        if (MainApp.getError().is_error()) {return false;}

        //Sync timeslots
        ArrayList<FOTT_Timeslot> timeslots = FOAPI_Timeslots.load(MainApp, d);
        if (MainApp.getError().is_error()) {return false;}
        FOTT_DBTimeslots.save(MainApp,timeslots,fullSync);
        if (MainApp.getError().is_error()) {return false;}

        MainApp.setLastSync(stamp);
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

    }
}
