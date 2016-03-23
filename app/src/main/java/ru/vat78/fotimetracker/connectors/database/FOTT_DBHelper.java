package ru.vat78.fotimetracker.connectors.database;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.vat78.fotimetracker.FOTT_App;

import static ru.vat78.fotimetracker.connectors.database.FOTT_DBContract.*;

/**
 * Created by vat on 25.11.2015.
 */
public class FOTT_DBHelper extends SQLiteOpenHelper {

    private  int db_version;
    private FOTT_App MainApp;

    public FOTT_DBHelper(Context context, FOTT_App app) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        MainApp = app;
        db_version = DATABASE_VERSION;
    }

    public int getDB_version() {
        return db_version;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FOTT_DBContract.MEMBERS_TABLE_CREATE);
        db.execSQL(FOTT_DBContract.TASK_TABLE_CREATE);
        db.execSQL(FOTT_DBContract.TIMESLOTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(FOTT_DBContract.MEMBERS_TABLE_DELETE);
        db.execSQL(FOTT_DBContract.TASK_TABLE_DELETE);
        db.execSQL(FOTT_DBContract.TIMESLOTS_TABLE_DELETE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
