package ru.vat78.fotimetracker.database;

import android.provider.BaseColumns;

/**
 * DataBase structure
 */
public final class FOTT_DBContract {
    public FOTT_DBContract() {}

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FOTT.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String NUMERIC_TYPE = " NUMERIC";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String EOL = "; ";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String CREATE_TABLE = "CREATE TABLE ";

    public static abstract class FOTT_DBMembers implements BaseColumns {
        public static final String TABLE_NAME = "members";
        public static final String COLUMN_NAME_MEMBER_ID = "memberid";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_PATH = "path";
        public static final String COLUMN_NAME_PARENT = "parentid";
        public static final String COLUMN_NAME_LEVEL = "level";
        public static final String COLUMN_NAME_CHANGED = "changed";
        public static final String COLUMN_NAME_FO_CHANGED = "fochanged";
        public static final String COLUMN_NAME_FO_ENABLE = "enable";
        public static final String COLUMN_NAME_TASKS = "tasks_cnt";

        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + FOTT_DBMembers.TABLE_NAME + " (" +
                        FOTT_DBMembers._ID + " INTEGER PRIMARY KEY," +
                        FOTT_DBMembers.COLUMN_NAME_MEMBER_ID + INTEGER_TYPE + COMMA_SEP +
                        FOTT_DBMembers.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBMembers.COLUMN_NAME_COLOR + INTEGER_TYPE + COMMA_SEP +
                        FOTT_DBMembers.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBMembers.COLUMN_NAME_PATH + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBMembers.COLUMN_NAME_PARENT + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBMembers.COLUMN_NAME_LEVEL + INTEGER_TYPE + COMMA_SEP +
                        FOTT_DBMembers.COLUMN_NAME_TASKS + INTEGER_TYPE + COMMA_SEP +
                        FOTT_DBMembers.COLUMN_NAME_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_DBMembers.COLUMN_NAME_FO_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_DBMembers.COLUMN_NAME_FO_ENABLE + NUMERIC_TYPE +" );";
        public static final String SQL_DELETE_ENTRIES =
                DROP_TABLE + FOTT_DBMembers.TABLE_NAME +";";
    }

    public static abstract class FOTT_DBTasks implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_TASK_ID = "taskid";
        public static final String COLUMN_NAME_TITLE = "name";
        public static final String COLUMN_NAME_DESC = "description";
        public static final String COLUMN_NAME_MEMBERS = "members";
        public static final String COLUMN_NAME_MEMPATH = "mempath";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_STARTDATE = "startdate";
        public static final String COLUMN_NAME_DUEDATE = "duedate";
        public static final String COLUMN_NAME_PRIORITY = "priority";
        public static final String COLUMN_NAME_ASSIGNEDBY = "assignedby";
        public static final String COLUMN_NAME_ASSIGNEDTO = "assignedto";
        public static final String COLUMN_NAME_PERCENT = "percent";
        public static final String COLUMN_NAME_WORKEDTIME = "workedtime";
        public static final String COLUMN_NAME_PENDINGTIME = "pendingtime";
        public static final String COLUMN_NAME_USETIMESLOTS = "usetimeslots";

