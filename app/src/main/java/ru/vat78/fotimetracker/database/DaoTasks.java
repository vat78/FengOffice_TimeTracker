package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.model.Task;
import ru.vat78.fotimetracker.views.ErrorsHandler;

/**
 * Created by vat on 21.12.2015.
 */
public class DaoTasks extends FOTT_DBContract {
    private static final String CLASS_NAME ="DaoTasks";

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


    public static void rebuild(App app){
        app.getDatabase().execSQL(SQL_DELETE_ENTRIES);
        app.getDatabase().execSQL(SQL_CREATE_ENTRIES);
    }

    public static void save(App app, List<Task> tasks_list, boolean fullSync) {

        try {
            if (fullSync) {
                rebuild(app);
                FOTT_DBMembers_Objects.rebuild(app);
            }

            for (FOTT_Task t : tasks_list) {
                if (t.canAddTimeslots() && t.getStatus() == 0) {
                    save(app, t);
                } else {
                    if (!fullSync) {
                        if (t.getId() == app.getCurTask()) {
                            t.setDeleted(true);
                        } else {
                            deleteTask(app,t);
                        }
                    }
                }
            }
        }
        catch (Error e){
            app.getError().error_handler(ErrorsHandler.ERROR_SAVE_ERROR,CLASS_NAME,e.getMessage());
        }
        //DaoMembers.calcTasksInMembers(app);
    }

    private static ContentValues convertToDB(Task task) {
        ContentValues res = new ContentValues();

        res.put(COLUMN_NAME_FO_ID, task.getId());
        res.put(COLUMN_NAME_TITLE, task.getName());
        res.put(COLUMN_NAME_DESC, task.getDesc());

        res.put(COLUMN_NAME_STATUS, task.getStatus());

        res.put(COLUMN_NAME_DUEDATE,task.getDueDate().getTime());

        res.put(COLUMN_NAME_CHANGED,task.getChanged().getTime());

        res.put(COLUMN_NAME_MEMBERS_IDS, task.getMembersIds());

        res.put(COLUMN_NAME_DELETED, task.isDeleted());
        return res;
    }

    private static void save(App app, Task task){
        ContentValues data = convertToDB(task);
        if (task.getId() != 0 ) {
            Cursor cursor = app.getDatabase().query(TABLE_NAME, new String[]{BaseColumns._ID},
                    COLUMN_NAME_FO_ID + " = " + task.getId(),"");
            if (cursor.moveToFirst()) data.put(BaseColumns._ID, cursor.getLong(0));
        }
        app.getDatabase().insertOrUpdate(TABLE_NAME, data);

        if (!app.getError().is_error()) {
            String[] members = task.getMembersArray();
            if (members.length > 0) {
                FOTT_DBMembers_Objects.addObject(app, task, 1);
            }
        }
    }

    public static ArrayList<Task> load (App app, String additionConditions) {

        ArrayList<Task> tasks = new ArrayList<>();
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
        Task m;
        if (!taskCursor.isAfterLast()) {
            do {
                long id = taskCursor.getLong(0);
                String name = taskCursor.getString(1);
                long duedate = taskCursor.getLong(2);

                m = new Task(id, name);
                m.setDuedate(duedate);

                m.setStatus(taskCursor.getInt(3));

                tasks.add(m);
            } while (taskCursor.moveToNext());
        }
        return tasks;
    }

    public static Task getTaskById(App app, long id){

        Task res = new Task(0,"");
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
                res.setId(taskCursor.getLong(0));
                res.setName(taskCursor.getString(1));
                res.setDuedate(taskCursor.getLong(2));
                res.setDesc(taskCursor.getString(3));
                res.setStatus(taskCursor.getInt(4));
            }
        }
        return res;
    }

    public static void clearNewTasks(App app) {
        app.getDatabase().delete(TABLE_NAME, COLUMN_NAME_FO_ID + " < 0 ");
    }

    public static ArrayList<Task> getChangedTasks(App app, Date lastSync) {
        return load(app, "(" + COLUMN_NAME_CHANGED + " > " + String.valueOf(lastSync.getTime()) +
                " OR " + COLUMN_NAME_FO_ID + " < 0)");
    }

    public static void clearDeletedTasks(App app) {
        app.getDatabase().delete(TABLE_NAME, COLUMN_NAME_DELETED + " > 0");
    }

    public static ArrayList<Task> getDeletedTasks(App app) {
        return load(app, COLUMN_NAME_DELETED + " > 0 AND " +
                COLUMN_NAME_FO_ID + " > 0");
    }

    public static void deleteTask(App app, Task task) {
        app.getDatabase().delete(TABLE_NAME, COLUMN_NAME_FO_ID + " == " + task.getId());
    }
    
    public static boolean isExistInDB(App app, long taskID) {
        Task res = getTaskById(app,taskID);
        return (res.getId() == taskID);
    }
}
