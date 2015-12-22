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
}
