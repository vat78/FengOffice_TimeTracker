package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.support.annotation.NonNull;
import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.model.Timeslot;
import ru.vat78.fotimetracker.views.ErrorsHandler;

import static ru.vat78.fotimetracker.database.DBContract.TasksTable.TABLE_NAME;
import static ru.vat78.fotimetracker.database.DBContract.TimeslotsTable.*;

/**
 * Created by vat on 21.12.2015.
 */
public class DaoTimeslots implements IDao<Timeslot> {
    private static final String CLASS_NAME = "DaoTimeslots";

    private final IDbConnect database;

    public DaoTimeslots(IDbConnect database) {
        this.database = database;
    }

    public void rebuild(App app){
        database.execSql(SQL_DELETE_ENTRIES);
        database.execSql(SQL_CREATE_ENTRIES);
    }

    @Override
    public long save(@NonNull Timeslot entity) {
        database.beginTransaction();
        ContentValues ts = convertForDB(entity);

        if (entity.getUid() != 0) {
            Cursor cursor = database.query(TABLE_NAME, new String[]{BaseColumns._ID},
                    DBContract.COLUMN_NAME_FO_ID + " = " + entity.getUid(),"");
            if (cursor.moveToFirst()) ts.put(BaseColumns._ID, cursor.getLong(0));
        }
        long id = database.insertOrUpdate(TABLE_NAME, ts);

        if (id != 0) {
            for (String member: entity.getMembersArray()) {
                saveLinkWithMember(id, member);
            }
        }
        database.endTransaction();
        return id;
    }

    @Override
    public long save(List<Timeslot> entities) {
        long cntr = 0;
        for (Timeslot t : entities) {
            if (save(t) != 0) cntr++;
        }
        return cntr;
    }

    @Override
    @NonNull
    public Timeslot getByUid(long uid) {
        Timeslot res = new Timeslot(0, "");
        if (uid > 0){
            database.beginTransaction();
            String filter = " " + DBContract.COLUMN_NAME_FO_ID + " = " + uid;
            Cursor tsCursor = database.query(TABLE_NAME,
                    new String[]{DBContract.COLUMN_NAME_FO_ID,
                            DBContract.COLUMN_NAME_TITLE,
                            COLUMN_NAME_START,
                            COLUMN_NAME_DURATION,
                            DBContract.COLUMN_NAME_CHANGED,
                            DBContract.COLUMN_NAME_CHANGED_BY,
                            COLUMN_NAME_TASK_ID,
                            DBContract.COLUMN_NAME_DESC,
                            DBContract.COLUMN_NAME_MEMBERS_IDS},
                    filter,
                    COLUMN_NAME_START);
            tsCursor.moveToFirst();
            if (!tsCursor.isAfterLast()){
                res.setUid(tsCursor.getLong(0));
                res.setName(tsCursor.getString(1));
                res.setStart(tsCursor.getLong(2));
                res.setDuration(tsCursor.getLong(3));
                res.setChanged(tsCursor.getLong(4));
                res.setAuthor(tsCursor.getString(5));
                res.setTaskId(tsCursor.getLong(6));
                res.setDesc(tsCursor.getString(7));
                res.setMembersIDs(tsCursor.getString(8));
            }
            database.endTransaction();
        }
        return res;
    }

    @Override
    public boolean isExistInDB(long tsUid) {
        return getByUid(tsUid).getUid() == tsUid;
    }

    @Override
    @NonNull
    public List<Timeslot> load() {
        return loadWithFilter("");
    }

    @NonNull
    public List<Timeslot> loadWithFilter(String filterConditions) {
        ArrayList<Timeslot> timeslots = new ArrayList<>();
        String filter = filterConditions;
        database.beginTransaction();

        Cursor tsCursor = database.query(TABLE_NAME,
                new String[]{DBContract.COLUMN_NAME_FO_ID,
                        DBContract.COLUMN_NAME_TITLE,
                        COLUMN_NAME_START,
                        COLUMN_NAME_DURATION,
                        DBContract.COLUMN_NAME_CHANGED,
                        DBContract.COLUMN_NAME_CHANGED_BY,
                        COLUMN_NAME_TASK_ID,
                        DBContract.COLUMN_NAME_DESC,
                        DBContract.COLUMN_NAME_MEMBERS_IDS},
                filter, COLUMN_NAME_START + " DESC");

        tsCursor.moveToFirst();
        if (!tsCursor.isAfterLast()){
            do {
                Timeslot el = new Timeslot(0, "");
                el.setUid(tsCursor.getLong(0));
                el.setName(tsCursor.getString(1));
                el.setStart(tsCursor.getLong(2));
                el.setDuration(tsCursor.getLong(3));
                el.setChanged(tsCursor.getLong(4));
                el.setAuthor(tsCursor.getString(5));
                el.setTaskId(tsCursor.getLong(6));
                el.setDesc(tsCursor.getString(7));
                el.setMembersIDs(tsCursor.getString(8));

                timeslots.add(el);
            } while (tsCursor.moveToNext());
        }
        return timeslots;
    }

    private ContentValues convertForDB(Timeslot ts) {
        ContentValues res = new ContentValues();
        res.put(DBContract.COLUMN_NAME_FO_ID, ts.getUid());
        res.put(DBContract.COLUMN_NAME_TITLE,ts.getName());
        res.put(DBContract.COLUMN_NAME_DESC,ts.getDesc());
        res.put(DBContract.COLUMN_NAME_DELETED, ts.isDeleted());

        res.put(COLUMN_NAME_START,ts.getStart().getTime());
        res.put(COLUMN_NAME_DURATION,ts.getDuration());

        res.put(DBContract.COLUMN_NAME_CHANGED,ts.getChanged().getTime());
        res.put(DBContract.COLUMN_NAME_CHANGED_BY,ts.getAuthor());

        res.put(COLUMN_NAME_TASK_ID, ts.getTaskId());
        res.put(DBContract.COLUMN_NAME_MEMBERS_IDS,ts.getMembersIds());
        return res;
    }

    private void saveLinkWithMember(long taskId, String memberUid) {
        ContentValues data = new ContentValues();
        data.put(DBContract.MemberObjectsTable.COLUMN_OBJECT_ID, taskId);
        data.put(DBContract.MemberObjectsTable.COLUMN_OBJECT_ID, memberUid);
        data.put(DBContract.MemberObjectsTable.COLUMN_OBJECT_TYPE, 2);
        database.insertOrUpdate(TABLE_NAME, data);
    }
}
