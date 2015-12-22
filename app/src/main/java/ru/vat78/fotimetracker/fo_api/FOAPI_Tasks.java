package ru.vat78.fotimetracker.fo_api;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.views.FOTT_ErrorsHandler;

import static ru.vat78.fotimetracker.fo_api.FOAPI_Dictionary.*;

/**
 * Created by vat on 30.11.2015.
 */
public class FOAPI_Tasks {
    private static final String CLASS_NAME = "FOAPI_Tasks";

    public static ArrayList<FOTT_Task> load(FOTT_App app){
        JSONObject jo = app.getWeb_service().executeAPI(FOAPI_Dictionary.FO_METHOD_LISTING,FOAPI_Dictionary.FO_SERVICE_TASKS,
                new String[] {FOAPI_Dictionary.FO_API_ARG_STATUS, "0"});
        return convertResults(app,jo);
    }

    public static ArrayList<FOTT_Task> load(FOTT_App app, Date timestamp){
        String[] args = new String[4];
        args[0]= FO_API_ARG_STATUS;
        args[1] = "0";
        args[2] = FO_API_ARG_LASTUPDATE;
        long l = (long) timestamp.getTime() / FO_API_DATE_CONVERTOR;
        args[3] = "" + l;
        JSONObject jo = app.getWeb_service().executeAPI(FO_METHOD_LISTING,FO_SERVICE_TASKS, args);
        return convertResults(app,jo);
    }

    private static ArrayList<FOTT_Task> convertResults(FOTT_App app, JSONObject data){

        if (data == null) {return null;}
        JSONArray list = null;
        JSONObject jo;
        String tmp;
        ArrayList<FOTT_Task> res = new ArrayList<>();
        try {
            list = data.getJSONArray(FOAPI_Dictionary.FO_API_MAIN_OBJ);
        } catch (Exception e) {
            app.getError().error_handler(FOTT_ErrorsHandler.ERROR_SAVE_ERROR, CLASS_NAME, e.getMessage());
        }
        if (list == null) {return null;}

        for (int i = 0; i < list.length(); i++) {
            FOTT_Task el = null;
            try {
                jo = list.getJSONObject(i);

                long id = jo.getLong(FOAPI_Dictionary.FO_API_FIELD_ID);
                String s = jo.getString(FOAPI_Dictionary.FO_API_FIELD_NAME);

                el = new FOTT_Task(id, s);

                if (jo.isNull(FOAPI_Dictionary.FO_API_FIELD_DESC)) {
                    el.setDesc("");
                } else {
                    el.setDesc(jo.getString(FOAPI_Dictionary.FO_API_FIELD_DESC));
                }

                s = "";
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_MEMPATH)) {
                    JSONArray ja = jo.getJSONArray(FOAPI_Dictionary.FO_API_FIELD_MEMPATH);
                    for (int j = 0; j < ja.length(); j++)
                        s = s + ja.getString(j) + "/";
                }
                el.setMembersPath(s);

                tmp = FOAPI_Dictionary.FO_API_FALSE;
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_STARTDATE))
                    tmp = jo.getString(FOAPI_Dictionary.FO_API_FIELD_STARTDATE);
                if (tmp == FOAPI_Dictionary.FO_API_FALSE) {
                    el.setStartDate(0);
                } else {
                    el.setStartDate(jo.getLong(FOAPI_Dictionary.FO_API_FIELD_STARTDATE));
                }

                tmp = FOAPI_Dictionary.FO_API_FALSE;
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_DUEDATE))
                    tmp = jo.getString(FOAPI_Dictionary.FO_API_FIELD_DUEDATE);
                if (tmp == FOAPI_Dictionary.FO_API_FALSE) {
                    el.setDueDate(0);
                } else {
                    el.setDueDate(jo.getLong(FOAPI_Dictionary.FO_API_FIELD_DUEDATE));
                }

                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_PRIORITY))
                    el.setPriority(jo.getInt(FOAPI_Dictionary.FO_API_FIELD_PRIORITY));

                /*
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
                */
            }
            catch (Exception e) {
                app.getError().error_handler(FOTT_ErrorsHandler.ERROR_LOG_MESSAGE, CLASS_NAME, e.getMessage());
            }
            finally {
                if (el != null)
                    if (!res.add(el)) {break;}
            }
        }
        return res;
    }
}
