package ru.vat78.fotimetracker.connectors.fo_api;


import ru.vat78.fotimetracker.controllers.FOTT_Exceptions;

public class FOAPI_Exceptions extends FOTT_Exceptions {

    public enum ECodes {

        NO_INTERNET,
        WRONG_URL,
        CERTIFICATE_ERROR,
        NO_FENGOFFICE,
        NO_VATAPI_PLUGIN,
        VATAPI_PLUGIN_INACTIVE,
        CREDENTIAL_ERROR,
        API_REQUEST_ERROR,

        JSON_ARRAY_MISMATCH,
        JSON_OBJECT_MISMATCH,

        API_EMPTY_DATA,
        API_FATAL_ERROR,
        API_WRONG_DATA
    }

    private final ECodes error;

    public FOAPI_Exceptions(ECodes error, ExeptionLevels level) {
        super("" , level);
        this.error = error;
    }

    public FOAPI_Exceptions(String message, ECodes error, ExeptionLevels level) {
        super(message , level);
        this.error = error;
    }

    public ECodes getError() {
        return error;
    }
}
