package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.views.ErrorsHandler;

/**
 * Created by vat on 21.12.2015.
 */
public class DB {
    private static final String CLASS_NAME = "DB";

    private SQLiteDatabase database;
    private App app;

    public DB(App application, long db_version) {

        app = application;
        FOTT_DBHelper helper = new FOTT_DBHelper(application, app);
        database = helper.getWritableDatabase();
        if (db_version != helper.getDB_version()) {
            //DB structure was changed and all records were deleted
            //Need to reload from web-service
            application.setNeedFullSync(true);
        }
    }

    public long getDb_version() {
        return FOTT_DBContract.DATABASE_VERSION;
    }

    public void execSQL(String sql){
        database.execSQL(sql);
    }

    public long insert(String table, ContentValues values){
        long res = database.insert(table, null, values);
        if (res == -1) app.getError().error_handler(ErrorsHandler.ERROR_LOG_MESSAGE, CLASS_NAME, app.getString(R.string.db_insert_empty_row));
        return res;
    }

    public long insertOrUpdate(String table, ContentValues values){
        long res = database.insertWithOnConflict(table, null, values, database.CONFLICT_REPLACE);
        if (res == -1) app.getError().error_handler(ErrorsHandler.ERROR_LOG_MESSAGE, CLASS_NAME, app.getString(R.string.db_insert_empty_row));
        return res;
    }

    public Cursor query(String table, String[] columns, String filter, String order){
        return database.query(table,columns,filter,null,null,null,order);
    }

    public int update(String table, ContentValues values, String whereClause){
        return database.update(table, values, whereClause, null);
    }


    public void delete(String table,String whereClause) {
        if (database.delete(table,whereClause,null) == -1)
            app.getError().error_handler(ErrorsHandler.ERROR_LOG_MESSAGE,CLASS_NAME,"Errors while deleting tasks from database");
    }
}
