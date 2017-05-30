package ru.vat78.fotimetracker;

import android.os.AsyncTask;

/**
 * Created by vat on 21.12.2015.
 */
public class SyncTask extends AsyncTask<App, Integer, Boolean> {
    private static final String CLASS_NAME = "SyncTask";

    private App app;
    private boolean appIsFree;

    @Override
    protected Boolean doInBackground(App... params) {
        boolean result = false;
        if (params.length > 0) {
            app = params[0];
            appIsFree = !app.isSyncing();
            app.setSyncing(true);
            if (!appIsFree) return false;
            if (app.getMainActivity() == null) app.setNeedFullSync(true);
            result = app.dataSynchronization();
        }
        return  result;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            app.setNeedFullSync(false);
            app.redrawMainActivity();
        }
        if (appIsFree) app.setSyncing(false);
    }
}
