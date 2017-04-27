package ru.vat78.fotimetracker.database;

import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.*;

import android.support.annotation.NonNull;
import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.model.Task;

import static ru.vat78.fotimetracker.database.DBContract.TasksTable.*;

/**
 * Created by vat on 21.12.2015.
 */
public class DaoTasks implements IDao<Task> {
    private static final String CLASS_NAME ="DaoTasks";

    private final IDbConnect database;

    public DaoTasks(IDbConnect database) {
        this.database = database;
    }

    public void rebuildTable(App app){
        database.execSql(SQL_DELETE_ENTRIES);
        database.execSql(SQL_CREATE_TABLE);
    }

    @Override
    public long save(@NonNull Task task) {
        database.beginTransaction();
        Map data = convertForDb(task);

        if (task.getUid() != 0 ) {
            Cursor cursor = database.query(TABLE_NAME, new String[]{BaseColumns._ID},
                    DBContract.COLUMN_NAME_FO_ID + " = " + task.getUid(),"");
            if (cursor.moveToFirst()) data.put(BaseColumns._ID, cursor.getLong(0));
        }
        long id = database.insertOrUpdate(TABLE_NAME, data);

        if (id != 0) {
            task.setId(id);
            saveLinkWithMember(id, "0");
            for (String member: task.getMembersArray()) {
                saveLinkWithMember(id, member);
            }
        }
        database.endTransaction();
        return id;
    }

    @Override
    public long save(List<Task> tasks) {
        long cntr = 0;
        for (Task t : tasks) {
            if (save(t) != 0) cntr++;
        }
        return cntr;
    }

    @Override
    @NonNull
    public Task getByUid(long uid) {
        Task res = new Task(0,"");
        if (uid > 0){
            database.beginTransaction();
            String filter = DBContract.COLUMN_NAME_FO_ID + " = " + uid;
            Cursor taskCursor = database.query(TABLE_NAME,
                    new String[]{BaseColumns._ID,
                            DBContract.COLUMN_NAME_FO_ID,
                            DBContract.COLUMN_NAME_TITLE,
                            COLUMN_NAME_DUEDATE,
                            DBContract.COLUMN_NAME_DESC,
                            COLUMN_NAME_STATUS},
                    filter,
                    COLUMN_NAME_DUEDATE);

            if (taskCursor.moveToFirst()) {
                res.setId(taskCursor.getLong(0));
                res.setUid(taskCursor.getLong(1));
                res.setName(taskCursor.getString(2));
                res.setDuedate(taskCursor.getLong(3));
                res.setDesc(taskCursor.getString(4));
                res.setStatus(taskCursor.getInt(5));
            }
            database.endTransaction();
        }
        return res;
    }

    @Override
    public boolean isExistInDB(long uid) {
        return (getByUid(uid).getUid() == uid);
    }

    /**
     * Now it returns only base info about tasks
     * @return list of all tasks in database
     */
    @Override
    @NonNull
    public List<Task> load() {
        return loadBaseInfo("");
    }

    /**
     * Select only base info about tasks (uid, title, due date and status)
     * @param conditions - conditions for 'WHERE' statement
     * @return list of tasks
     */
    @NonNull
    public List<Task> loadBaseInfo (String conditions) {
        List<Task> tasks = new ArrayList<>();

        database.beginTransaction();
        Cursor taskCursor = database.query(TABLE_NAME,
                new String[]{
                        BaseColumns._ID,
                        DBContract.COLUMN_NAME_FO_ID,
                        DBContract.COLUMN_NAME_TITLE,
                        COLUMN_NAME_DUEDATE,
                        COLUMN_NAME_STATUS},
                conditions,
                COLUMN_NAME_DUEDATE + " ASC");

        if (taskCursor.moveToFirst()) {
            do {
                long uid = taskCursor.getLong(0);
                String name = taskCursor.getString(1);
                long duedate = taskCursor.getLong(2);
                Task t = new Task(uid, name);
                t.setDuedate(duedate);
                t.setStatus(taskCursor.getInt(3));
                t.setId(taskCursor.getLong(4));
                tasks.add(t);
            } while (taskCursor.moveToNext());
        }
        database.endTransaction();
        return tasks;
    }

    /*
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
        app.getDatabase().delete(TABLE_NAME, COLUMN_NAME_FO_ID + " == " + task.getUid());
    }
    */

    private Map<String, Object> convertForDb(Task task) {
        Map<String, Object> res = new HashMap<>();
        res.put(BaseColumns._ID, task.getId());
        res.put(DBContract.COLUMN_NAME_FO_ID, task.getUid());
        res.put(DBContract.COLUMN_NAME_TITLE, task.getName());
        res.put(DBContract.COLUMN_NAME_DESC, task.getDesc());
        res.put(COLUMN_NAME_STATUS, task.getStatus());
        res.put(COLUMN_NAME_DUEDATE,task.getDueDate().getTime());
        res.put(DBContract.COLUMN_NAME_CHANGED,task.getChanged().getTime());
        res.put(DBContract.COLUMN_NAME_MEMBERS_IDS, task.getMembersIds());
        res.put(DBContract.COLUMN_NAME_DELETED, task.isDeleted());
        return res;
    }

    private void saveLinkWithMember(long taskId, String memberUid) {
        Map<String, Object> data = new HashMap<>(3);
        data.put(DBContract.MemberObjectsTable.COLUMN_OBJECT_ID, taskId);
        data.put(DBContract.MemberObjectsTable.COLUMN_OBJECT_ID, memberUid);
        data.put(DBContract.MemberObjectsTable.COLUMN_OBJECT_TYPE, 1);
        database.insertOrUpdate(DBContract.MemberObjectsTable.TABLE_NAME, data);
    }
}
