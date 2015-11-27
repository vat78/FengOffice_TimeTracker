package ru.vat78.fotimetracker;

import android.app.Application;

import ru.vat78.fotimetracker.fo_api.FOAPI_Connector;

/**
 * Created by vat on 24.11.2015.
 *
 * Main application
 */
public class FOTT_App extends Application {

    public FOAPI_Connector web_service;

    @Override
    public void onCreate() {
        super.onCreate();
        web_service = new FOAPI_Connector();
    }

}
