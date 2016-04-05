package ru.vat78.fotimetracker.connectors.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.model.FOTT_Object;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
import ru.vat78.fotimetracker.model.FOTT_TimeslotBuilder;

import static ru.vat78.fotimetracker.connectors.database.FOTT_DBContract.*;

public class FOTT_DBTimeslots extends FOTT_DBCommon {

    private static final String CLASS_NAME = "FOTT_DBTimeslots";

    public FOTT_DBTimeslots(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public ArrayList<FOTT_Timeslot> loadFilteredObjects(String filter) {

        ArrayList<FOTT_Timeslot> timeslots = new ArrayList<>();

        Cursor tsCursor = db.query(TIMESLOTS_TABLE_NAME,
                new String[]{COMMON_COLUMN_ID,
                        COMMON_COLUMN_FO_ID,
                        COMMON_COLUMN_TITLE,
                        TS_COLUMN_START,
                        TS_COLUMN_DURATION,
                        COMMON_COLUMN_CHANGED,
                        COMMON_COLUMN_CHANGED_BY,
                        TS_COLUMN_TASK_ID,
                        COMMON_COLUMN_DESC,
                        COMMON_COLUMN_MEMBERS_IDS},
                filter, null, "", "", TS_COLUMN_START + " DESC");

        tsCursor.moveToFirst();
        if (!tsCursor.isAfterLast()){
            do {
                FOTT_TimeslotBuilder el = new FOTT_TimeslotBuilder();
                el.setDbID(tsCursor.getLong(0));
                el.setWebID(tsCursor.getLong(1));
                el.setName(tsCursor.getString(2));
                el.setStart(tsCursor.getLong(3));
                el.setDuration(tsCursor.getLong(4));
                el.setChanged(tsCursor.getLong(5));
                el.setAuthor(tsCursor.getString(6));
                el.setTaskWebId(tsCursor.getLong(7));
                el.setDesc(tsCursor.getString(8));
                el.setMembersWebIds(arrayFromString(tsCursor.getString(9)));

                timeslots.add(el.buildObject());
            } while (tsCursor.moveToNext());
        }
        tsCursor.close();

        return timeslots;
    }

    @Override
    public long saveObject(FOTT_Object savingObject) {

        long result = 0;
        if (savingObject == null) return result;

        FOTT_Timeslot timeslot = (FOTT_Timeslot) savingObject;

        ContentValues data = convertToDB(timeslot);
        result = db.insertWithOnConflict(TIMESLOTS_TABLE_NAME, "", data, SQLiteDatabase.CONFLICT_REPLACE);

        if (result !=0 && timeslot.getMembersWebIds().length > 0) {
            if (timeslot.getTaskId() == 0)
                FOTT_DBMembers_Objects.addObjectLinks(db, timeslot, FOTT_DBMembers_Objects.TIMESLOT);
        }

        return result;
    }

    @Override
    public boolean deleteObject(FOTT_Object deletingObject) {
        return db.delete(TIMESLOTS_TABLE_NAME,
                COMMON_COLUMN_ID + " = " + deletingObject.getDbID(), null) == 1;
    }

    public void rebuild(){
        db.execSQL(TIMESLOTS_TABLE_DELETE);
        db.execSQL(TIMESLOTS_TABLE_CREATE);
    }


    private ContentValues convertToDB(FOTT_Timeslot ts) {
        ContentValues res = new ContentValues();

        long id = ts.getDbID();
        if (id == 0 && ts.getWebId() != 0) id = isExistInDB(ts.getWebId());
        if (id !=0 ) res.put(COMMON_COLUMN_ID, ts.getDbID());

        if (ts.getWebId() !=0 ) res.put(COMMON_COLUMN_FO_ID, ts.getWebId());
        res.put(COMMON_COLUMN_TITLE,ts.getName());
        res.put(COMMON_COLUMN_DESC,ts.getDesc());
        res.put(COMMON_COLUMN_DELETED, ts.isDeleted());

        res.put(TS_COLUMN_START,ts.getStart().getTime());
        res.put(TS_COLUMN_DURATION,ts.getDuration());

        res.put(COMMON_COLUMN_CHANGED,ts.getChanged().getTime());
        res.put(COMMON_COLUMN_CHANGED_BY,ts.getAuthor());

        res.put(TS_COLUMN_TASK_ID, ts.getTaskId());
        res.put(COMMON_COLUMN_MEMBERS_IDS, arrayToString(ts.getMembersWebIds()));

        return res;
    }


    /*

    ToDo: need to clear
    private static void insert (FOTT_App app, FOTT_Timeslot timeslot) {

        ContentValues ts = convertToDB(timeslot);
        ts.put(COMMON_COLUMN_DELETED,0);

        app.getWritableDB().insertOrUpdate(TIMESLOTS_TABLE_NAME, ts);

        if (!app.getErrorCode().is_error()) {
            String[] members = timeslot.getMembersWebIds();
            if (members.length > 0) {
                if (timeslot.getTaskId() == 0)
                    FOTT_DBMembers_Objects.addObjectLinks(app, timeslot, 2);
            }
        }
    }

    public static void save (FOTT_App app, FOTT_Timeslot timeslot) {
        insertOrUpdate(app, timeslot);
    }

    public static void clearDeletedTS(FOTT_App app) {
        app.getWritableDB().delete(TIMESLOTS_TABLE_NAME, COMMON_COLUMN_DELETED + " > 0");
    }

    public static void deleteTS(FOTT_App app, FOTT_Timeslot timeslot){
                app.getWritableDB().delete(TIMESLOTS_TABLE_NAME, COMMON_COLUMN_FO_ID + " = " + timeslot.getWebId());
    }

    public static ArrayList<FOTT_Timeslot> getDeletedTS(FOTT_App app) {
        return load(app, COMMON_COLUMN_DELETED + " > 0 AND " +
                COMMON_COLUMN_FO_ID + " > 0");
    }

    public static void clearNewTS(FOTT_App app) {
        app.getWritableDB().delete(TIMESLOTS_TABLE_NAME, COMMON_COLUMN_FO_ID + " < 0 ");
    }

    public static ArrayList<FOTT_Timeslot> getChangedTS(FOTT_App app, Date lastSync) {
        return load(app, "(" + COMMON_COLUMN_CHANGED + " > " + String.valueOf(lastSync.getTime()) +
            " OR " + COMMON_COLUMN_FO_ID + " < 0)");
    }

    public static void updateSavedTS(FOTT_App app, ArrayList<FOTT_Timeslot> timeslots){
        //todo ???????????????????
        for (FOTT_Timeslot ts:timeslots){
            if (ts.getWebId()>0){
                app.getWritableDB().delete(TIMESLOTS_TABLE_NAME, COMMON_COLUMN_FO_ID + " = " + ts.getWebId());
            }
        }
    }
     */
}
