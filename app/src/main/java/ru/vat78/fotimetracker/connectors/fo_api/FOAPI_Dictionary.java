package ru.vat78.fotimetracker.connectors.fo_api;

import android.graphics.Color;

/**
 * Dictionary for works with FengOffice API
 */
public final class FOAPI_Dictionary {
    public FOAPI_Dictionary(){}

    public static final String FO_API_CONNECT = "index.php?c=api&m=login&username=<login>&password=<password>";
    public static final String FO_API_CHECK_FO = "public/tools/";
    public static final String FO_API_CHECK_PLUGIN = "index.php?c=api&m=active_plugin&plugin=<plugin>&auth=<token>";
    public static final String FO_VAPI_REQUEST = "index.php?c=vatapi&m=<method>&srv=<service>&args=<args>&auth=<token>";
    public static final String FO_VAPI_REQUEST_BY_ID = "index.php?c=vatapi&m=<method>&oid=<object_id>&action=<action>&auth=<token>";

    public static final String FO_API_LOGIN = "<login>";
    public static final String FO_API_PASSWORD = "<password>";
    public static final String FO_API_PLUGIN = "<plugin>";
    public static final String FO_API_METHOD = "<method>";
    public static final String FO_API_SERVICE = "<service>";
    public static final String FO_API_ARGS = "<args>";
    public static final String FO_API_TOKEN = "<token>";
    public static final String FO_API_OBJECT_ID = "<object_id>";
    public static final String FO_API_ACTION = "<action>";

    public static final String FO_PLUGIN_NAME = "vatapi";

    public static final String FO_METHOD_LISTING = "listing";
    public static final String FO_METHOD_MEMBERS = "list_members";
    public static final String FO_METHOD_SAVE_OBJ = "save_object";
    public static final String FO_METHOD_DELETE_OBJ = "trash";
    public static final String FO_METHOD_COMPLETE_TASK = "complete_task";

    public static final String FO_ACTION_COMPLETE_TASK = "complete";
    public static final String FO_ACTION_OPEN_TASK = "open";

    public static final String FO_SERVICE_TASKS = "ProjectTasks";
    public static final String FO_SERVICE_TIMESLOTS = "Timeslots";

    public static final String FO_MEMBERS_WORKSPACE = "workspace";
    public static final String FO_MEMBERS_TAG = "tag";
    public static final String FO_MEMBERS_PROJECT = "customer_project";

    public static final String FO_API_FIELD_TOKEN = "token";
    public static final String FO_API_FIELD_PLUGIN_STATE = "plugin_state";
    public static final String FO_API_FIELD_RESULT = "result";

    public static final String FO_API_FIELD_ID = "id";
    public static final String FO_API_FIELD_OID = "oid";
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

    public static final String FO_API_TASK_STATUS_ACTIVE = "10";

    public static final String FO_API_TRUE = "true";
    public static final String FO_API_FALSE = "false";

    public static final String FO_API_MAIN_OBJ = "fo_obj";

    public static final long FO_API_DATE_CONVERTOR = 1000;

    public static final String FO_API_MEMBER_SPLITTER = "/";

    public static final int memColors[] = {Color.GRAY,Color.argb(255,90,105,134),Color.argb(255,32,108,225),
            Color.BLUE,Color.argb(255,82,41,163),Color.argb(255,133,79,97),Color.RED,
            Color.argb(255,236,112,0),Color.argb(255,179,109,0),Color.argb(255,171,139,0),
            Color.argb(255,99,99,48),Color.argb(255,100,153,44),Color.GREEN,Color.argb(255,177,184,200),
            Color.argb(255,202,220,249),Color.argb(255,197,198,245),Color.argb(255,237,231,251),
            Color.argb(255,253,242,248),Color.argb(255,245,198,198),Color.argb(255,255,246,237),
            Color.argb(255,239,215,179),Color.argb(255,235,224,179),Color.argb(255,227,227,200),
            Color.argb(255,224,236,210),Color.argb(255,203,223,210)};
}