        public static final String COLUMN_NAME_CHANGED = "changed";
        public static final String COLUMN_NAME_FO_CHANGED = "fochanged";
        public static final String COLUMN_NAME_FO_ENABLE = "enable";

        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + FOTT_DBTasks.TABLE_NAME + " (" +
                        FOTT_DBTasks._ID + " INTEGER PRIMARY KEY," +
                        FOTT_DBTasks.COLUMN_NAME_TASK_ID + INTEGER_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_DESC + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_MEMBERS + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_MEMPATH + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_STATUS + INTEGER_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_STARTDATE + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_DUEDATE + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_PRIORITY + INTEGER_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_ASSIGNEDBY + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_ASSIGNEDTO + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_PERCENT + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_WORKEDTIME + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_PENDINGTIME + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_USETIMESLOTS + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_FO_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_DBTasks.COLUMN_NAME_FO_ENABLE + NUMERIC_TYPE + " );";
        public static final String SQL_DELETE_ENTRIES =
               DROP_TABLE + FOTT_DBTasks.TABLE_NAME + ";";
    }

    public static abstract class FOTT_DBTimeslots implements BaseColumns {
        public static final String TABLE_NAME = "timeslots";
        public static final String COLUMN_TIMESLOT_ID = "timeslot_ID";
        public static final String COLUMN_NAME_TITLE = "name";
        public static final String COLUMN_NAME_DESC = "description";
        public static final String COLUMN_NAME_START = "start";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_TASK_ID = "taskid";
        public static final String COLUMN_NAME_MEMBERS_ID = "membersid";
        public static final String COLUMN_NAME_CHANGED = "changed";
        public static final String COLUMN_NAME_FO_CHANGED = "fochanged";
        public static final String COLUMN_NAME_FO_ENABLE = "enable";

        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + FOTT_DBTimeslots.TABLE_NAME + " (" +
                        FOTT_DBTimeslots._ID + " INTEGER PRIMARY KEY," +
                        FOTT_DBTimeslots.COLUMN_TIMESLOT_ID + INTEGER_TYPE + COMMA_SEP +
                        FOTT_DBTimeslots.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBTimeslots.COLUMN_NAME_DESC + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBTimeslots.COLUMN_NAME_START + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_DBTimeslots.COLUMN_NAME_DURATION + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_DBTimeslots.COLUMN_NAME_TASK_ID + INTEGER_TYPE + COMMA_SEP +
                        FOTT_DBTimeslots.COLUMN_NAME_MEMBERS_ID + TEXT_TYPE + COMMA_SEP +
                        FOTT_DBTimeslots.COLUMN_NAME_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_DBTimeslots.COLUMN_NAME_FO_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_DBTimeslots.COLUMN_NAME_FO_ENABLE + NUMERIC_TYPE + " );";
        public static final String SQL_DELETE_ENTRIES =
                DROP_TABLE + FOTT_DBTimeslots.TABLE_NAME + ";";
    }

    public static abstract class FOTT_DBObject_Members implements BaseColumns {
        public static final String TABLE_NAME = "object_members";
        public static final String COLUMN_OBJECT_ID = "object_id";
        public static final String COLUMN_MEMBER_ID = "member_id";
        public static final String COLUMN_OBJECT_TYPE = "object_type";
        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + FOTT_DBObject_Members.TABLE_NAME + " (" +
                        FOTT_DBObject_Members.COLUMN_OBJECT_ID + INTEGER_TYPE + COMMA_SEP +
                        FOTT_DBObject_Members.COLUMN_MEMBER_ID + INTEGER_TYPE + COMMA_SEP +
                        FOTT_DBObject_Members.COLUMN_OBJECT_TYPE + INTEGER_TYPE + COMMA_SEP +
                        " PRIMARY KEY(" + FOTT_DBObject_Members.COLUMN_OBJECT_ID + COMMA_SEP +
                        FOTT_DBObject_Members.COLUMN_MEMBER_ID + "));";
        public static final String SQL_DELETE_ENTRIES =
                DROP_TABLE + FOTT_DBObject_Members.TABLE_NAME + ";";
    }

    public static abstract class FOTT_DBObject_Object implements BaseColumns {
        public static final String TABLE_NAME = "object_object";
        public static final String COLUMN_PARENT_ID = "parent_id";
        public static final String COLUMN_CHILD_ID = "child_id";
        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + FOTT_DBObject_Object.TABLE_NAME + " (" +
                        FOTT_DBObject_Object.COLUMN_PARENT_ID + INTEGER_TYPE + COMMA_SEP +
                        FOTT_DBObject_Object.COLUMN_CHILD_ID + INTEGER_TYPE + COMMA_SEP +
                        " PRIMARY KEY(" + FOTT_DBObject_Object.COLUMN_PARENT_ID + COMMA_SEP +
                        FOTT_DBObject_Object.COLUMN_CHILD_ID + "));";
        public static final String SQL_DELETE_ENTRIES =
                DROP_TABLE + FOTT_DBObject_Object.TABLE_NAME + ";";
    }
}
