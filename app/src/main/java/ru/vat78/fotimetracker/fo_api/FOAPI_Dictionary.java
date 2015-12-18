package ru.vat78.fotimetracker.fo_api;

/**
 * Dictionary for works with FengOffice API
 */
public final class FOAPI_Dictionary {
    public FOAPI_Dictionary(){}

    public static final String FO_API_CONNECT = "index.php?c=api&m=login&username=<login>&password=<password>";
    public static final String FO_API_CHECK_PLUGIN = "index.php?c=api&m=active_plugin&plugin=<plugin>&auth=<token>";
    public static final String FO_VAPI_REQUEST = "index.php?c=vatapi&m=<method>&srv=<service>&args=<args>&auth=<token>";

    public static final String FO_API_LOGIN = "<login>";
    public static final String FO_API_PASSWORD = "<password>";
    public static final String FO_API_PLUGIN = "<plugin>";
    public static final String FO_API_METHOD = "<method>";
    public static final String FO_API_SERVICE = "<service>";
    public static final String FO_API_ARGS = "<args>";
    public static final String FO_API_TOKEN = "<token>";

    public static final String FO_PLUGIN_NAME = "vatapi";

    public static final String FO_METHOD_LISTING = "listing";
    public static final String FO_METHOD_MEMBERS = "list_members";
    public static final String FO_METHOD_SAVE_OBJ = "save_object";

    public static final String FO_SERVICE_TASKS = "ProjectTasks";
    public static final String FO_SERVICE_TASK = "ProjectTask";
    public static final String FO_SERVICE_TIMESLOTS = "Timeslots";
    public static final String FO_SERVICE_TIMESLOT = "Timeslot";

    public static final String FO_MEMBERS_WORKSPACE = "workspace";
    public static final String FO_MEMBERS_TAG = "tag";
    public static final String FO_MEMBERS_PROJECT = "customer_project";

    public static final String FO_API_FIELD_TOKEN = "token";
    public static final String FO_API_FIELD_PLUGIN_STATE = "plugin_state";

    public static final String FO_API_FIELD_ID = "id";
    public static final String FO_API_FIELD_NAME = "name";
    public static final String FO_API_FIELD_TYPE = "type";
    public static final String FO_API_FIELD_PATH = "path";
    public static final String FO_API_FIELD_COLOR = "color";
    public static final String FO_API_FIELD_DESC = "description";
    public static final String FO_API_FIELD_MEMBERS = "members";
    public static final String FO_API_FIELD_MEMPATH = "all_members";
    public static final String FO_API_FIELD_STATUS = "status";
    public static final String FO_API_FIELD_STARTDATE = "startDate";
    public static final String FO_API_FIELD_DUEDATE = "dueDate";
    public static final String FO_API_FIELD_PRIORITY = "priority";
    public static final String FO_API_FIELD_ASSIGNEDBY = "assignedById";
    public static final String FO_API_FIELD_ASSIGNEDTO = "assignedToContactId";
    public static final String FO_API_FIELD_PERCENT = "percentCompleted";
    public static final String FO_API_FIELD_WORKEDTIME = "worked_time";
    public static final String FO_API_FIELD_PENDINGTIME = "pending_time";
    public static final String FO_API_FIELD_USETIMESLOTS = "can_add_timeslots";
    public static final String FO_API_FIELD_TS_AUTHOR = "uname";
    public static final String FO_API_FIELD_TS_DESC = "desc";
    public static final String FO_API_FIELD_TS_DATE = "date";
    public static final String FO_API_FIELD_TS_DURATION = "time";
    public static final String FO_API_FIELD_TS_TASK = "ptid";
    public static final String FO_API_FIELD_LAST_UPDATE = "lastupdated";
    public static final String FO_API_FIELD_UPDATE_BY = "lastupdatedby";

    public static final String FO_API_ARG_STATUS = "status";
    public static final String FO_API_ARG_LASTUPDATE = "lupdate";

    public static final String FO_API_TRUE = "true";
    public static final String FO_API_FALSE = "false";

    public static final String FO_API_MAIN_OBJ = "fo_obj";
}
