package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.provider.BaseColumns;
import ru.vat78.fotimetracker.IErrorsHandler;
import ru.vat78.fotimetracker.model.ErrorsType;

import java.io.IOException;
import java.util.Map;

import static ru.vat78.fotimetracker.database.DBContract.DATABASE_NAME;
import static ru.vat78.fotimetracker.database.DBContract.DATABASE_VERSION;

/**
 * Created by vat on 21.12.2015.
 */
public class DbSQLite implements IDbConnect {
    private static final String CLASS_NAME = "DbSQLite";

    private final long currentDbVersion;
    private final IErrorsHandler errorsHandler;
    private SQLiteDatabase database;

    public DbSQLite(Context context, IErrorsHandler errorsHandler) {
        this.errorsHandler = errorsHandler;

        DBHelper helper = new DBHelper(context);
        database = helper.getWritableDatabase();
        currentDbVersion = helper.getCurrentDbVersion();
    }

    @Override
    public long getCurrentDbVersion() {
        return currentDbVersion;
    }

    @Override
    public void execSql(String sql){
        database.execSQL(sql);
    }

    @Override
    public void beginTransaction() {
        database.beginTransaction();
    }

    @Override
    public void endTransaction() {
        database.endTransaction();
    }

    @Override
    public long insert(String table, Map<String,Object> values) {
        long res = database.insert(table, null, convertValues(values));
        if (res == -1) errorsHandler.info(CLASS_NAME, ErrorsType.CANT_SAVE_TO_DB);
        return res;
    }

    @Override
    public long insertOrUpdate(String table, Map<String,Object> values){
        long res = database.insertWithOnConflict(table, null, convertValues(values), database.CONFLICT_REPLACE);
        if (res == -1) errorsHandler.info(CLASS_NAME, ErrorsType.CANT_UPDATE_IN_DB);
        return res;
    }

    @Override
    public Cursor query(String table, String[] columns, String filter, String order){
        return database.query(table,columns,filter,null,null,null,order);
    }

    @Override
    public boolean delete(String table, long id) {
        if (database.delete(table, BaseColumns._ID + " = " + id,null) == 1) {
            return true;
        }
        errorsHandler.info(CLASS_NAME, ErrorsType.CANT_DELETE_FROM_DB);
        return true;
    }

    /*

    public int update(String table, Map<String,DbObject> values, String whereClause){
        return database.update(table, convertValues(values), whereClause, null);
    }


    public void delete(String table,String whereClause) {
        if (database.delete(table,whereClause,null) == -1)
            app.getError().error_handler(ErrorsHandler.ERROR_LOG_MESSAGE,CLASS_NAME,"Errors while deleting tasks from database");
    }
    */

    @Override
    public void close() throws IOException {
        database.close();
    }

    private ContentValues convertValues(Map<String, Object> values) {
        if (values == null) return null;
        Parcel tmp = Parcel.obtain();
        tmp.writeMap(values);
        tmp.setDataPosition(0);
        ContentValues res = ContentValues.CREATOR.createFromParcel(tmp);
        return res;
    }


    private class DBHelper extends SQLiteOpenHelper {

        public int getCurrentDbVersion() {
            return DATABASE_VERSION;
        }

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DBContract.MembersTable.SQL_CREATE_TABLE);
            db.execSQL(DBContract.TasksTable.SQL_CREATE_TABLE);
            db.execSQL(DBContract.TimeslotsTable.SQL_CREATE_ENTRIES);
            db.execSQL(DBContract.MemberObjectsTable.SQL_CREATE_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(DBContract.MembersTable.SQL_DELETE_ENTRIES);
            db.execSQL(DBContract.TasksTable.SQL_DELETE_ENTRIES);
            db.execSQL(DBContract.TimeslotsTable.SQL_DELETE_ENTRIES);
            db.execSQL(DBContract.MemberObjectsTable.SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

    }
}
