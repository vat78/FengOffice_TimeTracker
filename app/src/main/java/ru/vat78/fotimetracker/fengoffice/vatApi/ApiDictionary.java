package ru.vat78.fotimetracker.fengoffice.vatApi;

/**
 * Dictionary for works with FengOffice API
 */
final class ApiDictionary {
    ApiDictionary(){}

    static final String FO_API_CONNECT = "index.php?c=api&m=login&username=<login>&password=<password>";
    static final String FO_API_CHECK_PLUGIN = "index.php?c=api&m=active_plugin&plugin=<plugin>&auth=<token>";
    static final String FO_VAPI_REQUEST = "index.php?c=vatapi&m=<method>&srv=<service>&args=<args>&auth=<token>";
    static final String FO_VAPI_REQUEST_BY_ID = "index.php?c=vatapi&m=<method>&oid=<object_id>&action=<action>&auth=<token>";

    static final String FO_API_LOGIN = "<login>";
    static final String FO_API_PASSWORD = "<password>";
    static final String FO_API_PLUGIN = "<plugin>";
    static final String FO_API_METHOD = "<method>";
    static final String FO_API_SERVICE = "<service>";
    static final String FO_API_ARGS = "<args>";
    static final String FO_API_TOKEN = "<token>";
    static final String FO_API_OBJECT_ID = "<object_id>";
    static final String FO_API_ACTION = "<action>";

    static final String FO_PLUGIN_NAME = "vatapi";

    static final String FO_METHOD_LISTING = "listing";
    static final String FO_METHOD_MEMBERS = "list_members";
    static final String FO_METHOD_SAVE_OBJ = "save_object";
    static final String FO_METHOD_DELETE_OBJ = "trash";
    static final String FO_METHOD_COMPLETE_TASK = "complete_task";

    static final String FO_ACTION_COMPLETE_TASK = "complete";
    static final String FO_ACTION_OPEN_TASK = "open";

    static final String FO_SERVICE_TASKS = "ProjectTasks";
    static final String FO_SERVICE_TIMESLOTS = "Timeslots";

    static final String FO_MEMBERS_WORKSPACE = "workspace";
    static final String FO_MEMBERS_TAG = "tag";
    static final String FO_MEMBERS_PROJECT = "customer_project";

    static final String FO_API_FIELD_TOKEN = "token";
    static final String FO_API_FIELD_PLUGIN_STATE = "plugin_state";
    static final String FO_API_FIELD_RESULT = "result";

    static final String FO_API_FIELD_ID = "id";
    static final String FO_API_FIELD_OID = "oid";
    static final String FO_API_FIELD_NAME = "name";
    static final String FO_API_FIELD_TYPE = "type";
    static final String FO_API_FIELD_PATH = "path";
    static final String FO_API_FIELD_COLOR = "color";
    static final String FO_API_FIELD_DESC = "description";
    static final String FO_API_FIELD_MEMBERS = "members";
    static final String FO_API_FIELD_MEMPATH = "all_members";
    static final String FO_API_FIELD_STATUS = "status";
    static final String FO_API_FIELD_STARTDATE = "startDate";
    static final String FO_API_FIELD_DUEDATE = "dueDate";
    static final String FO_API_FIELD_PRIORITY = "priority";
    static final String FO_API_FIELD_ASSIGNEDBY = "assignedById";
    static final String FO_API_FIELD_ASSIGNEDTO = "assignedToContactId";
    static final String FO_API_FIELD_PERCENT = "percentCompleted";
    static final String FO_API_FIELD_WORKEDTIME = "worked_time";
    static final String FO_API_FIELD_PENDINGTIME = "pending_time";
    static final String FO_API_FIELD_USETIMESLOTS = "can_add_timeslots";
    static final String FO_API_FIELD_TS_AUTHOR = "uname";
    static final String FO_API_FIELD_TS_DESC = "desc";
    static final String FO_API_FIELD_TS_DATE = "date";
    static final String FO_API_FIELD_TS_DURATION = "time";
    static final String FO_API_FIELD_TS_TASK = "ptid";
    static final String FO_API_FIELD_LAST_UPDATE = "lastupdated";
    static final String FO_API_FIELD_UPDATE_BY = "lastupdatedby";

    static final String FO_API_ARG_STATUS = "status";
    static final String FO_API_ARG_LASTUPDATE = "lupdate";

    static final String FO_API_TRUE = "true";
    static final String FO_API_FALSE = "false";

    static final String FO_API_MAIN_OBJ = "fo_obj";

    static final long FO_API_DATE_CONVERTOR = 1000;
}
