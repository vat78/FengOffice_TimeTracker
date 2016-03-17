package ru.vat78.fotimetracker.connectors.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.model.FOTT_TaskBuilder;
import ru.vat78.fotimetracker.views.FOTT_ErrorsHandler;

/**
 * Created by vat on 21.12.2015.
 */
public class FOTT_DBTasks extends FOTT_DBContract {
    private static final String CLASS_NAME ="FOTT_DBTasks";

    private static final String TABLE_NAME = "tasks";
    private static final String COLUMN_NAME_STATUS = "status";
    private static final String COLUMN_NAME_STARTDATE = "startdate";
    private static final String COLUMN_NAME_DUEDATE = "duedate";
    private static final String COLUMN_NAME_PRIORITY = "priority";
    private static final String COLUMN_NAME_ASSIGNEDBY = "assignedby";
    private static final String COLUMN_NAME_ASSIGNEDTO = "assignedto";
    private static final String COLUMN_NAME_PERCENT = "percent";
    private static final String COLUMN_NAME_WORKEDTIME = "workedtime";
    private static final String COLUMN_NAME_PENDINGTIME = "pendingtime";
    private static final String COLUMN_NAME_USETIMESLOTS = "usetimeslots";

    public static final String SQL_CREATE_ENTRIES =
            CREATE_TABLE + TABLE_NAME + " (" +
                    BaseColumns._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                    COLUMN_NAME_FO_ID + INTEGER_TYPE + UNIQUE_FIELD + COMMA_SEP +
                    COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_DESC + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_MEMBERS_IDS + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_STATUS + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NAME_STARTDATE + NUMERIC_TYPE + COMMA_SEP +
                    COLUMN_NAME_DUEDATE + NUMERIC_TYPE + COMMA_SEP +
                    COLUMN_NAME_PRIORITY + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NAME_ASSIGNEDBY + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_ASSIGNEDTO + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_PERCENT + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_WORKEDTIME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_PENDINGTIME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_USETIMESLOTS + NUMERIC_TYPE + COMMA_SEP +
                    COLUMN_NAME_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                    COLUMN_NAME_DELETED + NUMERIC_TYPE +
                    " );";
    public static final String SQL_DELETE_ENTRIES =
            DROP_TABLE + TABLE_NAME + ";";


    public static void rebuild(FOTT_App app){
        app.getDatabase().execSQL(SQL_DELETE_ENTRIES);
        app.getDatabase().execSQL(SQL_CREATE_ENTRIES);
    }

    public static void save(FOTT_App app, ArrayList<FOTT_Task> tasks_list, boolean fullSync) {

        try {
            if (fullSync) {
                rebuild(app);
                FOTT_DBMembers_Objects.rebuild(app);
            }


            for (FOTT_Task t : tasks_list) {
                if (t.canAddTimeslots() && t.getStatus() == FOTT_Task.TaskStatus.ACTIVE) {
                    save(app, t);
                } else {
                    if (!fullSync) {
                        if (t.getWebId() == app.getCurTask()) {
                            app.setCurTask(0);
                        } else {
                            deleteTask(app,t);
                        }
                    }
                }
            }
        }
        catch (Error e){
            app.getError().error_handler(FOTT_ErrorsHandler.ERROR_SAVE_ERROR,CLASS_NAME,e.getMessage());
        }
        //FOTT_DBMembers.calcTasksInMembers(app);
    }

    private static ContentValues convertToDB(FOTT_Task task) {
        ContentValues res = new ContentValues();

        res.put(COLUMN_NAME_FO_ID, task.getWebId());
        res.put(COLUMN_NAME_TITLE, task.getName());
        res.put(COLUMN_NAME_DESC, task.getDesc());

        res.put(COLUMN_NAME_STATUS, getCodeForStatus(task.getStatus()));

        res.put(COLUMN_NAME_DUEDATE,task.getDueDate().getTime());

        res.put(COLUMN_NAME_CHANGED,task.getChanged().getTime());

        res.put(COLUMN_NAME_MEMBERS_IDS, arrayToString(task.getMembersWebIds()));

        res.put(COLUMN_NAME_DELETED, task.isDeleted());
        return res;
    }

    public static void save(FOTT_App app, FOTT_Task task){
        ContentValues data = convertToDB(task);
        if (task.getWebId() != 0 ) {
            Cursor cursor = app.getDatabase().query(TABLE_NAME, new String[]{BaseColumns._ID},
                    COLUMN_NAME_FO_ID + " = " + task.getWebId(),"");
            if (cursor.moveToFirst()) data.put(BaseColumns._ID, cursor.getLong(0));
        }
        app.getDatabase().insertOrUpdate(TABLE_NAME, data);

        if (!app.getError().is_error()) {
            String[] members = task.getMembersWebIds();
            if (members.length > 0) {
                FOTT_DBMembers_Objects.addObject(app, task, 1);
            }
        }
    }

