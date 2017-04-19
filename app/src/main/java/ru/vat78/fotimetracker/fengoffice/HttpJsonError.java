package ru.vat78.fotimetracker.fengoffice;

/**
 * Created by vat on 16.04.17.
 */
public class HttpJsonError extends Exception {
    public HttpJsonError(String message) {
        super(message);
    }

    public HttpJsonError(String message, Exception parent) {
        super(message, parent);
    }
}
