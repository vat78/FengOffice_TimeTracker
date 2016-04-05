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

    private final static String MessageNoInternet = "No internet connections. Please check your settings";
    private final static String MessageWrongUrl = "Cannot find FengOffice web server. Please check url of server.";
    private final static String MessageCertificate = "Your FengOffice server uses untrusted SSL certificate. If you trust this server please mark \"Allow of using untrusted certificates\"";
    private final static String MessageVATApi = "This program requires vatapi plugin installed and activated on your FengOffice server.";
    private final static String MessageCredentials = "You entered wrong user name or password.";
    private final static String MessageAPIRequest = "Cannot get data from server. Wrong request.";
    private final static String MessageAPIBadData = "Server return wrong data.";
    private final static String MessageUnknown = "There is an unknown errorCode while works with FengOffice server.";


    private final ECodes errorCode;

    public FOAPI_Exceptions(ECodes errorCode, ExceptionLevels level) {
        super(getMessageTextByCode(errorCode) , level);
        this.errorCode = errorCode;
    }

    public FOAPI_Exceptions(String message, ECodes errorCode, ExceptionLevels level) {
        super(message , level);
        this.errorCode = errorCode;
    }

    public ECodes getErrorCode() {
        return errorCode;
    }

    private static String getMessageTextByCode (ECodes errorCode) {

        switch (errorCode) {
            case NO_INTERNET:
                return MessageNoInternet;
            case WRONG_URL:
            case NO_FENGOFFICE:
                return MessageWrongUrl;
            case CERTIFICATE_ERROR:
                return MessageCertificate;
            case NO_VATAPI_PLUGIN:
            case VATAPI_PLUGIN_INACTIVE:
                return MessageVATApi;
            case CREDENTIAL_ERROR:
                return MessageCredentials;
            case API_REQUEST_ERROR:
                return MessageAPIRequest;
            case API_EMPTY_DATA:
            case API_FATAL_ERROR:
            case API_WRONG_DATA:
                return MessageAPIBadData;
            default:
                return MessageUnknown;
        }
    }
}
