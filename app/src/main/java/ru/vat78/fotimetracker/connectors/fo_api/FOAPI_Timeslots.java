package ru.vat78.fotimetracker.connectors.fo_api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import ru.vat78.fotimetracker.connectors.FOTT_ObjectsConnector;
import ru.vat78.fotimetracker.model.FOTT_Object;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;
import ru.vat78.fotimetracker.model.FOTT_TimeslotBuilder;

import static ru.vat78.fotimetracker.connectors.fo_api.FOAPI_Dictionary.*;

public class FOAPI_Timeslots implements FOTT_ObjectsConnector {

    private static final String CLASS_NAME = "FOAPI_Timeslots";

    private final FOAPI_Connector webService;

    private static FOAPI_Timeslots _instance = null;

    private FOAPI_Timeslots(FOAPI_Connector webService) {
        this.webService = webService;
    }

    public static synchronized FOAPI_Timeslots getInstance(FOAPI_Connector webService) {
        if (_instance == null)
            _instance = new FOAPI_Timeslots(webService);
        return _instance;
    }

    @Override
    public ArrayList<FOTT_Timeslot> loadObjects() throws FOAPI_Exceptions {
        JSONObject jo = webService.executeAPI(FO_METHOD_LISTING, FO_SERVICE_TIMESLOTS);
        return convertResults(jo);
    }

    @Override
    public ArrayList<FOTT_Timeslot> loadFilteredObjects(String filter) throws FOAPI_Exceptions {
        return loadObjects();
    }

    @Override
    public ArrayList<FOTT_Timeslot> loadChangedObjects(Date milestone) throws FOAPI_Exceptions {

        HashMap<String, String> args = new HashMap<>();
        long l = (long) milestone.getTime() / FO_API_DATE_CONVERTOR;
        args.put(FO_API_ARG_LASTUPDATE, "" + l);

        JSONObject jo = webService.executeAPI(FO_METHOD_LISTING, FO_SERVICE_TIMESLOTS, args);
        return convertResults(jo);
    }

    @Override
    public FOTT_Timeslot loadObject(long objectId) throws FOAPI_Exceptions {
        return null;
    }

    @Override
    public boolean saveObjects(ArrayList<? extends FOTT_Object> savingObjects) throws FOAPI_Exceptions { return false; }

    @Override
    public long saveObject(FOTT_Object savingObject) throws FOAPI_Exceptions {
        long result = 0;
        FOTT_Timeslot timeslot = (FOTT_Timeslot) savingObject;

        if (timeslot != null) {
            HashMap<String, String> args = convertTSForAPI(timeslot);
            JSONObject jo = webService.executeAPI(FO_METHOD_SAVE_OBJ, FO_SERVICE_TIMESLOTS, args);
            if (jo != null) {
                try {
                    result = jo.getLong(FO_API_FIELD_ID);
                } catch (JSONException ignored) { }
            }
        }
        return result;
    }

    @Override
    public boolean saveChangedObjects(ArrayList<? extends FOTT_Object> savingObjects, Date milestone) throws FOAPI_Exceptions { return false; }

    @Override
    public boolean deleteObjects(ArrayList<? extends FOTT_Object> deletingObjects) throws FOAPI_Exceptions { return false; }

    @Override
    public boolean deleteObject(FOTT_Object deletingObject) throws FOAPI_Exceptions {

        boolean success = false;
        FOTT_Timeslot timeslot = (FOTT_Timeslot) deletingObject;

        if (timeslot.getWebId() > 0) {
            JSONObject jo = webService.executeAPI(FOAPI_Dictionary.FO_METHOD_DELETE_OBJ, timeslot.getWebId());

            try {
                success = (jo.getString(FOAPI_Dictionary.FO_API_FIELD_RESULT).startsWith(FOAPI_Dictionary.FO_API_TRUE));
            } catch (Exception e){
                success = false;
            }
        }
        return success;
    }


    private HashMap<String, String> convertTSForAPI(FOTT_Timeslot timeslot) {

        HashMap<String, String> args = new HashMap<>();
        long l;

        if (timeslot.getWebId() !=0 ) args.put(FO_API_FIELD_ID, "" + timeslot.getWebId());
        args.put(FO_API_FIELD_DESC, timeslot.getDesc());

        l = (long) timeslot.getStart().getTime() / FO_API_DATE_CONVERTOR;
        args.put(FO_API_FIELD_TS_DATE, "" + l);

        l = (long) timeslot.getDuration() / FO_API_DATE_CONVERTOR;
        args.put(FO_API_FIELD_TS_DURATION, "" + l);

        if (timeslot.getTaskId() == 0) {
            if (timeslot.getMembersWebIds().length != 0) {
                String s = "[";
                for (String member : timeslot.getMembersWebIds()) s += "\"" + member + "\",";
                s = s.substring(0,s.length()-1) + "]";
                args.put(FO_API_FIELD_MEMBERS, s);
            }
        } else {
            args.put(FO_API_FIELD_TS_TASK, "" + timeslot.getTaskId());
        }
        return args;
    }

    private ArrayList<FOTT_Timeslot> convertResults(JSONObject data) throws FOAPI_Exceptions{

        JSONArray list = null;
        ArrayList<FOTT_Timeslot> result = new ArrayList<>();
        if (data == null) {return result;}

        try {
            list = data.getJSONArray(FO_API_MAIN_OBJ);
        } catch (JSONException e) {
            throw new FOAPI_Exceptions(CLASS_NAME + "/n" + data, FOAPI_Exceptions.ECodes.JSON_ARRAY_MISMATCH, FOAPI_Exceptions.ExeptionLevels.WARNING);
        }

        if (list != null) {

            for (int i = 0; i < list.length(); i++) {
                FOTT_TimeslotBuilder ts = null;
                try {
                    ts = readElement(list.getJSONObject(i));
                } catch (JSONException ignored) { }

                if (ts != null)
                    result.add(ts.buildObject());
            }
        }

        return result;
    }

    private FOTT_TimeslotBuilder readElement(JSONObject jsonObject) {

        FOTT_TimeslotBuilder element = null;
        FOAPI_JSONHandler h = new FOAPI_JSONHandler(jsonObject);

        long id = h.getLong(FOAPI_Dictionary.FO_API_FIELD_ID, 0);
        String s = h.getString(FOAPI_Dictionary.FO_API_FIELD_TS_DESC, "");

        if (id != 0) {
            element = new FOTT_TimeslotBuilder();
            element.setWebID(id);
            element.setName(s);
            element.setDesc(s);

            element.setMembersWebIds(h.getArray(FOAPI_Dictionary.FO_API_FIELD_MEMPATH));
            element.setStart(h.getDateTime(FO_API_FIELD_TS_DATE, 0));
            element.setDuration(h.getLong(FO_API_FIELD_TS_DURATION, FO_API_DATE_CONVERTOR));
            element.setTaskWebId(h.getLong(FO_API_FIELD_TS_TASK, 0));
            element.setChanged(h.getDateTime(FO_API_FIELD_LAST_UPDATE, 0));
            element.setAuthor(h.getString(FO_API_FIELD_UPDATE_BY,""));
        }

        return element;
    }
}
