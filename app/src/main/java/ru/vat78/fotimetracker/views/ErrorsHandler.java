package ru.vat78.fotimetracker.views;

import android.util.Log;
import android.widget.Toast;

import ru.vat78.fotimetracker.App;

/**
 * Created by vat on 21.12.2015.
 */
public class ErrorsHandler {

    public final static String ERROR_LOG_MESSAGE = "log";
    public final static String ERROR_SAVE_ERROR = "saveAll";
    public final static String ERROR_SHOW_MESSAGE = "message";
    
    private App app;

    private boolean isError;
    private String error_desc;

    public ErrorsHandler(App app) {
        this.app = app;
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
                isError = true;
                error_desc = message;
                showToast();
                break;
                
            case ERROR_SAVE_ERROR:
                isError = true;
                error_desc = message;
                break;
                
            default:
                reset_error();
        }
    }

    private void showToast() {
        Toast toast = Toast.makeText(app,
                getError_desc(), Toast.LENGTH_SHORT);
        toast.show();
    }
}
