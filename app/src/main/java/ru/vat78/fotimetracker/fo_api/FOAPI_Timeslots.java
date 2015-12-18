package ru.vat78.fotimetracker.fo_api;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.adapters.FOTT_TimeslotsAdapter;
import ru.vat78.fotimetracker.database.FOTT_DBContract;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;

import static ru.vat78.fotimetracker.fo_api.FOAPI_Dictionary.*;

/**
 * Created by vat on 04.12.2015.
 */
public class FOAPI_Timeslots {

    public static ArrayList<ContentValues> load(FOAPI_Connector web_service){
        JSONObject jo = web_service.executeAPI(FO_METHOD_LISTING, FO_SERVICE_TIMESLOTS);
        return convertResults(jo);
    }

    public static ArrayList<ContentValues> load(FOAPI_Connector web_service, long timestamp){
        String[] args = new String[1];
        args[0] = FO_API_ARG_LASTUPDATE;
        args[1] = "" + timestamp;
        JSONObject jo = web_service.executeAPI(FO_METHOD_LISTING, FO_SERVICE_TIMESLOTS,args);
        return convertResults(jo);
    }

    public static boolean saveChangedTimeslots(FOTT_App app, FOTT_TimeslotsAdapter timeslots){

        boolean res = true;
        long l = app.getLastSync();

        for (int i=0; i < timeslots.getItemCount(); i++) {
            try {
                FOTT_Timeslot ts = timeslots.getItem(i);
                if (ts.getChanged() > l) {
                    save(app.getWeb_service(), ts);
                }
            } catch (Exception e) {
                res = false;
            }
        }
        return res;
    }

    private static boolean save(FOAPI_Connector web_service, FOTT_Timeslot timeslot) {
        String[] args = new String[11];
        long l = 0;
        args[0] = FO_API_FIELD_ID;
        args[1] = "" + timeslot.getId();
        args[2] = FO_API_FIELD_DESC;
        args[3] = timeslot.getDesc();
        args[4] = FO_API_FIELD_TS_DATE;
        l = (long) timeslot.getStart().getTime() / 1000;
        args[5] = "" + l;
        args[6] = FO_API_FIELD_TS_DURATION;
        l = (long) timeslot.getDuration() / 1000;
        args[7] = "" + l;
        args[8] = FO_API_FIELD_TS_TASK;
        args[9] = timeslot.getTaskId() == 0 ? "" : "" + timeslot.getTaskId();
        if (timeslot.getMemberId() > 0) {
            args[10] = FO_API_FIELD_MEMBERS;
            args[11] = "[\"" + timeslot.getMemberId() + "\"]";
        }

        JSONObject jo = web_service.executeAPI(FO_METHOD_SAVE_OBJ, FO_SERVICE_TIMESLOT,args);
        return jo.toString() == "[\"true\"]";
    }

    private static ArrayList<ContentValues> convertResults(JSONObject data){

        if (data == null) {return null;}
        JSONArray list = null;
        JSONObject jo;
        String tmp;
        ArrayList<ContentValues> res = new ArrayList<>();
        try {
            list = data.getJSONArray(FO_API_MAIN_OBJ);
        } catch (Exception e) {
            Log.e("FOTT", e.getMessage());
        }
        if (list == null) {return null;}

        for (int i = 0; i < list.length(); i++) {
            ContentValues el = new ContentValues();
            try {
                jo = list.getJSONObject(i);
                el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_TIMESLOT_ID,jo.getInt(FO_API_FIELD_ID));
                if (jo.isNull(FO_API_FIELD_TS_DESC)) {
                    el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_TITLE,"");
                } else {
                    el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_TITLE, jo.getString(FO_API_FIELD_TS_DESC));
                }

                String m = "";
                if (!jo.isNull(FO_API_FIELD_MEMPATH)) {
                    JSONArray ja = jo.getJSONArray(FO_API_FIELD_MEMPATH);
                    for (int j = 0; j < ja.length(); j++)
                        m = m + ja.getString(j) + ",";
                }
                el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_MEMBERS_ID,m);

                tmp = FO_API_FALSE;
                if (!jo.isNull(FO_API_FIELD_TS_DATE))
                    tmp = jo.getString(FO_API_FIELD_TS_DATE);
                if (tmp == FO_API_FALSE) {
                    el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_START,0);
                } else {
                    el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_START, jo.getLong(FO_API_FIELD_TS_DATE)*1000);
                }

                if (jo.isNull(FO_API_FIELD_TS_DURATION)) {
                    el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_DURATION,10);
                } else {
                    el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_DURATION, jo.getInt(FO_API_FIELD_TS_DURATION)*1000);
                }
                if (jo.isNull(FO_API_FIELD_TS_TASK)){
                    el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_TASK_ID,0);
                } else {
                    el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_TASK_ID, jo.getInt(FO_API_FIELD_TS_TASK));
                }
                if (jo.isNull(FO_API_FIELD_LAST_UPDATE)){
                    el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_CHANGED,0);
                } else {
                    el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_CHANGED, jo.getLong(FO_API_FIELD_LAST_UPDATE)*1000);
                }
                if (jo.isNull(FO_API_FIELD_UPDATE_BY)){
                    el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_CHANGED_BY,0);
                } else {
                    el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_CHANGED_BY, jo.getString(FO_API_FIELD_UPDATE_BY));
                }

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
