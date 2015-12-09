package ru.vat78.fotimetracker.fo_api;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.vat78.fotimetracker.database.FOTT_DBContract;

/**
 * Created by vat on 30.11.2015.
 */
public class FOAPI_Tasks {

    public static ArrayList<ContentValues> load(FOAPI_Connector web_service){
        JSONObject jo = web_service.executeAPI(FOAPI_Dictionary.FO_METHOD_LISTING,FOAPI_Dictionary.FO_SERVICE_TASKS,
                new String[] {FOAPI_Dictionary.FO_API_ARG_STATUS, "0"});
        return convertResults(jo);
    }

    private static ArrayList<ContentValues> convertResults(JSONObject data){

        if (data == null) {return null;}
        JSONArray list = null;
        JSONObject jo;
        String tmp;
        ArrayList<ContentValues> res = new ArrayList<>();
        try {
            list = data.getJSONArray(FOAPI_Dictionary.FO_API_MAIN_OBJ);
        } catch (Exception e) {
            Log.e("FOTT", e.getMessage());
        }
        if (list == null) {return null;}

        for (int i = 0; i < list.length(); i++) {
            ContentValues el = new ContentValues();
            try {
                jo = list.getJSONObject(i);

                el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_TASK_ID,jo.getInt(FOAPI_Dictionary.FO_API_FIELD_ID));
                el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_TITLE,jo.getString(FOAPI_Dictionary.FO_API_FIELD_NAME));

                if (jo.isNull(FOAPI_Dictionary.FO_API_FIELD_DESC)) {
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_DESC,"");
                } else {
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_DESC, jo.getString(FOAPI_Dictionary.FO_API_FIELD_DESC));
                }

                String m = "";
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_MEMPATH)) {
                    JSONArray ja = jo.getJSONArray(FOAPI_Dictionary.FO_API_FIELD_MEMPATH);
                    for (int j = 0; j < ja.length(); j++)
                        m = m + ja.getString(j) + ",";
                }
                el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_MEMPATH,m);

                tmp = FOAPI_Dictionary.FO_API_FALSE;
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_STARTDATE))
                    tmp = jo.getString(FOAPI_Dictionary.FO_API_FIELD_STARTDATE);
                if (tmp == FOAPI_Dictionary.FO_API_FALSE) {
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_STARTDATE,0);
                } else {
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_STARTDATE, jo.getLong(FOAPI_Dictionary.FO_API_FIELD_STARTDATE));
                }

                tmp = FOAPI_Dictionary.FO_API_FALSE;
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_DUEDATE))
                    tmp = jo.getString(FOAPI_Dictionary.FO_API_FIELD_DUEDATE);
                if (tmp == FOAPI_Dictionary.FO_API_FALSE) {
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_DUEDATE,0);
                } else {
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_DUEDATE, jo.getLong(FOAPI_Dictionary.FO_API_FIELD_DUEDATE));
                }

                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_PRIORITY))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_PRIORITY,jo.getInt(FOAPI_Dictionary.FO_API_FIELD_PRIORITY));
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_ASSIGNEDBY))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_ASSIGNEDBY,jo.getString(FOAPI_Dictionary.FO_API_FIELD_ASSIGNEDBY));
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_ASSIGNEDTO))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_ASSIGNEDTO,jo.getString(FOAPI_Dictionary.FO_API_FIELD_ASSIGNEDTO));
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_STATUS))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_STATUS,jo.getString(FOAPI_Dictionary.FO_API_FIELD_STATUS));
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_PERCENT))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_PERCENT,jo.getInt(FOAPI_Dictionary.FO_API_FIELD_PERCENT));
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_WORKEDTIME))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_WORKEDTIME,jo.getLong(FOAPI_Dictionary.FO_API_FIELD_WORKEDTIME));
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_PENDINGTIME))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_PENDINGTIME,jo.getLong(FOAPI_Dictionary.FO_API_FIELD_PENDINGTIME));
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_USETIMESLOTS))
                    el.put(FOTT_DBContract.FOTT_DBTasks.COLUMN_NAME_USETIMESLOTS,(jo.getString(FOAPI_Dictionary.FO_API_FIELD_USETIMESLOTS) == FOAPI_Dictionary.FO_API_TRUE));

            }
            catch (Exception e) {
                Log.e("FOTT", e.getMessage());
            }
            finally {
                if (el.size()>0)
                    if (!res.add(el)) {break;}
            }
        }


        return res;
    }
}
