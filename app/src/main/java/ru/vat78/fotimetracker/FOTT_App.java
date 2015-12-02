package ru.vat78.fotimetracker;

import android.app.Application;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.util.ArrayList;

import ru.vat78.fotimetracker.database.FOTT_Contract;
import ru.vat78.fotimetracker.database.FOTT_DBHelper;
import ru.vat78.fotimetracker.fo_api.FOAPI_Connector;
import ru.vat78.fotimetracker.fo_api.FOAPI_Members;

/**
 * Created by vat on 24.11.2015.
 *
 * Main application
 */
public class FOTT_App extends Application {

    private FOAPI_Connector web_service;
    private SQLiteDatabase database;

    private long curMember;
    private long curTask;
    private long curTimeslot;

    @Override
    public void onCreate() {
        super.onCreate();
        web_service = new FOAPI_Connector();
        FOTT_DBHelper helper = new FOTT_DBHelper(this);
        database = helper.getWritableDatabase();
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public FOAPI_Connector getWeb_service() {
        return web_service;
    }

    public boolean syncFOFull() {
        boolean res=true;
        res = res & syncMembers();
        return res;
    }

    private boolean syncMembers() {

        if (curMember > 0){

        }
        database.execSQL(FOTT_Contract.FOTT_Members.SQL_DELETE_ENTRIES);
        database.execSQL(FOTT_Contract.FOTT_Members.SQL_CREATE_ENTRIES);
        ArrayList<ContentValues> members;
        try {
            members = FOAPI_Members.load(web_service);
            if (members == null) {members = new ArrayList<>();}
            ContentValues any = new ContentValues();
            any.put(FOTT_Contract.FOTT_Members.COLUMN_NAME_MEMBER_ID,0);
            any.put(FOTT_Contract.FOTT_Members.COLUMN_NAME_NAME,getString(R.string.any_category));
            any.put(FOTT_Contract.FOTT_Members.COLUMN_NAME_PATH,"/");
            any.put(FOTT_Contract.FOTT_Members.COLUMN_NAME_LEVEL,0);
            any.put(FOTT_Contract.FOTT_Members.COLUMN_NAME_COLOR, Color.TRANSPARENT);
            members.add(any);
            for (int i = 0; i < members.size(); i++) {
                database.insert(FOTT_Contract.FOTT_Members.TABLE_NAME, null, members.get(i));
            }
        }
        catch (Error e){
            return  false;
        }
        return true;
    }
}
