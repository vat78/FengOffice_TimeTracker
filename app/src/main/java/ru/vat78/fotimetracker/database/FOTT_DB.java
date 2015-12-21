package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.util.ArrayList;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.fo_api.FOAPI_Members;
import ru.vat78.fotimetracker.fo_api.FOAPI_Tasks;
import ru.vat78.fotimetracker.fo_api.FOAPI_Timeslots;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
import ru.vat78.fotimetracker.views.FOTT_ErrorsHandler;

/**
 * Created by vat on 21.12.2015.
 */
public class FOTT_DB {
    private static final String CLASS_NAME = "FOTT_DB";

    private SQLiteDatabase database;
    private FOTT_App app;

    public FOTT_DB(FOTT_App application, int db_version) {

        app = application;
        FOTT_DBHelper helper = new FOTT_DBHelper(application, db_version);
        database = helper.getWritableDatabase();
        if (db_version != helper.getDb_version()) {
            //DB structure was changed and all records were deleted
            //Need to reload from web-service
            application.setNeedFullSync();
        }
    }

    public long getDb_version() {
        return FOTT_DBContract.DATABASE_VERSION;
    }

    public void execSQL(String sql){
        database.execSQL(sql);
    }

    public long insert(String table, ContentValues values){
        long res = database.insert(table,null,values);
        if (res == -1) app.getError().error_handler(FOTT_ErrorsHandler.ERROR_LOG_MESSAGE, CLASS_NAME, app.getString(R.string.db_insert_empty_row));
        return res;
    }

    public long insertOrUpdate(String table, ContentValues values){
        long res = database.insertWithOnConflict(table, null, values, database.CONFLICT_REPLACE);
        if (res == -1) app.getError().error_handler(FOTT_ErrorsHandler.ERROR_LOG_MESSAGE, CLASS_NAME, app.getString(R.string.db_insert_empty_row));
        return res;
    }

    public Cursor query(String table, String[] columns, String filter, String order){
        return database.query(table,columns,filter,null,null,null,order);
    }


    public boolean syncMembers(boolean fullSync) {

        if (app.getCurMember() > 0){
            //TODO: if has selected member
        }

        ArrayList<ContentValues> members;
        try {
            members = FOAPI_Members.load(app.getWeb_service());
            if (members == null) {return false;}
            database.execSQL(FOTT_DBContract.FOTT_DBMembers.SQL_DELETE_ENTRIES);
            database.execSQL(FOTT_DBContract.FOTT_DBMembers.SQL_CREATE_ENTRIES);

            ContentValues any = new ContentValues();
            any.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_MEMBER_ID,0);
            any.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_NAME, app.getString(R.string.any_category));
            any.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_PATH, "/");
            any.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_LEVEL,0);
            any.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_COLOR, Color.TRANSPARENT);
            members.add(any);
            for (int i = 0; i < members.size(); i++) {
                database.insert(FOTT_DBContract.FOTT_DBMembers.TABLE_NAME, null, members.get(i));
            }
        }
        catch (Error e){
            return  false;
        }
        if (fullSync) {
            //If syncing works good clear links-tables
            database.execSQL(FOTT_DBContract.FOTT_DBObject_Members.SQL_DELETE_ENTRIES);
            database.execSQL(FOTT_DBContract.FOTT_DBObject_Object.SQL_DELETE_ENTRIES);
            database.execSQL(FOTT_DBContract.FOTT_DBObject_Members.SQL_CREATE_ENTRIES);
            database.execSQL(FOTT_DBContract.FOTT_DBObject_Object.SQL_CREATE_ENTRIES);
        }
        return true;
    }


    private void fillMembersLinks(long objId, String members, int objType) {

        if (members == null) {return;}
        if (members.length() < 2){return;}

        String memarray[] = members.split(",");
        for (int i=0; i<memarray.length; i++){
            ContentValues el = new ContentValues();
            el.put(FOTT_DBContract.FOTT_DBObject_Members.COLUMN_OBJECT_ID, objId);
            long memid = Long.parseLong(memarray[i]);
            el.put(FOTT_DBContract.FOTT_DBObject_Members.COLUMN_MEMBER_ID, memid);
            el.put((FOTT_DBContract.FOTT_DBObject_Members.COLUMN_OBJECT_TYPE),objType);
            try {
                database.insertWithOnConflict(FOTT_DBContract.FOTT_DBObject_Members.TABLE_NAME, null, el, database.CONFLICT_REPLACE);
            }
            catch (Error e){
            }
        }
    }

    private void calcTasksInMembers(){

        Cursor mo = database.query(FOTT_DBContract.FOTT_DBObject_Members.TABLE_NAME,
                new String[] {FOTT_DBContract.FOTT_DBObject_Members.COLUMN_MEMBER_ID, "COUNT(" + FOTT_DBContract.FOTT_DBObject_Members.COLUMN_OBJECT_ID + ")"},
                FOTT_DBContract.FOTT_DBObject_Members.COLUMN_OBJECT_TYPE + " = 1 ", null,
                FOTT_DBContract.FOTT_DBObject_Members.COLUMN_MEMBER_ID,null,null);

        mo.moveToFirst();
        if (!mo.isAfterLast()){
            do {
                ContentValues t = new ContentValues();
                t.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_TASKS,mo.getInt(1));
                database.update(FOTT_DBContract.FOTT_DBMembers.TABLE_NAME, t,
                        FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_MEMBER_ID + " = " + mo.getString(0),null);
            } while (mo.moveToNext());
        }

        //Calc all tasks
        mo = database.query(FOTT_DBContract.FOTT_DBTasks.TABLE_NAME,
                new String[] {"COUNT(" + FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_TASK_ID + ")"},
                null, null,
                null,null,null);

        mo.moveToFirst();
        if (!mo.isAfterLast()){
            ContentValues t = new ContentValues();
            t.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_TASKS,mo.getInt(0));
            database.update(FOTT_DBContract.FOTT_DBMembers.TABLE_NAME, t,
                    FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_MEMBER_ID + " = 0 ",null);
        }
    }



}
