package ru.vat78.fotimetracker.fengoffice.vatApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.model.Timeslot;
import ru.vat78.fotimetracker.views.ErrorsHandler;

/**
 * Created by vat on 04.12.2015.
 */
public class ApiTimeslots {
    private static final String CLASS_NAME = "ApiTimeslots";

    public ArrayList<Timeslot> load(App app){
        JSONObject jo = app.getWebService().executeAPI(ApiDictionary.FO_METHOD_LISTING, ApiDictionary.FO_SERVICE_TIMESLOTS);
        return convertResults(app, jo);
    }

    public ArrayList<Timeslot> load(App app, Date timestamp){
        String[] args = new String[2];
        args[0] = ApiDictionary.FO_API_ARG_LASTUPDATE;
        long l = (long) timestamp.getTime() / ApiDictionary.FO_API_DATE_CONVERTOR;
        args[1] = "" + l;
        JSONObject jo = app.getWebService().executeAPI(ApiDictionary.FO_METHOD_LISTING, ApiDictionary.FO_SERVICE_TIMESLOTS, args);
        if (!app.getWeb_service().getError().isEmpty())
            app.getError().error_handler(FOTT_ErrorsHandler.ERROR_SAVE_ERROR, CLASS_NAME, app.getWeb_service().getError());
        return convertResults(app,jo);
    }

    public long save(App app, Timeslot timeslot) {
        long res = 0;
        if (timeslot == null) return res;

        String[] args = convertTSForAPI(timeslot);
        JSONObject jo = app.getWebService().executeAPI(ApiDictionary.FO_METHOD_SAVE_OBJ, ApiDictionary.FO_SERVICE_TIMESLOTS, args);
        if (jo != null) {
                try {
                    res = jo.getLong(ApiDictionary.FO_API_FIELD_ID);
                } catch (Exception e){
                }
        }
        return res;
    }

    private String[] convertTSForAPI(Timeslot timeslot) {
        String[] args = new String[12];
        long l;
        args[0] = ApiDictionary.FO_API_FIELD_ID;
        args[1] = "";
        if (timeslot.getId() > 0) args[1] = "" + timeslot.getId();
        args[2] = ApiDictionary.FO_API_FIELD_DESC;
        args[3] = timeslot.getDesc();
        args[4] = ApiDictionary.FO_API_FIELD_TS_DATE;
        l = (long) timeslot.getStart().getTime() / ApiDictionary.FO_API_DATE_CONVERTOR;
        args[5] = "" + l;
        args[6] = ApiDictionary.FO_API_FIELD_TS_DURATION;
        l = (long) timeslot.getDuration() / ApiDictionary.FO_API_DATE_CONVERTOR;
        args[7] = "" + l;
        args[8] = ApiDictionary.FO_API_FIELD_TS_TASK;
        args[9] = timeslot.getTaskId() == 0 ? "" : "" + timeslot.getTaskId();
        if (!timeslot.getMembersIds().isEmpty() && timeslot.getTaskId() == 0) {
            args[10] = ApiDictionary.FO_API_FIELD_MEMBERS;
            args[11] = "[";
            String[] members = timeslot.getMembersArray();
            for (String member : members) args[11] += "\"" + member + "\",";
            args[11] += "\"\"]";
        }
        return args;
    }

    private ArrayList<Timeslot> convertResults(App app, JSONObject data){

        if (data == null) {return null;}
        JSONArray list = null;
        JSONObject jo;
        String tmp;
        ArrayList<Timeslot> res = new ArrayList<>();
        try {
            list = data.getJSONArray(ApiDictionary.FO_API_MAIN_OBJ);
        } catch (Exception e) {
            app.getError().error_handler(ErrorsHandler.ERROR_SAVE_ERROR,CLASS_NAME,e.getMessage());
        }
        if (list == null) {return null;}

        for (int i = 0; i < list.length(); i++) {
            Timeslot el = null;
            try {
                jo = list.getJSONObject(i);
                long id = jo.getLong(ApiDictionary.FO_API_FIELD_ID);
                String s;
                if (jo.isNull(ApiDictionary.FO_API_FIELD_TS_DESC)) {
                    s="";
                } else {
                    s= jo.getString(ApiDictionary.FO_API_FIELD_TS_DESC);
                }
                el = new Timeslot(id,s);

                s = "";
                if (!jo.isNull(ApiDictionary.FO_API_FIELD_MEMPATH)) {
                    JSONArray ja = jo.getJSONArray(ApiDictionary.FO_API_FIELD_MEMPATH);
                    for (int j = 0; j < ja.length(); j++) {
                        String mem = ja.getString(j);
                        if (mem != null && mem.length() > 0) {
                            if (mem.getBytes()[0] == '{') {
                                JSONObject memObj = new JSONObject(mem);
                                mem = memObj.getString("member_id");
                            }
                            s = s + mem +el.getMemberSplitter();
                        }
                    }
                }
                el.setMembersIDs(s);

                tmp = ApiDictionary.FO_API_FALSE;
                if (!jo.isNull(ApiDictionary.FO_API_FIELD_TS_DATE))
                    tmp = jo.getString(ApiDictionary.FO_API_FIELD_TS_DATE);
                if (tmp.equalsIgnoreCase(FO_API_FALSE)) {
                    el.setStart(0);
                } else {
                    el.setStart(jo.getLong(ApiDictionary.FO_API_FIELD_TS_DATE)* ApiDictionary.FO_API_DATE_CONVERTOR);
                }

                if (jo.isNull(ApiDictionary.FO_API_FIELD_TS_DURATION)) {
                    el.setDuration(ApiDictionary.FO_API_DATE_CONVERTOR);
                } else {
                    el.setDuration(jo.getLong(ApiDictionary.FO_API_FIELD_TS_DURATION)* ApiDictionary.FO_API_DATE_CONVERTOR);
                }
                if (jo.isNull(ApiDictionary.FO_API_FIELD_TS_TASK)){
                    el.setTaskId(0);
                } else {
                    el.setTaskId(jo.getLong(ApiDictionary.FO_API_FIELD_TS_TASK));
                }
                if (jo.isNull(ApiDictionary.FO_API_FIELD_LAST_UPDATE)){
                    el.setChanged(0);
                } else {
                    el.setChanged(jo.getLong(ApiDictionary.FO_API_FIELD_LAST_UPDATE)* ApiDictionary.FO_API_DATE_CONVERTOR);
                }
                if (jo.isNull(ApiDictionary.FO_API_FIELD_UPDATE_BY)){
                    el.setAuthor("");
                } else {
                    el.setAuthor(jo.getString(ApiDictionary.FO_API_FIELD_UPDATE_BY));
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

    public boolean delete(App app, Timeslot timeslot) {
        boolean res = false;

        if (timeslot.getId() > 0) {
            JSONObject jo = app.getWebService().executeAPI(ApiDictionary.FO_METHOD_DELETE_OBJ, timeslot.getId());
            try {
                res = (!jo.getString(ApiDictionary.FO_API_FIELD_RESULT).equalsIgnoreCase(FO_API_TRUE));
            } catch (Exception e) {
            }
        }

        return res;
    }
}
