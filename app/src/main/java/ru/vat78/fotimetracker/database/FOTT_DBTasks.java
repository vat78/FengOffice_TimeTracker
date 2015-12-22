package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.model.FOTT_Object;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.views.FOTT_ErrorsHandler;

/**
 * Created by vat on 21.12.2015.
 */
public class FOTT_DBTasks extends FOTT_DBContract {
    private static final String CLASS_NAME ="FOTT_DBTasks";

    private static final String TABLE_NAME = "tasks";
    private static final String COLUMN_NAME_TASK_ID = "taskid";
    private static final String COLUMN_NAME_TITLE = "name";
    private static final String COLUMN_NAME_DESC = "description";
    private static final String COLUMN_NAME_MEMBERS_IDS = "members_ids";
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

    private static final String COLUMN_NAME_CHANGED = "changed";


    public static final String SQL_CREATE_ENTRIES =
            CREATE_TABLE + TABLE_NAME + " (" +
                    //BaseColumns._ID + PRIMARY_KEY + COMMA_SEP +
                    COLUMN_NAME_TASK_ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
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
                    COLUMN_NAME_CHANGED + NUMERIC_TYPE +
                    " );";
    public static final String SQL_DELETE_ENTRIES =
            DROP_TABLE + TABLE_NAME + ";";


    public static void rebuild(FOTT_App app){
        app.getDatabase().execSQL(SQL_DELETE_ENTRIES);
        app.getDatabase().execSQL(SQL_CREATE_ENTRIES);
    }

    public static void save(FOTT_App app, ArrayList<FOTT_Task> tasks_list, boolean fullSync) {

        if (app.getCurTask() > 0){
            //TODO: if has selected task
        }

        try {
            if (fullSync) {
                rebuild(app);
                FOTT_DBMembers_Objects.rebuild(app);
                //TODO: create emty task "Any task" ?
            }


            for (int i = 0; i < tasks_list.size(); i++) {
                insert(app, tasks_list.get(i));
            }
        }
        catch (Error e){
            app.getError().error_handler(FOTT_ErrorsHandler.ERROR_SAVE_ERROR,CLASS_NAME,e.getMessage());
        }
        //FOTT_DBMembers.calcTasksInMembers(app);
    }

    private static ContentValues convertToDB(FOTT_Task task) {
        ContentValues res = new ContentValues();

        res.put(COLUMN_NAME_TASK_ID, task.getId());
        res.put(COLUMN_NAME_TITLE, task.getName());
        res.put(COLUMN_NAME_DESC, task.getDesc());

        res.put(COLUMN_NAME_DUEDATE,task.getDueDate().getTime());

        res.put(COLUMN_NAME_CHANGED,task.getChanged().getTime());

        res.put(COLUMN_NAME_MEMBERS_IDS, task.getMembersIds());
        return res;
    }

    private static void insert(FOTT_App app, FOTT_Task task){
        ContentValues data = convertToDB(task);
        app.getDatabase().insertOrUpdate(TABLE_NAME, data);

        if (!app.getError().is_error()) {
            String[] members = task.getMembersArray();
            if (members.length > 0) {
                FOTT_DBMembers_Objects.addObject(app, (FOTT_Object) task, 1);
            }
        }
    }

    public static ArrayList<FOTT_Task> load (FOTT_App app) {

        ArrayList<FOTT_Task> tasks = new ArrayList<>();
        String memFilter = null;
        if (app.getCurMember() > 0) {
            memFilter = " " + COLUMN_NAME_TASK_ID + " IN (" +
                    FOTT_DBMembers_Objects.getSQLCondition(app.getCurMember(),1) + ")";
        }
        Cursor taskCursor = app.getDatabase().query(TABLE_NAME,
                new String[]{COLUMN_NAME_TASK_ID,
                        COLUMN_NAME_TITLE,
                        COLUMN_NAME_DUEDATE},
                memFilter,
                COLUMN_NAME_DUEDATE + " ASC");

        taskCursor.moveToFirst();
        FOTT_Task m;
        if (!taskCursor.isAfterLast()) {
            do {
                long id = taskCursor.getLong(0);
                String name = taskCursor.getString(1);
                long duedate = taskCursor.getLong(2);

                m = new FOTT_Task(id, name);
                m.setDuedate(duedate);

                tasks.add(m);
            } while (taskCursor.moveToNext());
        }
        return tasks;
    }

    public static FOTT_Task getTaskById(FOTT_App app, long id){

        FOTT_Task res = new FOTT_Task(0,"");
        if (id>0){
            String filter = " " + COLUMN_NAME_TASK_ID + " = " + id;
            Cursor taskCursor = app.getDatabase().query(TABLE_NAME,
                    new String[]{COLUMN_NAME_TASK_ID,
                            COLUMN_NAME_TITLE,
                            COLUMN_NAME_DUEDATE,
                            COLUMN_NAME_DESC},
                    filter,
                    COLUMN_NAME_DUEDATE);
            taskCursor.moveToFirst();
            if (!taskCursor.isAfterLast()){
                res.setId(taskCursor.getLong(0));
                res.setName(taskCursor.getString(1));
                res.setDuedate(taskCursor.getLong(2));
                res.setDesc(taskCursor.getString(3));
            }
        }
        return res;
    }
}
