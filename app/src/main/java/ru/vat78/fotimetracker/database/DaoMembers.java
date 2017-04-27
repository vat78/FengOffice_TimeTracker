package ru.vat78.fotimetracker.database;

import android.database.Cursor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import ru.vat78.fotimetracker.model.Member;

import static ru.vat78.fotimetracker.database.DBContract.COLUMN_NAME_FO_ID;
import static ru.vat78.fotimetracker.database.DBContract.COLUMN_NAME_TITLE;
import static ru.vat78.fotimetracker.database.DBContract.MembersTable.*;

/**
 * Created by vat on 21.12.2015.
 */
public class DaoMembers implements IDao<Member> {
    private IDbConnect database;

    public DaoMembers(IDbConnect database) {
        this.database = database;
    }

    public void rebuildTable(){
        database.execSql(SQL_DELETE_ENTRIES);
        database.execSql(SQL_CREATE_TABLE);
    }

    @Override
    public long save(@NonNull Member member) {
        database.beginTransaction();
        Map ts = convertForDB(member);
        long res = database.insertOrUpdate(TABLE_NAME, ts);
        if (res != 0 ) member.setId(res);
        database.endTransaction();
        return res;
    }

    @Override
    public long save(@NonNull List<Member> members) {
        long cntr = 0;
        for (Member m : members) {
            if (m.getUid() != 0) {
                if (save(m) != 0) cntr++;
            }
        }
        return cntr;
    }

    @Override
    public Member getByUid(long uid) {
        Member res = new Member(0, "");
        database.beginTransaction();
        if (uid > 0) {
            String filter = " " + COLUMN_NAME_FO_ID + " = " + uid;
            Cursor memberCursor = database.query(TABLE_NAME,
                    getColumnForSelect(),
                    filter,
                    COLUMN_NAME_PATH);

            if (memberCursor.moveToFirst()) {
                res.setId(memberCursor.getLong(0));
                res.setUid(memberCursor.getLong(1));
                res.setName(memberCursor.getString(2));
                res.setPath(memberCursor.getString(3));
                res.setColorIndex(memberCursor.getInt(5));
                res.setTasksCnt(memberCursor.getInt(6));
            }
        }
        database.endTransaction();
        return res;
    }

    @Override
    public boolean isExistInDB(long uid) {
        Member res = getByUid(uid);
        return (res.getUid() == uid);
    }

    @Override
    public List<Member> load (){
        List<Member> members = new LinkedList<>();
        int taskCnt = 0;

        database.beginTransaction();
        Cursor data = database.query(TABLE_NAME,
                getColumnForSelect(),
                null,
                COLUMN_NAME_PATH + " ASC");

        Member any = null;

        if (data.moveToFirst()){
            do {
                long uid = data.getLong(1);
                Member m = new Member(uid, data.getString(2));
                m.setId(data.getLong(0));
                m.setPath(data.getString(3));
                m.setColorIndex(data.getInt(5));
                if (uid == -1) {
                    any = m;
                } else {
                    int t = data.getInt(6);
                    m.setTasksCnt(t);
                    if (m.getLevel() == 1) taskCnt += t;
                    members.add(m);
                }
            } while (data.moveToNext());
        }
        if (any != null) {
            any.setTasksCnt(taskCnt);
            members.add(0,any);
        }
        database.endTransaction();
        return members;
    }

    private Map<String, Object> convertForDB(Member member) {
        Map<String, Object> res = new HashMap<>();
        res.put(BaseColumns._ID, member.getId());
        res.put(COLUMN_NAME_FO_ID, member.getUid());
        res.put(COLUMN_NAME_TITLE,member.getName());
        res.put(COLUMN_NAME_PATH,member.getPath());
        res.put(COLUMN_NAME_LEVEL, member.getLevel());
        res.put(COLUMN_NAME_COLOR, member.getColorIndex());
        res.put(COLUMN_NAME_TASKS, member.getTasksCnt());
        return res;
    }

    private String[] getColumnForSelect() {
        return new String[]{
                BaseColumns._ID,
                DBContract.COLUMN_NAME_FO_ID,
                DBContract.COLUMN_NAME_TITLE,
                COLUMN_NAME_PATH,
                COLUMN_NAME_LEVEL,
                COLUMN_NAME_COLOR,
                "(" + DBContract.MemberObjectsTable.SQL_QUERY_COUNT_OBJECTS + COLUMN_NAME_FO_ID +
                        ") AS TaskCnt"};
    }
}
