package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.model.Timeslot;
import ru.vat78.fotimetracker.views.ErrorsHandler;

/**
 * Created by vat on 21.12.2015.
 */
public class DaoTimeslots extends FOTT_DBContract {
    private static final String CLASS_NAME = "DaoTimeslots";

    private static final String TABLE_NAME = "timeslots";
    private static final String COLUMN_NAME_START = "start";
    private static final String COLUMN_NAME_DURATION = "duration";
    private static final String COLUMN_NAME_TASK_ID = "task_id";

    public static final String SQL_CREATE_ENTRIES =
            CREATE_TABLE + TABLE_NAME + " (" +
                    BaseColumns._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                    COLUMN_NAME_FO_ID + INTEGER_TYPE + UNIQUE_FIELD + COMMA_SEP +
                    COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_DESC + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_START + NUMERIC_TYPE + COMMA_SEP +
                    COLUMN_NAME_DURATION + NUMERIC_TYPE + COMMA_SEP +
                    COLUMN_NAME_TASK_ID + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NAME_MEMBERS_IDS + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                    COLUMN_NAME_CHANGED_BY + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_DELETED + NUMERIC_TYPE + " );";
    public static final String SQL_DELETE_ENTRIES =
            DROP_TABLE + TABLE_NAME + ";";

    public static void rebuild(App app){
        app.getDatabase().execSQL(SQL_DELETE_ENTRIES);
        app.getDatabase().execSQL(SQL_CREATE_ENTRIES);
    }

    public static void save(App app, List<Timeslot> ts_list, boolean fullSync) {

        try {
            if (fullSync) {
                rebuild(app);

                for (int i = 0; i < ts_list.size(); i++) {
                    insert(app, ts_list.get(i));
                }
            } else {
                for (int i = 0; i < ts_list.size(); i++) {
                    insertOrUpdate(app, ts_list.get(i));
                }
            }
        }
        catch (Error e){
            app.getError().error_handler(ErrorsHandler.ERROR_SAVE_ERROR,CLASS_NAME,e.getMessage());
        }
    }

    private static void insertOrUpdate(App app, Timeslot timeslot) {
        ContentValues ts = convertToDB(timeslot);
        ts.put(COLUMN_NAME_DELETED,0);

        if (timeslot.getId() != 0) {
            Cursor cursor = app.getDatabase().query(TABLE_NAME, new String[]{BaseColumns._ID},
                    COLUMN_NAME_FO_ID + " = " + timeslot.getId(),"");
            if (cursor.moveToFirst()) ts.put(BaseColumns._ID, cursor.getLong(0));
        }

        long id = app.getDatabase().insertOrUpdate(TABLE_NAME, ts);

        if (!app.getError().is_error()) {

            if (timeslot.getId() == 0) {
                //For new record make sintetyc FO ID
                ts = new ContentValues();
                ts.put(COLUMN_NAME_FO_ID, -id);
                String s = BaseColumns._ID + " = " + id;
                app.getDatabase().update(TABLE_NAME,ts,s);
                timeslot.setId(-id);
            }

            String[] members = timeslot.getMembersArray();
            if (members.length > 0) {
                if (timeslot.getTaskId() == 0)
                    FOTT_DBMembers_Objects.addObject(app, timeslot, 2);
            }
        }
    }

    private static ContentValues convertToDB(Timeslot ts) {
        ContentValues res = new ContentValues();
        res.put(COLUMN_NAME_FO_ID, ts.getId());
        res.put(COLUMN_NAME_TITLE,ts.getName());
        res.put(COLUMN_NAME_DESC,ts.getDesc());
        res.put(COLUMN_NAME_DELETED, ts.isDeleted());

        res.put(COLUMN_NAME_START,ts.getStart().getTime());
        res.put(COLUMN_NAME_DURATION,ts.getDuration());

        res.put(COLUMN_NAME_CHANGED,ts.getChanged().getTime());
        res.put(COLUMN_NAME_CHANGED_BY,ts.getAuthor());

        res.put(COLUMN_NAME_TASK_ID, ts.getTaskId());
        res.put(COLUMN_NAME_MEMBERS_IDS,ts.getMembersIds());

        return res;
    }

