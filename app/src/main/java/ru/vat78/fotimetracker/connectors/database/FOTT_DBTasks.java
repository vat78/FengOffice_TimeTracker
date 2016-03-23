package ru.vat78.fotimetracker.connectors.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.connectors.FOTT_ObjectsConnector;
import ru.vat78.fotimetracker.model.FOTT_Object;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.model.FOTT_TaskBuilder;

import static ru.vat78.fotimetracker.connectors.database.FOTT_DBContract.*;


public class FOTT_DBTasks implements FOTT_ObjectsConnector {
    private static final String CLASS_NAME ="FOTT_DBTasks";

    private final SQLiteDatabase db;

    FOTT_DBTasks(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public ArrayList<FOTT_Task> loadObjects() {
        return loadFilteredObjects("");
    }

    @Override
    public ArrayList<FOTT_Task> loadFilteredObjects(String filter) {

        ArrayList<FOTT_Task> tasks = new ArrayList<>();

        Cursor taskCursor = db.query(TASK_TABLE_NAME,
                new String[]{COMMON_COLUMN_ID,
                        COMMON_COLUMN_FO_ID,
                        COMMON_COLUMN_TITLE,
                        TASK_COLUMN_DUEDATE,
                        TASK_COLUMN_STATUS,
                        COMMON_COLUMN_CHANGED},
                filter, null, "", "",
                TASK_COLUMN_DUEDATE + " ASC");

        taskCursor.moveToFirst();
        FOTT_TaskBuilder t;
        if (!taskCursor.isAfterLast()) {
            do {
                t = new FOTT_TaskBuilder();
                t.setDbID(taskCursor.getLong(0));
                t.setWebID(taskCursor.getLong(1));
                t.setName(taskCursor.getString(2));
                t.setDueDate(taskCursor.getLong(3));
                t.setStatus(taskCursor.getInt(4));
                t.setChanged(taskCursor.getLong(5));
                tasks.add(t.buildObject());
            } while (taskCursor.moveToNext());
        }
        taskCursor.close();

        return tasks;
    }

    @Override
    public ArrayList<FOTT_Task> loadChangedObjects(Date milestone) {
        String filter = COMMON_COLUMN_CHANGED + " >= " + milestone.getTime();
        return loadFilteredObjects(filter);
    }

    @Override
    public FOTT_Task loadObject(long objectId) {

        if (objectId != 0) {

            String filter = COMMON_COLUMN_FO_ID + " = " + objectId;

            ArrayList<FOTT_Task> result = loadFilteredObjects(filter);
            if (result.size() > 0) return result.get(0);
        }

        return null;
    }

    @Override
    public long saveObject(FOTT_Object savingObject) {

        long result = 0;
        if (savingObject == null) return result;

        FOTT_Task task = (FOTT_Task) savingObject;

        ContentValues data = convertToDB(task);
        result = db.insertWithOnConflict(TASK_TABLE_NAME, "", data, db.CONFLICT_REPLACE);

        if (result !=0 && task.getMembersWebIds().length > 0) {
            FOTT_DBMembers_Objects.addObject(db, task, 1);
        }

        return result;
    }

    @Override
    public boolean saveObjects(ArrayList<? extends FOTT_Object> savingObjects) {

        boolean result = true;
        for (FOTT_Object obj : savingObjects) {

            result = result && (saveObject(obj) != 0);
        }
        return result;
    }

    @Override
    public boolean saveChangedObjects(ArrayList<? extends FOTT_Object> savingObjects, Date milestone) {

        boolean result = true;
        for (FOTT_Object obj : savingObjects) {

            if (obj.getChanged().after(milestone))
                result = result && (saveObject(obj) != 0);
        }
        return result;
    }

    @Override
    public boolean deleteObjects(ArrayList<? extends FOTT_Object> deletingObjects) { return false; }

    @Override
    public boolean deleteObject(FOTT_Object deletingObject) {
        return db.delete(TASK_TABLE_NAME,
                COMMON_COLUMN_ID + " = " + deletingObject.getDbID(), null) == 0;
    }




    public static void rebuild(FOTT_App app){
        app.getDatabase().execSQL(TASK_TABLE_DELETE);
        app.getDatabase().execSQL(TASK_TABLE_CREATE);
    }



    private static ContentValues convertToDB(FOTT_Task task) {
        ContentValues res = new ContentValues();

        if (task.getDbID() != 0) res.put(COMMON_COLUMN_ID, task.getDbID());
        if (task.getWebId() != 0) res.put(COMMON_COLUMN_FO_ID, task.getWebId());
        res.put(COMMON_COLUMN_TITLE, task.getName());
        res.put(COMMON_COLUMN_DESC, task.getDesc());

        res.put(TASK_COLUMN_STATUS, task.getStatus());

        res.put(TASK_COLUMN_DUEDATE,task.getDueDate().getTime());

        res.put(COMMON_COLUMN_CHANGED,task.getChanged().getTime());

        res.put(COMMON_COLUMN_MEMBERS_IDS, arrayToString(task.getMembersWebIds()));

        res.put(COMMON_COLUMN_DELETED, task.isDeleted());
        return res;
    }



    /*


    public static void clearNewTasks(FOTT_App app) {
        app.getDatabase().delete(TASK_TABLE_NAME, COMMON_COLUMN_FO_ID + " < 0 ");
    }


    public static void clearDeletedTasks(FOTT_App app) {
        app.getDatabase().delete(TASK_TABLE_NAME, COMMON_COLUMN_DELETED + " > 0");
    }

    public static ArrayList<FOTT_Task> getDeletedTasks(FOTT_App app) {
        return load(app, COMMON_COLUMN_DELETED + " > 0 AND " +
                COMMON_COLUMN_FO_ID + " > 0");
    }

    public static void deleteTask(FOTT_App app, FOTT_Task task) {
        app.getDatabase().delete(TASK_TABLE_NAME, COMMON_COLUMN_FO_ID + " = " + task.getWebId());
    }

    public static boolean isExistInDB(FOTT_App app, long taskID) {
        FOTT_Task res = getTaskById(app,taskID);
        return (res.getWebId() == taskID);
    }



    */
}
