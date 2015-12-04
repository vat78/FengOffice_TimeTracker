package ru.vat78.fotimetracker;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.util.ArrayList;

import ru.vat78.fotimetracker.database.FOTT_DBContract;
import ru.vat78.fotimetracker.database.FOTT_DBHelper;
import ru.vat78.fotimetracker.fo_api.FOAPI_Connector;
import ru.vat78.fotimetracker.fo_api.FOAPI_Members;
import ru.vat78.fotimetracker.fo_api.FOAPI_Tasks;

/**
 * Created by vat on 24.11.2015.
 *
 * Main application
 */
public class FOTT_App extends Application {

    private FOAPI_Connector web_service;
    private SQLiteDatabase database;

    private long curMember;
    private long curTask;
    private long curTimeslot;

    @Override
    public void onCreate() {
        super.onCreate();
        web_service = new FOAPI_Connector();
        FOTT_DBHelper helper = new FOTT_DBHelper(this);
        database = helper.getWritableDatabase();
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public FOAPI_Connector getWeb_service() {
        return web_service;
    }

    public long getCurMember() {
        return curMember;
    }

    public long getCurTask() {
        return curTask;
    }

    public long getCurTimeslot() {
        return curTimeslot;
    }

    public void setCurMember(long curMember) {
        this.curMember = curMember;
    }

    public void setCurTask(long curTask) {
        this.curTask = curTask;
    }

    public void setCurTimeslot(long curTimeslot) {
        this.curTimeslot = curTimeslot;
    }

    public boolean syncFOFull() {

        if (syncMembers()) {
            //If syncing works good clear links-tables
            database.execSQL(FOTT_DBContract.FOTT_DBObject_Members.SQL_DELETE_ENTRIES);
            database.execSQL(FOTT_DBContract.FOTT_DBObject_Object.SQL_DELETE_ENTRIES);
            database.execSQL(FOTT_DBContract.FOTT_DBObject_Members.SQL_CREATE_ENTRIES);
            database.execSQL(FOTT_DBContract.FOTT_DBObject_Object.SQL_CREATE_ENTRIES);

            if (syncTasks()) {
                return syncTimeslots();
            }
        }
        return false;
    }

    private boolean syncMembers() {

        if (curMember > 0){
            //TODO: if has selected member
        }

        ArrayList<ContentValues> members;
        try {
            members = FOAPI_Members.load(web_service);
            if (members == null) {return false;}
            database.execSQL(FOTT_DBContract.FOTT_DBMembers.SQL_DELETE_ENTRIES);
            database.execSQL(FOTT_DBContract.FOTT_DBMembers.SQL_CREATE_ENTRIES);

            ContentValues any = new ContentValues();
            any.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_MEMBER_ID,0);
            any.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_NAME, getString(R.string.any_category));
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
        return true;
    }

    private boolean syncTasks() {

        if (curTask > 0){
            //TODO: if has selected task
        }

        ArrayList<ContentValues> tasks;
        try {
            tasks = FOAPI_Tasks.load(web_service);
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
                            tasks.get(i).getAsString(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_MEMPATH));
                }
            }
        }
        catch (Error e){
            return  false;
        }
        calcTasksInMembers();

        return true;
    }

    private boolean syncTimeslots() {
        return true;
    }

    private void fillMembersLinks(long objId, String members) {

        if (members == null) {return;}
        if (members.length() < 2){return;}

        String memarray[] = members.split(",");
        for (int i=0; i<memarray.length; i++){
            ContentValues el = new ContentValues();
            el.put(FOTT_DBContract.FOTT_DBObject_Members.COLUMN_OBJECT_ID, objId);
            long memid = Long.parseLong(memarray[i]);
            el.put(FOTT_DBContract.FOTT_DBObject_Members.COLUMN_MEMBER_ID, memid);
            el.put((FOTT_DBContract.FOTT_DBObject_Members.COLUMN_OBJECT_TYPE),1);
            try {
                database.insert(FOTT_DBContract.FOTT_DBObject_Members.TABLE_NAME, null, el);
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
