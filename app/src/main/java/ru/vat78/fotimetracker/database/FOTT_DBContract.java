package ru.vat78.fotimetracker.database;

/**
 * DataBase structure
 */
public class FOTT_DBContract {
    public FOTT_DBContract() {}

    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "FOTT.db";

    protected static final String TEXT_TYPE = " TEXT";
    protected static final String NUMERIC_TYPE = " NUMERIC";
    protected static final String INTEGER_TYPE = " INTEGER";
    protected static final String COMMA_SEP = ",";
    protected static final String EOL = "; ";
    protected static final String PRIMARY_KEY = " PRIMARY KEY";
    protected static final String UNIQUE_FIELD = " UNIQUE";

    protected static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    protected static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";

    protected static final String COLUMN_NAME_FO_ID = "fo_id";
    protected static final String COLUMN_NAME_TITLE = "name";
    protected static final String COLUMN_NAME_DESC = "description";
    protected static final String COLUMN_NAME_MEMBERS_IDS = "members_ids";
    protected static final String COLUMN_NAME_CHANGED = "changed";
    protected static final String COLUMN_NAME_CHANGED_BY = "changed_by";
    protected static final String COLUMN_NAME_DELETED = "deleted";

}
