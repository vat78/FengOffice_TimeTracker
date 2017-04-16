package ru.vat78.fotimetracker.views;

import android.util.Log;

/**
 * Created by vat on 21.12.2015.
 */
public class ErrorsHandler {

    public final static String ERROR_LOG_MESSAGE = "log";
    public final static String ERROR_SAVE_ERROR = "save";
    public final static String ERROR_SHOW_MESSAGE = "message";

    private boolean isError;
    private String error_desc;

    public ErrorsHandler() {
        reset_error();
    }

    public boolean is_error() {
        return isError;
    }

    public String getError_desc() {
        return error_desc;
    }


    public void reset_error() {
        isError = false;
        error_desc = "";
    }

    public void error_handler(String type, String module, String message){

        Log.e("FOTT." + module + ": ",message);

        switch (type) {
            case ERROR_SHOW_MESSAGE:

            case ERROR_SAVE_ERROR:
                isError = true;
                error_desc = message;
                break;
        }

    }

}
