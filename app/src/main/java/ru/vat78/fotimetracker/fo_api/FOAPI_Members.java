package ru.vat78.fotimetracker.fo_api;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;

import ru.vat78.fotimetracker.database.FOTT_Contract;
import ru.vat78.fotimetracker.model.FOTT_Member;

/**
 * Created by vat on 27.11.2015.
 */
public class FOAPI_Members {
    private static FOAPI_Connector FOApp;

    protected FOAPI_Members(FOAPI_Connector web_service) {
        FOApp = web_service;
    }

    public static ContentValues[] load(){

        JSONObject jo = FOApp.executeAPI(FOAPI_Dictionary.FO_METHOD_MEMBERS,FOAPI_Dictionary.FO_MEMBERS_WORKSPACE);
        return convertResults(jo);
    }

    private static ContentValues[] convertResults(JSONObject data){

        if (data == null) {return null;}
        JSONArray list;
        JSONObject jo;
        ContentValues[] res = null;
        try {
            list = data.getJSONArray(FOAPI_Dictionary.FO_API_MAIN_OBJ);

            for (int i = 0; i < list.length(); i++) {
                ContentValues el = new ContentValues();
                jo = list.getJSONObject(i);

                el.put(FOTT_Contract.FOTT_Members.COLUMN_NAME_MEMBER_ID,jo.getInt(FOAPI_Dictionary.FO_API_FIELD_ID));
                el.put(FOTT_Contract.FOTT_Members.COLUMN_NAME_NAME,jo.getString(FOAPI_Dictionary.FO_API_FIELD_NAME));
                el.put(FOTT_Contract.FOTT_Members.COLUMN_NAME_PATH,jo.getString(FOAPI_Dictionary.FO_API_FIELD_PATH));
                el.put(FOTT_Contract.FOTT_Members.COLUMN_NAME_TYPE,jo.getString(FOAPI_Dictionary.FO_API_FIELD_TYPE));
                el.put(FOTT_Contract.FOTT_Members.COLUMN_NAME_COLOR,jo.getString(FOAPI_Dictionary.FO_API_FIELD_COLOR));

                res[i] = el;
            }

        } catch (Exception e) {
            Log.e("FOTT",e.getMessage());
        }
        return res;
    }
}