    public static ArrayList<Timeslot> load(App app, String additionConditions) {

        ArrayList<Timeslot> timeslots = new ArrayList<>();

        String filter = additionConditions;
        //if (filter.isEmpty()) filter = COLUMN_NAME_DELETED + " = 0";
        if (filter.isEmpty()) {
            if (app.getCurTask() > 0) {
                filter = " " + COLUMN_NAME_TASK_ID +
                        " = " + String.valueOf(app.getCurTask());
            } else {
                filter = " " + COLUMN_NAME_FO_ID + " IN ( " +
                        FOTT_DBMembers_Objects.getSQLCondition(app.getCurMember(), 2) + ")";
            }
        }

        Cursor tsCursor = app.getDatabase().query(TABLE_NAME,
                new String[]{COLUMN_NAME_FO_ID,
                        COLUMN_NAME_TITLE,
                        COLUMN_NAME_START,
                        COLUMN_NAME_DURATION,
                        COLUMN_NAME_CHANGED,
                        COLUMN_NAME_CHANGED_BY,
                        COLUMN_NAME_TASK_ID,
                        COLUMN_NAME_DESC},
                filter, COLUMN_NAME_START + " DESC");

        tsCursor.moveToFirst();
        Timeslot el;
        if (!tsCursor.isAfterLast()){
            do {
                long id = tsCursor.getLong(0);
                String name = tsCursor.getString(1);
                long start = tsCursor.getLong(2);
                long dur = tsCursor.getLong(3);
                long changed = tsCursor.getLong(4);
                String author = tsCursor.getString(5);
                long tid = tsCursor.getLong(6);

                el = new Timeslot(id, name);
                el.setStart(start);
                el.setDuration(dur);
                el.setChanged(changed);
                el.setAuthor(author);
                el.setTaskId(tid);
                el.setDesc(tsCursor.getString(7));

                timeslots.add(el);
            } while (tsCursor.moveToNext());
        }

        return timeslots;
    }

    private static void insert (App app, Timeslot timeslot) {

        ContentValues ts = convertToDB(timeslot);
        ts.put(COLUMN_NAME_DELETED,0);

        app.getDatabase().insertOrUpdate(TABLE_NAME, ts);

        if (!app.getError().is_error()) {
            String[] members = timeslot.getMembersArray();
            if (members.length > 0) {
                if (timeslot.getTaskId() == 0)
                    FOTT_DBMembers_Objects.addObject(app, timeslot, 2);
            }
        }
    }

    public static void save (App app, Timeslot timeslot) {
        insertOrUpdate(app, timeslot);
    }

    public static void clearDeletedTS(App app) {
        app.getDatabase().delete(TABLE_NAME, COLUMN_NAME_DELETED + " > 0");
    }

    public static void deleteTS(App app, Timeslot timeslot){
                app.getDatabase().delete(TABLE_NAME, COLUMN_NAME_FO_ID + " = " + timeslot.getId());
    }

    public static ArrayList<Timeslot> getDeletedTS(App app) {
        return load(app, COLUMN_NAME_DELETED + " > 0 AND " +
            COLUMN_NAME_FO_ID + " > 0");
    }

    public static void clearNewTS(App app) {
        app.getDatabase().delete(TABLE_NAME, COLUMN_NAME_FO_ID + " < 0 ");
    }

    public static ArrayList<Timeslot> getChangedTS(App app, Date lastSync) {
        return load(app, "(" + COLUMN_NAME_CHANGED + " > " + String.valueOf(lastSync.getTime()) +
            " OR " + COLUMN_NAME_FO_ID + " < 0)");
    }

    public static void updateSavedTS(App app, ArrayList<Timeslot> timeslots){
        //todo ???????????????????
        for (Timeslot ts:timeslots){
            if (ts.getId()>0){
                app.getDatabase().delete(TABLE_NAME, COLUMN_NAME_FO_ID + " = " + ts.getId());
            }
        }
    }
}
