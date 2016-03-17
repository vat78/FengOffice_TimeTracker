package ru.vat78.fotimetracker.connectors.database;

import java.util.ArrayList;

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

    public static String arrayToString(String[] array) {
        if (array == null) {
            return "";
        }
        if (array.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(array.length * 7);
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(",");
            sb.append(array[i]);
        }
        return sb.toString();
    }

    public static String[] arrayFromString(String str) {

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
