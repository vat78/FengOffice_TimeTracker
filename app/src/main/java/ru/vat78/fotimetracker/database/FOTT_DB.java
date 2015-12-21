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

/**
 * Created by vat on 21.12.2015.
 */
public class FOTT_DB {

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
    public boolean syncTasks(boolean fullSync) {

        if (app.getCurTask() > 0){
            //TODO: if has selected task
        }

        ArrayList<ContentValues> tasks;
        try {
            tasks = FOAPI_Tasks.load(app.getWeb_service());
            if (tasks == null) {return false;}
            database.execSQL(FOTT_DBContract.FOTT_DBTasks.SQL_DELETE_ENTRIES);
            database.execSQL(FOTT_DBContract.FOTT_DBTasks.SQL_CREATE_ENTRIES);

            ContentValues any = new ContentValues();
            any.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_TASK_ID,0);
            any.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_TITLE,"..");
            tasks.add(any);
            for (int i = 0; i < tasks.size(); i++) {
                database.insert(FOTT_DBContract.FOTT_DBTasks.TABLE_NAME, null, tasks.get(i));
                if (tasks.get(i).containsKey(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_MEMPATH)) {
                    fillMembersLinks(tasks.get(i).getAsLong(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_TASK_ID),
                            tasks.get(i).getAsString(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_MEMPATH),1);
                }
            }
        }
        catch (Error e){
            return  false;
        }
        calcTasksInMembers();

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

    public boolean syncTimeslots(boolean fullSync) {

        ArrayList<FOTT_Timeslot> ts;
        try {
            if (fullSync) {
                ts = app.getWeb_service().loadTimeslots(null);
            } else {
                ts = app.getWeb_service().loadTimeslots(app.getLastSync());
            }
            if (ts == null) {return false;}
            database.execSQL(FOTT_DBContract.FOTT_DBTimeslots.SQL_DELETE_ENTRIES);
            database.execSQL(FOTT_DBContract.FOTT_DBTimeslots.SQL_CREATE_ENTRIES);

            for (int i = 0; i < ts.size(); i++) {
                database.insert(FOTT_DBContract.FOTT_DBTimeslots.TABLE_NAME, null, ts.get(i));
                if (ts.get(i).containsKey(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_MEMBERS_ID)) {
                    if (!ts.get(i).containsKey(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_TASK_ID))
                        fillMembersLinks(ts.get(i).getAsLong(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_TIMESLOT_ID),
                                ts.get(i).getAsString(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_MEMBERS_ID),2);
                }
            }
        }
        catch (Error e){
            return  false;
        }

        return true;
    }

}
