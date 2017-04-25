package ru.vat78.fotimetracker.database;

import android.provider.BaseColumns;

/**
 * DataBase structure
 */
class DBContract {
    public DBContract() {}

    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "FOTT.db";

    static final String TEXT_TYPE = " TEXT";
    static final String NUMERIC_TYPE = " NUMERIC";
    static final String INTEGER_TYPE = " INTEGER";
    static final String COMMA_SEP = ",";
    static final String PRIMARY_KEY = " PRIMARY KEY";
    static final String UNIQUE_FIELD = " UNIQUE";

    static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";

    static final String COLUMN_NAME_FO_ID = "fo_id";
    static final String COLUMN_NAME_TITLE = "name";
    static final String COLUMN_NAME_DESC = "description";
    static final String COLUMN_NAME_MEMBERS_IDS = "members_ids";
    static final String COLUMN_NAME_CHANGED = "changed";
    static final String COLUMN_NAME_CHANGED_BY = "changed_by";
    static final String COLUMN_NAME_DELETED = "deleted";
    
    //-----------------------------------------------------------------------------------------------------
    static class MembersTable {
        static final String CLASS_NAME = "DaoMembers";

        static final String TABLE_NAME = "members";
        static final String COLUMN_NAME_COLOR = "color";
        static final String COLUMN_NAME_TYPE = "type";
        static final String COLUMN_NAME_PATH = "path";
        static final String COLUMN_NAME_PARENT = "parentid";
        static final String COLUMN_NAME_LEVEL = "level";
        static final String COLUMN_NAME_TASKS = "tasks_cnt";

        static final String SQL_CREATE_TABLE =
                CREATE_TABLE + TABLE_NAME + " (" +
                        BaseColumns._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                        COLUMN_NAME_FO_ID + INTEGER_TYPE + UNIQUE_FIELD + COMMA_SEP +
                        COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_COLOR + INTEGER_TYPE + COMMA_SEP +
                        COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PATH + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PARENT + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_LEVEL + INTEGER_TYPE + COMMA_SEP +
                        COLUMN_NAME_TASKS + INTEGER_TYPE + COMMA_SEP +
                        COLUMN_NAME_CHANGED + NUMERIC_TYPE +
                        " );";
        static final String SQL_DELETE_ENTRIES =
                DROP_TABLE + TABLE_NAME +";";
    }
    
    //-----------------------------------------------------------------------------------------------------
    static class TasksTable {
        static final String TABLE_NAME = "tasks";
        static final String COLUMN_NAME_STATUS = "status";
        static final String COLUMN_NAME_STARTDATE = "startdate";
        static final String COLUMN_NAME_DUEDATE = "duedate";
        static final String COLUMN_NAME_PRIORITY = "priority";
        static final String COLUMN_NAME_ASSIGNEDBY = "assignedby";
        static final String COLUMN_NAME_ASSIGNEDTO = "assignedto";
        static final String COLUMN_NAME_PERCENT = "percent";
        static final String COLUMN_NAME_WORKEDTIME = "workedtime";
        static final String COLUMN_NAME_PENDINGTIME = "pendingtime";
        static final String COLUMN_NAME_USETIMESLOTS = "usetimeslots";

        static final String SQL_CREATE_TABLE =
                CREATE_TABLE + TABLE_NAME + " (" +
                        BaseColumns._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                        COLUMN_NAME_FO_ID + INTEGER_TYPE + UNIQUE_FIELD + COMMA_SEP +
                        COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_DESC + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_MEMBERS_IDS + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_STATUS + INTEGER_TYPE + COMMA_SEP +
                        COLUMN_NAME_STARTDATE + NUMERIC_TYPE + COMMA_SEP +
                        COLUMN_NAME_DUEDATE + NUMERIC_TYPE + COMMA_SEP +
                        COLUMN_NAME_PRIORITY + INTEGER_TYPE + COMMA_SEP +
                        COLUMN_NAME_ASSIGNEDBY + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_ASSIGNEDTO + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PERCENT + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_WORKEDTIME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PENDINGTIME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_USETIMESLOTS + NUMERIC_TYPE + COMMA_SEP +
                        COLUMN_NAME_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                        COLUMN_NAME_DELETED + NUMERIC_TYPE +
                        " );";
        static final String SQL_DELETE_ENTRIES =
                DROP_TABLE + TABLE_NAME + ";";
    }

    //-----------------------------------------------------------------------------------------------------
    static class TimeslotsTable{
        static final String TABLE_NAME = "timeslots";
        static final String COLUMN_NAME_START = "start";
        static final String COLUMN_NAME_DURATION = "duration";
        static final String COLUMN_NAME_TASK_ID = "task_id";

        static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + TABLE_NAME + " (" +
                        BaseColumns._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                        COLUMN_NAME_FO_ID + INTEGER_TYPE + UNIQUE_FIELD + COMMA_SEP +
                        COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_DESC + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_START + NUMERIC_TYPE + COMMA_SEP +
                        COLUMN_NAME_DURATION + NUMERIC_TYPE + COMMA_SEP +
                        COLUMN_NAME_TASK_ID + INTEGER_TYPE + COMMA_SEP +
                        COLUMN_NAME_MEMBERS_IDS + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                        COLUMN_NAME_CHANGED_BY + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_DELETED + NUMERIC_TYPE + " );";
        static final String SQL_DELETE_ENTRIES =
                DROP_TABLE + TABLE_NAME + ";";
    }
    
    //-----------------------------------------------------------------------------------------------------
    static class MemberObjectsTable {
        static final String TABLE_NAME = "members_objects";
        static final String COLUMN_MEMBER_ID = "member_id";
        static final String COLUMN_OBJECT_ID = "object_id";
        static final String COLUMN_OBJECT_TYPE = "object_type";
        static final String SQL_CREATE_TABLE =
                CREATE_TABLE + TABLE_NAME + " (" +
                        COLUMN_OBJECT_ID + INTEGER_TYPE + COMMA_SEP +
                        COLUMN_MEMBER_ID + INTEGER_TYPE + COMMA_SEP +
                        COLUMN_OBJECT_TYPE + INTEGER_TYPE + COMMA_SEP +
                        PRIMARY_KEY + "(" + COLUMN_OBJECT_ID + COMMA_SEP +
                        COLUMN_MEMBER_ID + "));";
        static final String SQL_DELETE_ENTRIES =
                DROP_TABLE + TABLE_NAME + ";";
    }
}
