package ru.vat78.fotimetracker.connectors.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * DataBase structure
 */
public final class FOTT_DBContract {

    private static SQLiteDatabase db;

    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "FOTT.db";

    static final String TEXT_TYPE = " TEXT";
    static final String NUMERIC_TYPE = " NUMERIC";
    static final String INTEGER_TYPE = " INTEGER";
    static final String COMMA_SEP = ",";
    static final String EOL = "; ";
    static final String PRIMARY_KEY = " PRIMARY KEY";
    static final String UNIQUE_FIELD = " UNIQUE";

    static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";

    /***********************************************************
     *                    COMMON COLUMNS
     ***********************************************************/

    static final String COMMON_COLUMN_ID = BaseColumns._ID;
    static final String COMMON_COLUMN_FO_ID = "fo_id";
    static final String COMMON_COLUMN_TITLE = "name";
    static final String COMMON_COLUMN_DESC = "description";
    static final String COMMON_COLUMN_MEMBERS_IDS = "members_ids";
    static final String COMMON_COLUMN_CHANGED = "changed";
    static final String COMMON_COLUMN_CHANGED_BY = "changed_by";
    static final String COMMON_COLUMN_DELETED = "deleted";

    /***********************************************************
     *                        MEMBERS
     ***********************************************************/

    static final String MEMBERS_TABLE_NAME = "members";
    static final String MEMBERS_COLUMN_COLOR = "color";
    static final String MEMBERS_COLUMN_TYPE = "type";
    static final String MEMBERS_COLUMN_PATH = "path";
    static final String MEMBERS_COLUMN_PARENT = "parentid";
    static final String MEMBERS_COLUMN_LEVEL = "level";
    static final String MEMBERS_COLUMN_TASKS = "tasks_cnt";

    static final String MEMBERS_TABLE_CREATE =
            CREATE_TABLE + MEMBERS_TABLE_NAME + " (" +
                    COMMON_COLUMN_ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                    COMMON_COLUMN_FO_ID + INTEGER_TYPE + UNIQUE_FIELD + COMMA_SEP +
                    COMMON_COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    MEMBERS_COLUMN_COLOR + INTEGER_TYPE + COMMA_SEP +
                    MEMBERS_COLUMN_TYPE + TEXT_TYPE + COMMA_SEP +
                    MEMBERS_COLUMN_PATH + TEXT_TYPE + COMMA_SEP +
                    MEMBERS_COLUMN_PARENT + TEXT_TYPE + COMMA_SEP +
                    MEMBERS_COLUMN_LEVEL + INTEGER_TYPE + COMMA_SEP +
                    MEMBERS_COLUMN_TASKS + INTEGER_TYPE + COMMA_SEP +
                    COMMON_COLUMN_CHANGED + NUMERIC_TYPE +
                    " )" + EOL;
    static final String MEMBERS_TABLE_DELETE =
            DROP_TABLE + MEMBERS_TABLE_NAME + EOL;

    /***********************************************************
     *                        TASKS
     ***********************************************************/

    static final String TASK_TABLE_NAME = "tasks";
    static final String TASK_COLUMN_STATUS = "status";
    static final String TASK_COLUMN_STARTDATE = "startdate";
    static final String TASK_COLUMN_DUEDATE = "duedate";
    static final String TASK_COLUMN_PRIORITY = "priority";
    static final String TASK_COLUMN_ASSIGNEDBY = "assignedby";
    static final String TASK_COLUMN_ASSIGNEDTO = "assignedto";
    static final String TASK_COLUMN_PERCENT = "percent";
    static final String TASK_COLUMN_WORKEDTIME = "workedtime";
    static final String TASK_COLUMN_PENDINGTIME = "pendingtime";
    static final String TASK_COLUMN_USETIMESLOTS = "usetimeslots";

