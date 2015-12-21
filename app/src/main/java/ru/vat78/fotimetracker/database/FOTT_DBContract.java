package ru.vat78.fotimetracker.database;

import android.provider.BaseColumns;

/**
 * DataBase structure
 */
public class FOTT_DBContract {
    public FOTT_DBContract() {}

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "FOTT.db";

    protected static final String TEXT_TYPE = " TEXT";
    protected static final String NUMERIC_TYPE = " NUMERIC";
    protected static final String INTEGER_TYPE = " INTEGER";
    protected static final String COMMA_SEP = ",";
    protected static final String EOL = "; ";
    protected static final String PRIMARY_KEY = " PRIMARY KEY";

    protected static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    protected static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";

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

}
