package ru.vat78.fotimetracker.connectors.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
import ru.vat78.fotimetracker.model.FOTT_TimeslotBuilder;
import ru.vat78.fotimetracker.views.FOTT_ErrorsHandler;

/**
 * Created by vat on 21.12.2015.
 */
public class FOTT_DBTimeslots extends FOTT_DBContract {
    private static final String CLASS_NAME = "FOTT_DBTimeslots";




    public static void rebuild(FOTT_App app){
        app.getDatabase().execSQL(TIMESLOTS_TABLE_DELETE);
        app.getDatabase().execSQL(TIMESLOTS_TABLE_CREATE);
    }

    public static void save(FOTT_App app, ArrayList<FOTT_Timeslot> ts_list, boolean fullSync) {

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
            app.getError().error_handler(FOTT_ErrorsHandler.ERROR_SAVE_ERROR,CLASS_NAME,e.getMessage());
        }
    }

    private static void insertOrUpdate(FOTT_App app, FOTT_Timeslot timeslot) {
        ContentValues ts = convertToDB(timeslot);
        ts.put(COMMON_COLUMN_DELETED,0);
        app.getError().reset_error();

        if (timeslot.getWebId() != 0) {
            Cursor cursor = app.getDatabase().query(TIMESLOTS_TABLE_NAME, new String[]{BaseColumns._ID},
                    COMMON_COLUMN_FO_ID + " = " + timeslot.getWebId(),"");
            if (cursor.moveToFirst()) ts.put(BaseColumns._ID, cursor.getLong(0));
        }

        long id = app.getDatabase().insertOrUpdate(TIMESLOTS_TABLE_NAME, ts);

        if (!app.getError().is_error()) {

            if (timeslot.getWebId() == 0) {
                //For new record make synthetic FO ID
                ts = new ContentValues();
                ts.put(COMMON_COLUMN_FO_ID, -id);
                String s = BaseColumns._ID + " = " + id;
                app.getDatabase().update(TIMESLOTS_TABLE_NAME,ts,s);
                //ToDo: probleb here
                //timeslot.setId(-id);
            }

            String[] members = timeslot.getMembersWebIds();
            if (members.length > 0) {
                if (timeslot.getTaskId() == 0)
                    FOTT_DBMembers_Objects.addObject(app, timeslot, 2);
            }
        }
    }

    private static ContentValues convertToDB(FOTT_Timeslot ts) {
        ContentValues res = new ContentValues();
        res.put(COMMON_COLUMN_FO_ID, ts.getWebId());
        res.put(COMMON_COLUMN_TITLE,ts.getName());
        res.put(COMMON_COLUMN_DESC,ts.getDesc());
        res.put(COMMON_COLUMN_DELETED, ts.isDeleted());

        res.put(TS_COLUMN_START,ts.getStart().getTime());
        res.put(TS_COLUMN_DURATION,ts.getDuration());

        res.put(COMMON_COLUMN_CHANGED,ts.getChanged().getTime());
        res.put(COMMON_COLUMN_CHANGED_BY,ts.getAuthor());

        res.put(TS_COLUMN_TASK_ID, ts.getTaskId());
        res.put(COMMON_COLUMN_MEMBERS_IDS, Arrays.toString(ts.getMembersWebIds()));

        return res;
    }

    public static ArrayList<FOTT_Timeslot> load(FOTT_App app, String additionConditions) {

        ArrayList<FOTT_Timeslot> timeslots = new ArrayList<>();

        String filter = additionConditions;
        //if (filter.isEmpty()) filter = COLUMN_DELETED + " = 0";
        if (filter.isEmpty()) {
            if (app.getCurTask() > 0) {
                filter = " " + TS_COLUMN_TASK_ID +
                        " = " + String.valueOf(app.getCurTask());
            } else {
                filter = " " + COMMON_COLUMN_FO_ID + " IN ( " +
                        FOTT_DBMembers_Objects.getSQLCondition(app.getCurMember(), 2) + ")";
            }
        }

        Cursor tsCursor = app.getDatabase().query(TIMESLOTS_TABLE_NAME,
                new String[]{COMMON_COLUMN_FO_ID,
                        COMMON_COLUMN_TITLE,
                        TS_COLUMN_START,
                        TS_COLUMN_DURATION,
                        COMMON_COLUMN_CHANGED,
                        COMMON_COLUMN_CHANGED_BY,
                        TS_COLUMN_TASK_ID,
                        COMMON_COLUMN_DESC,
                        COMMON_COLUMN_MEMBERS_IDS},
                filter, TS_COLUMN_START + " DESC");

        tsCursor.moveToFirst();
        FOTT_TimeslotBuilder el;
        if (!tsCursor.isAfterLast()){
            do {
                long id = tsCursor.getLong(0);
                String name = tsCursor.getString(1);
                long start = tsCursor.getLong(2);
                long dur = tsCursor.getLong(3);
                long changed = tsCursor.getLong(4);
                String author = tsCursor.getString(5);
                long tid = tsCursor.getLong(6);

                el = new FOTT_TimeslotBuilder();
                el.setWebID(id);
                el.setName(name);
                el.setStart(start);
                el.setDuration(dur);
                el.setChanged(changed);
                el.setAuthor(author);
                el.setTaskWebId(tid);
                el.setDesc(tsCursor.getString(7));
                el.setMembersWebIds(arrayFromString(tsCursor.getString(8)));

                timeslots.add(el.buildObject());
            } while (tsCursor.moveToNext());
        }

        return timeslots;
    }

    private static void insert (FOTT_App app, FOTT_Timeslot timeslot) {

        ContentValues ts = convertToDB(timeslot);
        ts.put(COMMON_COLUMN_DELETED,0);

        app.getDatabase().insertOrUpdate(TIMESLOTS_TABLE_NAME, ts);

        if (!app.getError().is_error()) {
            String[] members = timeslot.getMembersWebIds();
            if (members.length > 0) {
                if (timeslot.getTaskId() == 0)
                    FOTT_DBMembers_Objects.addObject(app, timeslot, 2);
            }
        }
    }

    public static void save (FOTT_App app, FOTT_Timeslot timeslot) {
        insertOrUpdate(app, timeslot);
    }

    public static void clearDeletedTS(FOTT_App app) {
        app.getDatabase().delete(TIMESLOTS_TABLE_NAME, COMMON_COLUMN_DELETED + " > 0");
    }

    public static void deleteTS(FOTT_App app, FOTT_Timeslot timeslot){
                app.getDatabase().delete(TIMESLOTS_TABLE_NAME, COMMON_COLUMN_FO_ID + " = " + timeslot.getWebId());
    }

    public static ArrayList<FOTT_Timeslot> getDeletedTS(FOTT_App app) {
        return load(app, COMMON_COLUMN_DELETED + " > 0 AND " +
                COMMON_COLUMN_FO_ID + " > 0");
    }

    public static void clearNewTS(FOTT_App app) {
        app.getDatabase().delete(TIMESLOTS_TABLE_NAME, COMMON_COLUMN_FO_ID + " < 0 ");
    }

    public static ArrayList<FOTT_Timeslot> getChangedTS(FOTT_App app, Date lastSync) {
        return load(app, "(" + COMMON_COLUMN_CHANGED + " > " + String.valueOf(lastSync.getTime()) +
            " OR " + COMMON_COLUMN_FO_ID + " < 0)");
    }

    public static void updateSavedTS(FOTT_App app, ArrayList<FOTT_Timeslot> timeslots){
        //todo ???????????????????
        for (FOTT_Timeslot ts:timeslots){
            if (ts.getWebId()>0){
                app.getDatabase().delete(TIMESLOTS_TABLE_NAME, COMMON_COLUMN_FO_ID + " = " + ts.getWebId());
            }
        }
    }
}
