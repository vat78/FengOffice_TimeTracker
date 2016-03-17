package ru.vat78.fotimetracker.connectors.database;

import android.content.ContentValues;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.model.FOTT_Object;

/**
 * Created by vat on 21.12.2015.
 */
public class FOTT_DBMembers_Objects extends FOTT_DBContract {

    private static final String TABLE_NAME = "members_objects";
    private static final String COLUMN_MEMBER_ID = "member_id";
    private static final String COLUMN_OBJECT_ID = "object_id";
    public static final String COLUMN_OBJECT_TYPE = "object_type";
    private static final String SQL_CREATE_ENTRIES =
            CREATE_TABLE + TABLE_NAME + " (" +
                    COLUMN_OBJECT_ID + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_MEMBER_ID + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_OBJECT_TYPE + INTEGER_TYPE + COMMA_SEP +
                    PRIMARY_KEY + "(" + COLUMN_OBJECT_ID + COMMA_SEP +
                    COLUMN_MEMBER_ID + "));";
    private static final String SQL_DELETE_ENTRIES =
            DROP_TABLE + TABLE_NAME + ";";

    public static void addObject(FOTT_App app, FOTT_Object object, int object_type) {

        String[] members = object.getMembersWebIds();
        long obj_id = object.getWebId();

        if (members.length > 0 && obj_id != 0){
            for (String member : members) {
                ContentValues data = new ContentValues();
                data.put(COLUMN_OBJECT_ID, obj_id);
                data.put(COLUMN_MEMBER_ID, member);
                data.put(COLUMN_OBJECT_TYPE, object_type);
                app.getDatabase().insertOrUpdate(TABLE_NAME, data);
            }
        }
        //Add link with member "Any"
        if (object_type == 1 && obj_id != 0) {
            ContentValues data = new ContentValues();
            data.put(COLUMN_OBJECT_ID, obj_id);
            data.put(COLUMN_MEMBER_ID, 0);
            data.put(COLUMN_OBJECT_TYPE, object_type);
            app.getDatabase().insertOrUpdate(TABLE_NAME, data);
        }
    }

    public static String getSQLCondition(long member_id, int object_type){
        return "SELECT  " +
                COLUMN_OBJECT_ID + " FROM " +
                TABLE_NAME + " WHERE " +
                COLUMN_MEMBER_ID + " = " +
                String.valueOf(member_id) + " AND " +
                COLUMN_OBJECT_TYPE + " = " + String.valueOf(object_type);
    }

    public static String getSQLObectsCnt(String parent_id) {
        return "SELECT COUNT(" +
                "o." + COLUMN_OBJECT_ID +
                ") FROM " + TABLE_NAME +
                " o WHERE o." + COLUMN_OBJECT_TYPE + " = 1 AND " +
                COLUMN_MEMBER_ID + " = " + parent_id;
    }

    public static void rebuild(FOTT_App app) {
        app.getDatabase().execSQL(SQL_DELETE_ENTRIES);
        app.getDatabase().execSQL(SQL_CREATE_ENTRIES);
    }
}
