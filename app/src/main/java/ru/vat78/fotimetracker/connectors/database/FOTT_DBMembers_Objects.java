package ru.vat78.fotimetracker.connectors.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.model.FOTT_Object;

/**
 * Created by vat on 21.12.2015.
 */
public class FOTT_DBMembers_Objects {



    public static void addObject(SQLiteDatabase db, FOTT_Object object, int object_type) {

        String[] members = object.getMembersWebIds();
        long obj_id = object.getWebId();

        if (members.length > 0 && obj_id != 0){
            for (String member : members) {
                ContentValues data = new ContentValues();
                data.put(LINKS_COLUMN_OBJECT_ID, obj_id);
                data.put(LINKS_COLUMN_MEMBER_ID, member);
                data.put(LINKS_COLUMN_OBJECT_TYPE, object_type);
                app.getDatabase().insertOrUpdate(LINKS_TABLE_NAME, data);
            }
        }
        //Add link with member "Any"
        if (object_type == 1 && obj_id != 0) {
            ContentValues data = new ContentValues();
            data.put(LINKS_COLUMN_OBJECT_ID, obj_id);
            data.put(LINKS_COLUMN_MEMBER_ID, 0);
            data.put(LINKS_COLUMN_OBJECT_TYPE, object_type);
            app.getDatabase().insertOrUpdate(LINKS_TABLE_NAME, data);
        }
    }

    public static String getSQLCondition(long member_id, int object_type){
        return "SELECT  " +
                LINKS_COLUMN_OBJECT_ID + " FROM " +
                LINKS_TABLE_NAME + " WHERE " +
                LINKS_COLUMN_MEMBER_ID + " = " +
                String.valueOf(member_id) + " AND " +
                LINKS_COLUMN_OBJECT_TYPE + " = " + String.valueOf(object_type);
    }

    public static String getSQLObectsCnt(String parent_id) {
        return "SELECT COUNT(" +
                "o." + LINKS_COLUMN_OBJECT_ID +
                ") FROM " + LINKS_TABLE_NAME +
                " o WHERE o." + LINKS_COLUMN_OBJECT_TYPE + " = 1 AND " +
                LINKS_COLUMN_MEMBER_ID + " = " + parent_id;
    }

    public static void rebuild(FOTT_App app) {
        app.getDatabase().execSQL(LINKS_TABLE_DELETE);
        app.getDatabase().execSQL(LINKS_TABLE_CREATE);
    }
}
