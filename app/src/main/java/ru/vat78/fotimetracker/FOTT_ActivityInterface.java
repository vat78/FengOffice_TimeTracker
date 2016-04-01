package ru.vat78.fotimetracker;

import android.app.Activity;
import android.app.Application;

import ru.vat78.fotimetracker.controllers.FOTT_Exceptions;

public interface  FOTT_ActivityInterface {

    public void onPostExecuteWebSyncing(FOTT_Exceptions result);

    public  Application getApplication();
}
