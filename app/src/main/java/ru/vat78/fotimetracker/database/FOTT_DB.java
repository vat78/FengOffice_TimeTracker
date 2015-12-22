package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.util.ArrayList;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.fo_api.FOAPI_Members;
import ru.vat78.fotimetracker.fo_api.FOAPI_Tasks;
import ru.vat78.fotimetracker.fo_api.FOAPI_Timeslots;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
import ru.vat78.fotimetracker.views.FOTT_ErrorsHandler;

/**
 * Created by vat on 21.12.2015.
 */
public class FOTT_DB {
    private static final String CLASS_NAME = "FOTT_DB";

    private SQLiteDatabase database;
    private FOTT_App app;

    public FOTT_DB(FOTT_App application, int db_version) {

        app = application;
        FOTT_DBHelper helper = new FOTT_DBHelper(application, db_version, app);
        database = helper.getWritableDatabase();
        if (db_version != helper.getDb_version()) {
            //DB structure was changed and all records were deleted
            //Need to reload from web-service
            application.setNeedFullSync();
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
        if (res == -1) app.getError().error_handler(FOTT_ErrorsHandler.ERROR_LOG_MESSAGE, CLASS_NAME, app.getString(R.string.db_insert_empty_row));
        return res;
    }

    public long insertOrUpdate(String table, ContentValues values){
        long res = database.insertWithOnConflict(table, null, values, database.CONFLICT_REPLACE);
        if (res == -1) app.getError().error_handler(FOTT_ErrorsHandler.ERROR_LOG_MESSAGE, CLASS_NAME, app.getString(R.string.db_insert_empty_row));
        return res;
    }

    public Cursor query(String table, String[] columns, String filter, String order){
        return database.query(table,columns,filter,null,null,null,order);
    }








}
