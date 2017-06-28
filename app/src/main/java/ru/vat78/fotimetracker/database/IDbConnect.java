package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Closeable;
import java.util.Map;

/**
 * Created by vat on 20.04.17.
 */
public interface IDbConnect extends Closeable {
    public void execSql(String sql);
    public long insert(String table, Map<String,Object> entry);
    public long insertOrUpdate(String table, Map<String,Object> entry);
    public Cursor query(String table, String[] columns, String filter, String order);
    public boolean delete(String table, long id);

    public void beginTransaction();
    public void endTransaction();

    public long getCurrentDbVersion();

}
