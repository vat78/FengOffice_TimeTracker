package ru.vat78.fotimetracker.fo_api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.views.FOTT_ErrorsHandler;

/**
 * Created by vat on 27.11.2015.
 */
public class FOAPI_Members {
    private static final String CLASS_NAME = "FOAPI_Members";

    public static ArrayList<FOTT_Member> load(FOTT_App app){

        JSONObject jo = app.getWeb_service().executeAPI(FOAPI_Dictionary.FO_METHOD_MEMBERS, FOAPI_Dictionary.FO_MEMBERS_WORKSPACE);
        return convertResults(app,jo);
    }

    private static ArrayList<FOTT_Member> convertResults(FOTT_App app, JSONObject data){

        if (data == null) {return null;}
        JSONArray list = null;
        JSONObject jo;
        ArrayList<FOTT_Member> res = new ArrayList<>();
        try {
            list = data.getJSONArray(FOAPI_Dictionary.FO_API_MAIN_OBJ);
        } catch (Exception e) {
            app.getError().error_handler(FOTT_ErrorsHandler.ERROR_SAVE_ERROR, CLASS_NAME, e.getMessage());
        }

        if (list == null) {return null;}
        for (int i = 0; i < list.length(); i++) {

            FOTT_Member el = null;
            try {
                jo = list.getJSONObject(i);

                long id = jo.getLong(FOAPI_Dictionary.FO_API_FIELD_ID);
                String s = jo.getString(FOAPI_Dictionary.FO_API_FIELD_NAME);
                el = new FOTT_Member(id,s);

                String path = s;
                if (!jo.isNull(FOAPI_Dictionary.FO_API_FIELD_PATH)) {

                    path = jo.getString(FOAPI_Dictionary.FO_API_FIELD_PATH);
                    //Add current name to path for make better sort order in adapter
                    if (path.isEmpty()) {
                        path = s;
                    } else {
                        path = path + el.getMemberSplitter() + s;
                    }
                }
                el.setPath(path);

                if (jo.isNull(FOAPI_Dictionary.FO_API_FIELD_TYPE)) {
                    el.setColorIndex(0);
                } else {
                    el.setColorIndex(jo.getInt(FOAPI_Dictionary.FO_API_FIELD_COLOR));
                }
            }
            catch (Exception e) {
                app.getError().error_handler(FOTT_ErrorsHandler.ERROR_LOG_MESSAGE,CLASS_NAME, e.getMessage());
            }
            finally {
                if (el != null)
                    if (!res.add(el)) {break;}
            }
        }
        return res;
    }
}
