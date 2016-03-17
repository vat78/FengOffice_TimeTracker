package ru.vat78.fotimetracker.connectors.fo_api;


import ru.vat78.fotimetracker.controllers.FOTT_Exceptions;

public class FOAPI_Exceptions extends FOTT_Exceptions {

    public static final String JSON_ARRAY_MISMATCH = "Web service must return JSON array";
    public static final String JSON_OBJECT_MISMATCH = "Web service must return JSON object";
    public static final String OBJECT_DELETING_ERROR = "Couldn't delete object";

    public FOAPI_Exceptions(String detailMessage, ExeptionLevels level) {
        super(detailMessage, level);
    }
}