    static final String TASK_TABLE_CREATE =
            CREATE_TABLE + TASK_TABLE_NAME + " (" +
                    COMMON_COLUMN_ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                    COMMON_COLUMN_FO_ID + INTEGER_TYPE + UNIQUE_FIELD + COMMA_SEP +
                    COMMON_COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    COMMON_COLUMN_DESC + TEXT_TYPE + COMMA_SEP +
                    COMMON_COLUMN_MEMBERS_IDS + TEXT_TYPE + COMMA_SEP +
                    TASK_COLUMN_STATUS + INTEGER_TYPE + COMMA_SEP +
                    TASK_COLUMN_STARTDATE + NUMERIC_TYPE + COMMA_SEP +
                    TASK_COLUMN_DUEDATE + NUMERIC_TYPE + COMMA_SEP +
                    TASK_COLUMN_PRIORITY + INTEGER_TYPE + COMMA_SEP +
                    TASK_COLUMN_ASSIGNEDBY + TEXT_TYPE + COMMA_SEP +
                    TASK_COLUMN_ASSIGNEDTO + TEXT_TYPE + COMMA_SEP +
                    TASK_COLUMN_PERCENT + TEXT_TYPE + COMMA_SEP +
                    TASK_COLUMN_WORKEDTIME + TEXT_TYPE + COMMA_SEP +
                    TASK_COLUMN_PENDINGTIME + TEXT_TYPE + COMMA_SEP +
                    TASK_COLUMN_USETIMESLOTS + NUMERIC_TYPE + COMMA_SEP +
                    COMMON_COLUMN_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                    COMMON_COLUMN_DELETED + NUMERIC_TYPE +
                    " )" + EOL;
    static final String TASK_TABLE_DELETE =
            DROP_TABLE + TASK_TABLE_NAME + EOL;

    /***********************************************************
     *                 TIMESLOTS
     ***********************************************************/

    static final String TIMESLOTS_TABLE_NAME = "timeslots";
    static final String TS_COLUMN_START = "start";
    static final String TS_COLUMN_DURATION = "duration";
    static final String TS_COLUMN_TASK_ID = "task_id";

    static final String TIMESLOTS_TABLE_CREATE =
            CREATE_TABLE + TIMESLOTS_TABLE_NAME + " (" +
                    COMMON_COLUMN_ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                    COMMON_COLUMN_FO_ID + INTEGER_TYPE + UNIQUE_FIELD + COMMA_SEP +
                    COMMON_COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    COMMON_COLUMN_DESC + TEXT_TYPE + COMMA_SEP +
                    TS_COLUMN_START + NUMERIC_TYPE + COMMA_SEP +
                    TS_COLUMN_DURATION + NUMERIC_TYPE + COMMA_SEP +
                    TS_COLUMN_TASK_ID + INTEGER_TYPE + COMMA_SEP +
                    COMMON_COLUMN_MEMBERS_IDS + TEXT_TYPE + COMMA_SEP +
                    COMMON_COLUMN_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                    COMMON_COLUMN_CHANGED_BY + TEXT_TYPE + COMMA_SEP +
                    COMMON_COLUMN_DELETED + NUMERIC_TYPE + " )" + EOL;
    static final String TIMESLOTS_TABLE_DELETE =
            DROP_TABLE + TIMESLOTS_TABLE_NAME + EOL;

    /***********************************************************
     *              LINKS OBJECTS WITH MEMBERS
     ***********************************************************/

    static final String LINKS_TABLE_NAME = "members_objects";
    static final String LINKS_COLUMN_MEMBER_ID = "member_id";
    static final String LINKS_COLUMN_OBJECT_ID = "object_id";
    static final String LINKS_COLUMN_OBJECT_TYPE = "object_type";
    static final String LINKS_TABLE_CREATE =
            CREATE_TABLE + LINKS_TABLE_NAME + " (" +
                    LINKS_COLUMN_OBJECT_ID + INTEGER_TYPE + COMMA_SEP +
                    LINKS_COLUMN_MEMBER_ID + INTEGER_TYPE + COMMA_SEP +
                    LINKS_COLUMN_OBJECT_TYPE + INTEGER_TYPE + COMMA_SEP +
                    PRIMARY_KEY + "(" + LINKS_COLUMN_OBJECT_ID + COMMA_SEP +
                    LINKS_COLUMN_MEMBER_ID + "))" + EOL;
    static final String LINKS_TABLE_DELETE =
            DROP_TABLE + LINKS_TABLE_NAME + EOL;


    public static void createDb(Context context) {


    }

    static SQLiteDatabase getDb() {return db;}

    static String arrayToString(String[] array) {
        if (array == null) {
            return "";
        }
        if (array.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(array.length * 2);
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(",");
            sb.append(array[i]);
        }
        return sb.toString();
    }

    static String[] arrayFromString(String str) {

        if (str == null) {
            return new String[]{};
        }

        ArrayList<String> res = new ArrayList<>();
        int i = str.indexOf(",");
        while (i>=0) {
            if (i>0) res.add(str.substring(0,i-1));
            str = str.substring(i+1);
            i = str.indexOf(",");
        }
        res.add(str);
        return (String[]) res.toArray();
    }
}
