package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.model.FOTT_Object;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
import ru.vat78.fotimetracker.views.FOTT_ErrorsHandler;

/**
 * Created by vat on 21.12.2015.
 */
public class FOTT_DBTimeslots extends FOTT_DBContract {
    private static final String CLASS_NAME = "FOTT_DBTimeslots";

    private static final String TABLE_NAME = "timeslots";
    private static final String COLUMN_NAME_TIMESLOT_ID = "timeslot_ID";
    private static final String COLUMN_NAME_TITLE = "name";
    private static final String COLUMN_NAME_DESC = "description";
    private static final String COLUMN_NAME_START = "start";
    private static final String COLUMN_NAME_DURATION = "duration";
    private static final String COLUMN_NAME_TASK_ID = "task_id";
    private static final String COLUMN_NAME_MEMBERS_ID = "members_id";
    private static final String COLUMN_NAME_CHANGED = "changed";
    private static final String COLUMN_NAME_CHANGED_BY = "changed_by";

    public static final String SQL_CREATE_ENTRIES =
            CREATE_TABLE + TABLE_NAME + " (" +
                    //BaseColumns._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                    COLUMN_NAME_TIMESLOT_ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                    COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_DESC + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_START + NUMERIC_TYPE + COMMA_SEP +
                    COLUMN_NAME_DURATION + NUMERIC_TYPE + COMMA_SEP +
                    COLUMN_NAME_TASK_ID + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NAME_MEMBERS_ID + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_CHANGED + NUMERIC_TYPE + COMMA_SEP +
                    COLUMN_NAME_CHANGED_BY + TEXT_TYPE +  " );";
    public static final String SQL_DELETE_ENTRIES =
            DROP_TABLE + TABLE_NAME + ";";

    public static void rebuild(FOTT_App app){
        app.getDatabase().execSQL(SQL_DELETE_ENTRIES);
        app.getDatabase().execSQL(SQL_CREATE_ENTRIES);
    }

    public static void save(FOTT_App app, ArrayList<FOTT_Timeslot> ts_list, boolean fullSync) {

        try {
            if (fullSync) {
                rebuild(app);
            }

            for (int i = 0; i < ts_list.size(); i++) {
                insert(app,ts_list.get(i));
            }
        }
        catch (Error e){
            app.getError().error_handler(FOTT_ErrorsHandler.ERROR_SAVE_ERROR,CLASS_NAME,e.getMessage());
        }
    }

    private static ContentValues convertToDB(FOTT_Timeslot ts) {
        ContentValues res = new ContentValues();
        res.put(COLUMN_NAME_TIMESLOT_ID, ts.getId());
        res.put(COLUMN_NAME_TITLE,ts.getName());
        res.put(COLUMN_NAME_DESC,ts.getDesc());

        res.put(COLUMN_NAME_START,ts.getStart().getTime());
        res.put(COLUMN_NAME_DURATION,ts.getDuration().getTime());

        res.put(COLUMN_NAME_CHANGED,ts.getChanged().getTime());
        res.put(COLUMN_NAME_CHANGED_BY,ts.getAuthor());

        res.put(COLUMN_NAME_TASK_ID, ts.getTaskId());
        res.put(COLUMN_NAME_MEMBERS_ID,ts.getMembersIds());

        return res;
    }

    public static ArrayList<FOTT_Timeslot> load(FOTT_App app) {

        ArrayList<FOTT_Timeslot> timeslots = new ArrayList<>();

        String filter = "";
        if (app.getCurTask() > 0){
            filter = " " + COLUMN_NAME_TASK_ID +
                    " = " + String.valueOf(app.getCurTask());
        } else {
            filter = " " + COLUMN_NAME_TIMESLOT_ID + " IN ( " +
                    FOTT_DBMembers_Objects.getSQLCondition(app.getCurMember(),2) + ")";
        }

        Cursor tsCursor = app.getDatabase().query(TABLE_NAME,
                new String[]{COLUMN_NAME_TIMESLOT_ID,
                        COLUMN_NAME_TITLE,
                        COLUMN_NAME_START,
                        COLUMN_NAME_DURATION,
                        COLUMN_NAME_CHANGED,
                        COLUMN_NAME_CHANGED_BY,
                        COLUMN_NAME_TASK_ID,
                        COLUMN_NAME_DESC},
                filter, COLUMN_NAME_START + " DESC");

        tsCursor.moveToFirst();
        FOTT_Timeslot el;
        if (!tsCursor.isAfterLast()){
            do {
                long id = tsCursor.getLong(0);
                String name = tsCursor.getString(1);
                long start = tsCursor.getLong(2);
                int dur = tsCursor.getInt(3);
                long changed = tsCursor.getLong(4);
                String author = tsCursor.getString(5);
                long tid = tsCursor.getLong(6);

                el = new FOTT_Timeslot(id, name);
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

    private static void insert (FOTT_App app, FOTT_Timeslot timeslot) {

        ContentValues ts = convertToDB(timeslot);
        app.getDatabase().insertOrUpdate(TABLE_NAME, ts);

        if (!app.getError().is_error()) {
            String[] members = timeslot.getMembersArray();
            if (members.length > 0) {
                if (timeslot.getTaskId() == 0)
                    FOTT_DBMembers_Objects.addObject(app, (FOTT_Object) timeslot, 2);
            }
        }
    }

    public static void save (FOTT_App app, FOTT_Timeslot timeslot) {
        insert(app,timeslot);
    }
}
