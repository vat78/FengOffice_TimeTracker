package ru.vat78.fotimetracker.fo_api;

/**
 * Dictionary for works with FengOffice API
 */
public final class FOAPI_Dictionary {
    public FOAPI_Dictionary(){}

    public static final String FO_API_CONNECT = "index.php?c=api&m=login&username=<login>&password=<password>";
    public static final String FO_API_CHECK_PLUGIN = "index.php?c=api&m=active_plugin&plugin=<plugin>";
    public static final String FO_VAPI_REQUEST = "index.php?c=vapi&m=<method>&srv=<service>&args=<args>&auth=<token>";

    public static final String FO_API_LOGIN = "<login>";
    public static final String FO_API_PASSWORD = "<password>";
    public static final String FO_API_PLUGIN = "<plugin>";
    public static final String FO_API_METHOD = "<method>";
    public static final String FO_API_SERVICE = "<service>";
    public static final String FO_API_ARGS = "<args>";
    public static final String FO_API_TOKEN = "<token>";

    public static final String FO_PLUGIN_NAME = "vatAPI";

    public static final String FO_METHOD_LISTING = "listing";
    public static final String FO_METHOD_MEMBERS = "list_members";
    public static final String FO_METHOD_SAVE_OBJ = "save_object";

    public static final String FO_SERVICE_TASKS = "ProjectTasks";
    public static final String FO_SERVICE_TASK = "ProjectTask";
    public static final String FO_SERVICE_TIMESLOTS = "Timeslots";
    public static final String FO_SERVICE_TIMESLOT = "Timeslot";

    public static final String FO_API_FIELD_TOKEN = "token";
    public static final String FO_API_FIELD_PLUGIN_STATE = "plugin_state";
}
