package ru.vat78.fotimetracker;

import android.content.Context;
import android.util.Log;
import ru.vat78.fotimetracker.model.ErrorsType;

/**
 * Created by vat on 28.04.17.
 */
public class ErrorsHandlerImpl implements IErrorsHandler {
    private boolean stopError;
    private Context context;

    public ErrorsHandlerImpl(Context context) {
        this.context = context;
    }

    @Override
    public void info(String className, ErrorsType type) {
        info(className, type, null);
    }

    @Override
    public void info(String className, ErrorsType type, Exception error) {
        String message = error == null ? "" : ": " + error.getMessage();
        Log.i(className, context.getString(type.getDescription()) + message);
    }

    @Override
    public void error(String className, ErrorsType type) {
        error(className, type, "");
    }

    @Override
    public void error(String className, ErrorsType type, String message) {
        stopError = true;
        Log.e(className, context.getString(type.getDescription()) + ". " + message);
    }

    @Override
    public void error(String className, ErrorsType type, Exception error) {
        error(className, type, error.getMessage());
    }

    @Override
    public boolean hasStopError() {
        return stopError;
    }

    @Override
    public void resetError() {
        stopError = false;
    }
}
