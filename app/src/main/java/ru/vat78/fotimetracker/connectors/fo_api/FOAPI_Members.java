package ru.vat78.fotimetracker.connectors.fo_api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import ru.vat78.fotimetracker.controllers.FOTT_Exceptions;
import ru.vat78.fotimetracker.connectors.FOTT_ObjectsConnector;
import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.model.FOTT_MemberBuilder;
import ru.vat78.fotimetracker.model.FOTT_Object;


public class FOAPI_Members implements FOTT_ObjectsConnector {

    private static final String CLASS_NAME = "FOAPI_Members. ";

    private final FOAPI_Connector webService;

    private static FOAPI_Members _instance = null;

    private FOAPI_Members(FOAPI_Connector webService) {
        this.webService = webService;
    }

    public static synchronized FOAPI_Members getInstance(FOAPI_Connector webService) {
        if (_instance == null)
            _instance = new FOAPI_Members(webService);
        return _instance;
    }

    @Override
    public ArrayList<FOTT_Member> loadObjects() throws FOAPI_Exceptions{

        JSONObject jo = webService.executeAPI(FOAPI_Dictionary.FO_METHOD_MEMBERS, FOAPI_Dictionary.FO_MEMBERS_WORKSPACE);
        //if (!webService.getError().isEmpty())
        //    webService.error_handler(FOTT_ErrorsHandler.ERROR_SAVE_ERROR, CLASS_NAME, webService.getError());
        return convertResults(jo);
    }

    @Override
    public ArrayList<FOTT_Member> loadFilteredObjects(String filter) throws FOAPI_Exceptions {
        return loadObjects();
    }

    @Override
    public ArrayList<FOTT_Member> loadChangedObjects(Date milestone) throws FOAPI_Exceptions {
        return loadObjects();
    }

    @Override
    public FOTT_Member loadObject(long objectId) throws FOAPI_Exceptions {
        return null;
    }

    @Override
    public boolean saveObjects(ArrayList<? extends FOTT_Object> savingObjects) throws FOAPI_Exceptions { return false; }

    @Override
    public long saveObject(FOTT_Object savingObject) throws FOAPI_Exceptions {
        return 0;
    }

    @Override
    public boolean saveChangedObjects(ArrayList<? extends FOTT_Object> savingObjects, Date milestone) throws FOAPI_Exceptions { return false; }

    @Override
    public boolean deleteObjects(ArrayList<? extends FOTT_Object> deletingObjects) throws FOAPI_Exceptions { return false; }

    @Override
    public boolean deleteObject(FOTT_Object deletingObject) throws FOAPI_Exceptions { return false; }



    private ArrayList<FOTT_Member> convertResults(JSONObject data) throws FOAPI_Exceptions{

        JSONArray list;
        ArrayList<FOTT_Member> result = new ArrayList<>();
        if (data == null) {return result;}

        try {
            list = data.getJSONArray(FOAPI_Dictionary.FO_API_MAIN_OBJ);
        } catch (JSONException e) {
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.JSON_ARRAY_MISMATCH, FOTT_Exceptions.ExeptionLevels.WARNING);
        }

        if (list != null) {
            for (int i = 0; i < list.length(); i++) {

                FOTT_MemberBuilder m = null;
                try {
                    m = readElement(list.getJSONObject(i));
                } catch (JSONException ignored) {  }

                if (m != null)
                    result.add(m.buildObject());
            }
        }
        return result;
    }

    private FOTT_MemberBuilder readElement(JSONObject element) {

        FOTT_MemberBuilder result = null;
        FOAPI_JSONHandler h = new FOAPI_JSONHandler(element);

        long id = h.getLong(FOAPI_Dictionary.FO_API_FIELD_ID, 0);
        String s = h.getString(FOAPI_Dictionary.FO_API_FIELD_NAME, "");

        if (id != 0 && !s.isEmpty()) {
            result = new FOTT_MemberBuilder();
            result.setWebID(id);
            result.setName(s);

            String path = getMemberPathFromJSON(h, s);
            result.setPath(path);
            result.setColor(getMemberColorFromJSON(h));
            result.setLevel(getLevelFromPath(path));
        }
        return result;
    }

    private String getMemberPathFromJSON(FOAPI_JSONHandler element, String memberName) {

        String path = element.getString(FOAPI_Dictionary.FO_API_FIELD_PATH,"");

        //Add current name to path for make better sort order
        if (path.isEmpty()) {
            path = memberName;
        } else {
            path = path + FOAPI_Dictionary.FO_API_MEMBER_SPLITTER + memberName;
        }
        return path;
    }

    private int getMemberColorFromJSON(FOAPI_JSONHandler element) {

        int color = element.getInt(FOAPI_Dictionary.FO_API_FIELD_TYPE, 0);
        if (color < FOAPI_Dictionary.memColors.length)color = FOAPI_Dictionary.memColors[color];

        return color;
    }

    private int getLevelFromPath(String path){
        int result = 0;
        int i = 0;
        while (i >= 0) {
            i = path.indexOf(FOAPI_Dictionary.FO_API_MEMBER_SPLITTER,i);
            result++;
        }
        return result;
    }

}
