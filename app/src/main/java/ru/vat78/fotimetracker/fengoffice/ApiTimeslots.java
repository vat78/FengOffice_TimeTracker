package ru.vat78.fotimetracker.fengoffice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.model.Timeslot;
import ru.vat78.fotimetracker.views.ErrorsHandler;

import static ru.vat78.fotimetracker.fengoffice.ApiDictionary.*;

/**
 * Created by vat on 04.12.2015.
 */
public class ApiTimeslots {
    private static final String CLASS_NAME = "ApiTimeslots";

    public static ArrayList<Timeslot> load(App app){
        JSONObject jo = app.getWeb_service().executeAPI(FO_METHOD_LISTING, FO_SERVICE_TIMESLOTS);
        return convertResults(app, jo);
    }

    public static ArrayList<Timeslot> load(App app, Date timestamp){
        String[] args = new String[2];
        args[0] = FO_API_ARG_LASTUPDATE;
        long l = (long) timestamp.getTime() / FO_API_DATE_CONVERTOR;
        args[1] = "" + l;
        JSONObject jo = app.getWeb_service().executeAPI(FO_METHOD_LISTING, FO_SERVICE_TIMESLOTS, args);
        return convertResults(app,jo);
    }

    public static long save(App app, Timeslot timeslot) {
        long res = 0;
        if (timeslot == null) return res;

        String[] args = convertTSForAPI(timeslot);
        JSONObject jo = app.getWeb_service().executeAPI(FO_METHOD_SAVE_OBJ, FO_SERVICE_TIMESLOTS, args);
        if (jo != null) {
                try {
                    res = jo.getLong(FO_API_FIELD_ID);
                } catch (Exception e){
                }
        }
        return res;
    }

    private static String[] convertTSForAPI(Timeslot timeslot) {
        String[] args = new String[11];
        long l = 0;
        args[0] = FO_API_FIELD_ID;
        args[1] = "";
        if (timeslot.getId() > 0) args[1] = "" + timeslot.getId();
        args[2] = FO_API_FIELD_DESC;
        args[3] = timeslot.getDesc();
        args[4] = FO_API_FIELD_TS_DATE;
        l = (long) timeslot.getStart().getTime() / FO_API_DATE_CONVERTOR;
        args[5] = "" + l;
        args[6] = FO_API_FIELD_TS_DURATION;
        l = (long) timeslot.getDuration() / FO_API_DATE_CONVERTOR;
        args[7] = "" + l;
        args[8] = FO_API_FIELD_TS_TASK;
        args[9] = timeslot.getTaskId() == 0 ? "" : "" + timeslot.getTaskId();
        if (!timeslot.getMembersIds().isEmpty() && timeslot.getTaskId() == 0) {
            args[10] = FO_API_FIELD_MEMBERS;
            args[11] = "[";
            String[] members = timeslot.getMembersArray();
            for (String member : members) args[11] += "\"" + member + "\",";
            args[11] += "]";
        }
        return args;
    }

    private static ArrayList<Timeslot> convertResults(App app, JSONObject data){

        if (data == null) {return null;}
        JSONArray list = null;
        JSONObject jo;
        String tmp;
        ArrayList<Timeslot> res = new ArrayList<>();
        try {
            list = data.getJSONArray(FO_API_MAIN_OBJ);
        } catch (Exception e) {
            app.getError().error_handler(ErrorsHandler.ERROR_SAVE_ERROR,CLASS_NAME,e.getMessage());
        }
        if (list == null) {return null;}

        for (int i = 0; i < list.length(); i++) {
            Timeslot el = null;
            try {
                jo = list.getJSONObject(i);
                long id = jo.getLong(FO_API_FIELD_ID);
                String s;
                if (jo.isNull(FO_API_FIELD_TS_DESC)) {
                    s="";
                } else {
                    s= jo.getString(FO_API_FIELD_TS_DESC);
                }
                el = new Timeslot(id,s);

                s = "";
                if (!jo.isNull(FO_API_FIELD_MEMPATH)) {
                    JSONArray ja = jo.getJSONArray(FO_API_FIELD_MEMPATH);
                    for (int j = 0; j < ja.length(); j++)
                        s = s + ja.getString(j) + "/";
                }
                el.setMembersIDs(s);

                tmp = FO_API_FALSE;
                if (!jo.isNull(FO_API_FIELD_TS_DATE))
                    tmp = jo.getString(FO_API_FIELD_TS_DATE);
                if (tmp == FO_API_FALSE) {
                    el.setStart(0);
                } else {
                    el.setStart(jo.getLong(FO_API_FIELD_TS_DATE)*FO_API_DATE_CONVERTOR);
                }

                if (jo.isNull(FO_API_FIELD_TS_DURATION)) {
                    el.setDuration(FO_API_DATE_CONVERTOR);
                } else {
                    el.setDuration(jo.getLong(FO_API_FIELD_TS_DURATION)*FO_API_DATE_CONVERTOR);
                }
                if (jo.isNull(FO_API_FIELD_TS_TASK)){
                    el.setTaskId(0);
                } else {
                    el.setTaskId(jo.getLong(FO_API_FIELD_TS_TASK));
                }
                if (jo.isNull(FO_API_FIELD_LAST_UPDATE)){
                    el.setChanged(0);
                } else {
                    el.setChanged(jo.getLong(FO_API_FIELD_LAST_UPDATE)*FO_API_DATE_CONVERTOR);
                }
                if (jo.isNull(FO_API_FIELD_UPDATE_BY)){
                    el.setAuthor("");
                } else {
                    el.setAuthor(jo.getString(FO_API_FIELD_UPDATE_BY));
                }

            }
            catch (Exception e) {
                app.getError().error_handler(ErrorsHandler.ERROR_LOG_MESSAGE,CLASS_NAME, e.getMessage());
            }
            finally {
                if (el != null)
                    if (!res.add(el)) {break;}
            }
        }

        return res;
    }

    public static boolean delete(App app, Timeslot timeslot) {
        boolean res = false;

        if (timeslot.getId() > 0) {
            JSONObject jo = app.getWeb_service().executeAPI(FO_METHOD_DELETE_OBJ, timeslot.getId());
            try {
                res = (jo.getString(FO_API_FIELD_RESULT) != FO_API_TRUE);
            } catch (Exception e) {
            }
        }

        return res;
    }
}
