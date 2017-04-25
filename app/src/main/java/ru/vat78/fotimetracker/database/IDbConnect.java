package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Closeable;

/**
 * Created by vat on 20.04.17.
 */
public interface IDbConnect extends Closeable {
    public void execSql(String sql);
    public long insert(String table, ContentValues entry);
    public long insertOrUpdate(String table, ContentValues entry);
    public Cursor query(String table, String[] columns, String filter, String order);

    public void beginTransaction();
    public void endTransaction();
}
