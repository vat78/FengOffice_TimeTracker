package ru.vat78.fotimetracker.fo_api;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.vat78.fotimetracker.database.FOTT_Contract;

/**
 * Created by vat on 30.11.2015.
 */
public class FOAPI_Tasks {

    private static FOAPI_Connector FOApp;

    protected FOAPI_Tasks(FOAPI_Connector web_service) {
        FOApp = web_service;
    }

    public static ContentValues[] load(){
        JSONObject jo = FOApp.executeAPI(FOAPI_Dictionary.FO_METHOD_LISTING,FOAPI_Dictionary.FO_SERVICE_TASKS);
        return convertResults(jo);
    }

    private static ContentValues[] convertResults(JSONObject data){

        if (data == null) {return null;}
        JSONArray list;
        JSONObject jo;
        String tmp;
        ContentValues[] res = null;
        try {
            list = data.getJSONArray(FOAPI_Dictionary.FO_API_MAIN_OBJ);

            for (int i = 0; i < list.length(); i++) {
                ContentValues el = new ContentValues();
                jo = list.getJSONObject(i);

                el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_TASK_ID,jo.getInt(FOAPI_Dictionary.FO_API_FIELD_ID));
                el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_TITLE,jo.getString(FOAPI_Dictionary.FO_API_FIELD_NAME));
                el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_DESC,jo.getString(FOAPI_Dictionary.FO_API_FIELD_DESC));
                el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_MEMBERS,jo.getString(FOAPI_Dictionary.FO_API_FIELD_MEMBERS));
                el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_STATUS,jo.getString(FOAPI_Dictionary.FO_API_FIELD_STATUS));
                tmp = jo.getString(FOAPI_Dictionary.FO_API_FIELD_STARTDATE);
                if (tmp == FOAPI_Dictionary.FO_API_FALSE) {
                    el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_STARTDATE,0);
                } else {
                    el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_STARTDATE, jo.getLong(FOAPI_Dictionary.FO_API_FIELD_STARTDATE));
                }
                tmp = jo.getString(FOAPI_Dictionary.FO_API_FIELD_DUEDATE);
                if (tmp == FOAPI_Dictionary.FO_API_FALSE) {
                    el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_DUEDATE,0);
                } else {
                    el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_DUEDATE, jo.getLong(FOAPI_Dictionary.FO_API_FIELD_DUEDATE));
                }
                el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_PRIORITY,jo.getInt(FOAPI_Dictionary.FO_API_FIELD_PRIORITY));
                el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_ASSIGNEDBY,jo.getString(FOAPI_Dictionary.FO_API_FIELD_ASSIGNEDBY));
                el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_ASSIGNEDTO,jo.getString(FOAPI_Dictionary.FO_API_FIELD_ASSIGNEDTO));
                el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_PERCENT,jo.getInt(FOAPI_Dictionary.FO_API_FIELD_PERCENT));
                el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_WORKEDTIME,jo.getLong(FOAPI_Dictionary.FO_API_FIELD_WORKEDTIME));
                el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_PENDINGTIME,jo.getLong(FOAPI_Dictionary.FO_API_FIELD_PENDINGTIME));
                el.put(FOTT_Contract.FOTT_Tasks.COLUMN_NAME_USETIMESLOTS,(jo.getString(FOAPI_Dictionary.FO_API_FIELD_USETIMESLOTS) == FOAPI_Dictionary.FO_API_TRUE));

                res[i] = el;
            }

        } catch (Exception e) {
            Log.e("FOTT", e.getMessage());
        }
        return res;
    }
}
