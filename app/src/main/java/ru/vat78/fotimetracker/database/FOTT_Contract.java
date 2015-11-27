package ru.vat78.fotimetracker.database;

import android.provider.BaseColumns;

/**
 * DataBase structure
 */
public final class FOTT_Contract {
    public FOTT_Contract() {}

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FOTT.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String NUMERIC_TYPE = " NUMERIC";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String EOL = "; ";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String CREATE_TABLE = "CREATE TABLE ";

    public static abstract class FOTT_Members implements BaseColumns {
        public static final String TABLE_NAME = "members";
        public static final String COLUMN_NAME_MEMBER_ID = "memberid";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_PATH = "path";
        public static final String COLUMN_NAME_PARENT = "parentid";
        public static final String COLUMN_NAME_CHANGED = "changed";
        public static final String COLUMN_NAME_FO_CHANGED = "fochanged";

        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + FOTT_Members.TABLE_NAME + " (" +
                        FOTT_Members._ID + " INTEGER PRIMARY KEY," +
                        FOTT_Members.COLUMN_NAME_MEMBER_ID + INTEGER_TYPE + COMMA_SEP +
                        FOTT_Members.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        FOTT_Members.COLUMN_NAME_COLOR + INTEGER_TYPE + COMMA_SEP +
                        FOTT_Members.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                        FOTT_Members.COLUMN_NAME_PATH + TEXT_TYPE + COMMA_SEP +
                        FOTT_Members.COLUMN_NAME_PARENT + TEXT_TYPE + COMMA_SEP +
                        FOTT_Members.COLUMN_NAME_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_Members.COLUMN_NAME_FO_CHANGED + NUMERIC_TYPE + " )";
        public static final String SQL_DELETE_ENTRIES =
                DROP_TABLE + FOTT_Members.TABLE_NAME;
    }

    public static abstract class FOTT_Tasks implements BaseColumns {
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

        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + FOTT_Tasks.TABLE_NAME + " (" +
                        FOTT_Tasks._ID + " INTEGER PRIMARY KEY," +
                        FOTT_Tasks.COLUMN_NAME_TASK_ID + INTEGER_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_DESC + TEXT_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_MEMBERS + TEXT_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_MEMPATH + TEXT_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_STATUS + INTEGER_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_STARTDATE + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_DUEDATE + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_PRIORITY + INTEGER_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_ASSIGNEDBY + TEXT_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_ASSIGNEDTO + TEXT_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_PERCENT + TEXT_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_WORKEDTIME + TEXT_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_PENDINGTIME + TEXT_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_USETIMESLOTS + NUMERIC_TYPE +
                        FOTT_Tasks.COLUMN_NAME_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_Tasks.COLUMN_NAME_FO_CHANGED + NUMERIC_TYPE + " )";
        public static final String SQL_DELETE_ENTRIES =
               DROP_TABLE + FOTT_Tasks.TABLE_NAME;
    }

    public static abstract class FOTT_Timeslots implements BaseColumns {
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

        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + FOTT_Timeslots.TABLE_NAME + " (" +
                        FOTT_Timeslots._ID + " INTEGER PRIMARY KEY," +
                        FOTT_Timeslots.COLUMN_TIMESLOT_ID + INTEGER_TYPE + COMMA_SEP +
                        FOTT_Timeslots.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        FOTT_Timeslots.COLUMN_NAME_DESC + TEXT_TYPE + COMMA_SEP +
                        FOTT_Timeslots.COLUMN_NAME_START + NUMERIC_TYPE +
                        FOTT_Timeslots.COLUMN_NAME_DURATION + INTEGER_TYPE +
                        FOTT_Timeslots.COLUMN_NAME_TASK_ID + INTEGER_TYPE + COMMA_SEP +
                        FOTT_Timeslots.COLUMN_NAME_MEMBERS_ID + TEXT_TYPE + COMMA_SEP +
                        FOTT_Timeslots.COLUMN_NAME_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                        FOTT_Timeslots.COLUMN_NAME_FO_CHANGED + NUMERIC_TYPE + " )";
        public static final String SQL_DELETE_ENTRIES =
                DROP_TABLE + FOTT_Timeslots.TABLE_NAME;
    }


}
