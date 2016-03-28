package ru.vat78.fotimetracker.connectors.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.model.FOTT_Object;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.model.FOTT_TaskBuilder;

import static ru.vat78.fotimetracker.connectors.database.FOTT_DBContract.*;


public class FOTT_DBTasks extends FOTT_DBCommon {
    private static final String CLASS_NAME ="FOTT_DBTasks";

    public FOTT_DBTasks(SQLiteDatabase db) {
        super(db);
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
                        COMMON_COLUMN_CHANGED,
                        COMMON_COLUMN_DELETED},
                filter, null, "", "",
                TASK_COLUMN_DUEDATE + " ASC");

        taskCursor.moveToFirst();
        if (!taskCursor.isAfterLast()) {
            do {
                FOTT_TaskBuilder t = new FOTT_TaskBuilder();
                t.setDbID(taskCursor.getLong(0));
                t.setWebID(taskCursor.getLong(1));
                t.setName(taskCursor.getString(2));
                t.setDueDate(taskCursor.getLong(3));
                t.setStatus(taskCursor.getInt(4));
                t.setChanged(taskCursor.getLong(5));
                t.setDeleted(taskCursor.getInt(6) > 0);
                tasks.add(t.buildObject());
            } while (taskCursor.moveToNext());
        }
        taskCursor.close();

        return tasks;
    }

    @Override
    public long saveObject(FOTT_Object savingObject) {

        long result = 0;
        if (savingObject == null) return result;

        FOTT_Task task = (FOTT_Task) savingObject;

        ContentValues data = convertToDB(task);
        result = db.insertWithOnConflict(TASK_TABLE_NAME, "", data, SQLiteDatabase.CONFLICT_REPLACE);

        if (result !=0 && task.getMembersWebIds().length > 0) {
            FOTT_DBMembers_Objects.addObjectLinks(db, task, FOTT_DBMembers_Objects.TASK);
        }

        return result;
    }

    @Override
    public boolean deleteObject(FOTT_Object deletingObject) {
        return db.delete(TASK_TABLE_NAME,
                COMMON_COLUMN_ID + " = " + deletingObject.getDbID(), null) == 1;
    }

    public void rebuild(){
        db.execSQL(TASK_TABLE_DELETE);
        db.execSQL(TASK_TABLE_CREATE);
        FOTT_DBMembers_Objects.rebuild(db);
    }



    private static ContentValues convertToDB(FOTT_Task task) {
        ContentValues res = new ContentValues();

        if (task.getDbID() != 0) res.put(COMMON_COLUMN_ID, task.getDbID());
        if (task.getWebId() != 0) res.put(COMMON_COLUMN_FO_ID, task.getWebId());
        res.put(COMMON_COLUMN_TITLE, task.getName());
        res.put(COMMON_COLUMN_DESC, task.getDesc());

        res.put(TASK_COLUMN_STATUS, task.getStatus());

        if (task.getDueDate() != null)
            res.put(TASK_COLUMN_DUEDATE,task.getDueDate().getTime());

        if (task.getChanged() != null)
            res.put(COMMON_COLUMN_CHANGED,task.getChanged().getTime());

        res.put(COMMON_COLUMN_MEMBERS_IDS, arrayToString(task.getMembersWebIds()));

        if (task.isDeleted()) {
            res.put(COMMON_COLUMN_DELETED, 1);
        } else {
            res.put(COMMON_COLUMN_DELETED, 0);
        }
        return res;
    }



    /*
    ToDo: need to clear

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