    public static ArrayList<FOTT_Task> load (FOTT_App app, String additionConditions) {

        ArrayList<FOTT_Task> tasks = new ArrayList<>();
        String memFilter = additionConditions;
        if (app.getCurMember() > 0) {
            if (memFilter.isEmpty()) {
                memFilter = " " + COLUMN_NAME_FO_ID + " IN (" +
                        FOTT_DBMembers_Objects.getSQLCondition(app.getCurMember(), 1) + ")";
            }
        }
        Cursor taskCursor = app.getDatabase().query(TABLE_NAME,
                new String[]{COLUMN_NAME_FO_ID,
                        COLUMN_NAME_TITLE,
                        COLUMN_NAME_DUEDATE,
                        COLUMN_NAME_STATUS},
                memFilter,
                COLUMN_NAME_DUEDATE + " ASC");

        taskCursor.moveToFirst();
        FOTT_TaskBuilder m;
        if (!taskCursor.isAfterLast()) {
            do {
                long id = taskCursor.getLong(0);
                String name = taskCursor.getString(1);
                long duedate = taskCursor.getLong(2);

                m = new FOTT_TaskBuilder();
                m.setWebID(id);
                m.setName(name);
                m.setDueDate(duedate);
                m.setStatus(getStatusByCode(taskCursor.getInt(3)));
                tasks.add(m.buildObject());
            } while (taskCursor.moveToNext());
        }
        return tasks;
    }

    public static FOTT_Task getTaskById(FOTT_App app, long id){

        FOTT_TaskBuilder res = new FOTT_TaskBuilder();
        if (id>0){
            String filter = " " + COLUMN_NAME_FO_ID + " = " + id;
            Cursor taskCursor = app.getDatabase().query(TABLE_NAME,
                    new String[]{COLUMN_NAME_FO_ID,
                            COLUMN_NAME_TITLE,
                            COLUMN_NAME_DUEDATE,
                            COLUMN_NAME_DESC,
                            COLUMN_NAME_STATUS},
                    filter,
                    COLUMN_NAME_DUEDATE);
            taskCursor.moveToFirst();
            if (!taskCursor.isAfterLast()){
                res.setWebID(taskCursor.getLong(0));
                res.setName(taskCursor.getString(1));
                res.setDueDate(taskCursor.getLong(2));
                res.setDesc(taskCursor.getString(3));
                res.setStatus(getStatusByCode(taskCursor.getInt(4)));
            }
        }
        return res.buildObject();
    }

    public static void clearNewTasks(FOTT_App app) {
        app.getDatabase().delete(TABLE_NAME, COLUMN_NAME_FO_ID + " < 0 ");
    }

    public static ArrayList<FOTT_Task> getChangedTasks(FOTT_App app, Date lastSync) {
        return load(app, "(" + COLUMN_NAME_CHANGED + " > " + String.valueOf(lastSync.getTime()) +
                " OR " + COLUMN_NAME_FO_ID + " < 0)");
    }

    public static void clearDeletedTasks(FOTT_App app) {
        app.getDatabase().delete(TABLE_NAME, COLUMN_NAME_DELETED + " > 0");
    }

    public static ArrayList<FOTT_Task> getDeletedTasks(FOTT_App app) {
        return load(app, COLUMN_NAME_DELETED + " > 0 AND " +
                COLUMN_NAME_FO_ID + " > 0");
    }

    public static void deleteTask(FOTT_App app, FOTT_Task task) {
        app.getDatabase().delete(TABLE_NAME, COLUMN_NAME_FO_ID + " = " + task.getWebId());
    }

    public static boolean isExistInDB(FOTT_App app, long taskID) {
        FOTT_Task res = getTaskById(app,taskID);
        return (res.getWebId() == taskID);
    }

    private static int getCodeForStatus(FOTT_Task.TaskStatus status) {
        int res=0;

        switch (status) {
            case ACTIVE:
                res = 0;
                break;
            case COMPLETED:
                res = 1;
                break;
        }
        return res;
    }

    private static FOTT_Task.TaskStatus getStatusByCode(int statusCode) {
        switch (statusCode) {
            case 0:
                return FOTT_Task.TaskStatus.ACTIVE;
            default:
                return FOTT_Task.TaskStatus.COMPLETED;
        }
    }
}
