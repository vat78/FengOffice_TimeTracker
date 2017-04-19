package ru.vat78.fotimetracker;

import android.os.AsyncTask;

/**
 * Created by vat on 21.12.2015.
 */
public class SyncTask extends AsyncTask<Void, Void, Boolean> {
    private static final String CLASS_NAME = "SyncTask";

    private App MainApp;
    private boolean appIsFree;

    public SyncTask(App app){
        MainApp = app;
        appIsFree = !MainApp.isSyncing();
        MainApp.setSyncing(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        appIsFree = !MainApp.isSyncing();
        MainApp.setSyncing(true);
        if (!appIsFree) return false;
        if (MainApp.getMainActivity() == null) MainApp.setNeedFullSync(true);
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
