package ru.vat78.fotimetracker;

import ru.vat78.fotimetracker.model.ErrorsType;

/**
 * Created by vat on 28.04.17.
 */
public interface IErrorsHandler {
    public void info(String className, ErrorsType type);
    public void info(String className, ErrorsType type, Exception error);

    public void error(String className, ErrorsType type);
    public void error(String className, ErrorsType type, String message);
    public void error(String className, ErrorsType type, Exception error);

    public boolean hasStopError();
    public void resetError();
}
