package ru.vat78.fotimetracker;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import ru.vat78.fotimetracker.database.FOTT_DBHelper;
import ru.vat78.fotimetracker.fo_api.FOAPI_Connector;

/**
 * Created by vat on 24.11.2015.
 *
 * Main application
 */
public class FOTT_App extends Application {

    public FOAPI_Connector web_service;
    private SQLiteDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        web_service = new FOAPI_Connector();
        FOTT_DBHelper helper = new FOTT_DBHelper(this);
        database = helper.getWritableDatabase();
    }

}
