package ru.vat78.fotimetracker.database;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static ru.vat78.fotimetracker.database.FOTT_DBContract.*;

/**
 * Created by vat on 25.11.2015.
 */
public class FOTT_DBHelper extends SQLiteOpenHelper {

    private  int db_version;

    public int getDb_version() {
        return db_version;
    }

    public FOTT_DBHelper(Context context, int database_version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db_version = database_version;
    }
    public void onCreate(SQLiteDatabase db) {
        if (DATABASE_VERSION > db_version)
        {
            int tmp = db_version;
            db_version = DATABASE_VERSION;
            this.onUpgrade(db,tmp,DATABASE_VERSION);
        } else {
            db.execSQL(FOTT_DBMembers.SQL_CREATE_ENTRIES);
            db.execSQL(FOTT_DBTasks.SQL_CREATE_ENTRIES);
            db.execSQL(FOTT_DBTimeslots.SQL_CREATE_ENTRIES);
        }
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(FOTT_DBMembers.SQL_DELETE_ENTRIES);
        db.execSQL(FOTT_DBTasks.SQL_DELETE_ENTRIES);
        db.execSQL(FOTT_DBTimeslots.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
