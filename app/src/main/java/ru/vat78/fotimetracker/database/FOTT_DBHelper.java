package ru.vat78.fotimetracker.database;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vat on 25.11.2015.
 */
public class FOTT_DBHelper extends SQLiteOpenHelper {

    public FOTT_DBHelper(Context context) {
        super(context, FOTT_Contract.DATABASE_NAME, null, FOTT_Contract.DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(FOTT_Contract.FOTT_Members.SQL_CREATE_ENTRIES);
        db.execSQL(FOTT_Contract.FOTT_Tasks.SQL_CREATE_ENTRIES);
        db.execSQL(FOTT_Contract.FOTT_Timeslots.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(FOTT_Contract.FOTT_Members.SQL_DELETE_ENTRIES);
        db.execSQL(FOTT_Contract.FOTT_Tasks.SQL_DELETE_ENTRIES);
        db.execSQL(FOTT_Contract.FOTT_Timeslots.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
