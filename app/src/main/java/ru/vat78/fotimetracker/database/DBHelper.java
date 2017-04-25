package ru.vat78.fotimetracker.database;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.vat78.fotimetracker.App;

import static ru.vat78.fotimetracker.database.DBContract.*;

/**
 * Created by vat on 25.11.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private App MainApp;

    public int getDB_version() {
        return DATABASE_VERSION;
    }

    public DBHelper(Context context, App app) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        MainApp = app;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MembersTable.SQL_CREATE_TABLE);
        db.execSQL(TasksTable.SQL_CREATE_TABLE);
        db.execSQL(TimeslotsTable.SQL_CREATE_ENTRIES);
        db.execSQL(MemberObjectsTable.SQL_CREATE_TABLE);
        MainApp.setNeedFullSync(true);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(MembersTable.SQL_DELETE_ENTRIES);
        db.execSQL(TasksTable.SQL_DELETE_ENTRIES);
        db.execSQL(TimeslotsTable.SQL_DELETE_ENTRIES);
        db.execSQL(MemberObjectsTable.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
