package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Parcel;
import ru.vat78.fotimetracker.IErrorsHandler;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.model.ErrorsType;
import ru.vat78.fotimetracker.views.ErrorsHandler;

import java.io.IOException;
import java.util.Map;

/**
 * Created by vat on 21.12.2015.
 */
public class DB implements IDbConnect {
    private static final String CLASS_NAME = "DB";

    private final long currentDbVersion;
    private final IErrorsHandler errorsHandler;
    private SQLiteDatabase database;

    public DB(Context context, IErrorsHandler errorsHandler) {
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

    /*

    public int update(String table, Map<String,Object> values, String whereClause){
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


}
