package ru.vat78.fotimetracker.connectors.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.model.FOTT_Object;

import static ru.vat78.fotimetracker.connectors.database.FOTT_DBContract.*;

public class FOTT_DBMembers_Objects {

    public static final int TASK = 0;
    public static final int TIMESLOT = 1;

    public static void addObjectLinks(SQLiteDatabase db, FOTT_Object object, int objectType) {

        String[] members = object.getMembersWebIds();
        long obj_id = object.getWebId();

        if (members.length > 0 && obj_id != 0){
            for (String member : members) {
                ContentValues data = new ContentValues();
                data.put(LINKS_COLUMN_OBJECT_ID, obj_id);
                data.put(LINKS_COLUMN_MEMBER_ID, member);
                data.put(LINKS_COLUMN_OBJECT_TYPE, objectType);
                db.insertWithOnConflict(LINKS_TABLE_NAME, "", data, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }

        //Add link with member "Any"
        if (objectType == TASK && obj_id != 0) {
            ContentValues data = new ContentValues();
            data.put(LINKS_COLUMN_OBJECT_ID, obj_id);
            data.put(LINKS_COLUMN_MEMBER_ID, 0);
            data.put(LINKS_COLUMN_OBJECT_TYPE, objectType);
            db.insertWithOnConflict(LINKS_TABLE_NAME, "", data, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public static int getObjectsCntForMember(SQLiteDatabase db, long memberId, int objectType) {

        int result = 0;

        Cursor cursor = db.query(LINKS_TABLE_NAME,
                new String[] {"COUNT(" + LINKS_COLUMN_OBJECT_ID + ") as objCnt"},
                LINKS_COLUMN_MEMBER_ID + " = " + memberId + " AND " +
                        LINKS_COLUMN_OBJECT_TYPE + " = " + objectType,
                null, LINKS_COLUMN_MEMBER_ID, "", "");

        if (cursor.moveToFirst()) result = cursor.getInt(0);
        cursor.close();

        return result;
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

    public static void rebuild(SQLiteDatabase db) {
        db.execSQL(LINKS_TABLE_DELETE);
        db.execSQL(LINKS_TABLE_CREATE);
    }
}
