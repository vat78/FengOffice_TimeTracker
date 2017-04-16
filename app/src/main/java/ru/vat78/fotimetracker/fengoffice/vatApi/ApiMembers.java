package ru.vat78.fotimetracker.fengoffice.vatApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.model.Member;
import ru.vat78.fotimetracker.views.ErrorsHandler;

/**
 * Created by vat on 27.11.2015.
 */
public class ApiMembers {
    private static final String CLASS_NAME = "ApiMembers";

    public ArrayList<Member> load(App app){

        JSONObject jo = app.getWebService().executeAPI(ApiDictionary.FO_METHOD_MEMBERS, ApiDictionary.FO_MEMBERS_WORKSPACE);
        return convertResults(app,jo);
    }

    private ArrayList<Member> convertResults(App app, JSONObject data){

        if (data == null) {return null;}
        JSONArray list = null;
        JSONObject jo;
        ArrayList<Member> res = new ArrayList<>();
        try {
            list = data.getJSONArray(ApiDictionary.FO_API_MAIN_OBJ);
        } catch (Exception e) {
            app.getError().error_handler(ErrorsHandler.ERROR_SAVE_ERROR, CLASS_NAME, e.getMessage());
        }

        if (list == null) {return null;}
        for (int i = 0; i < list.length(); i++) {

            Member el = null;
            try {
                jo = list.getJSONObject(i);

                long id = jo.getLong(ApiDictionary.FO_API_FIELD_ID);
                String s = jo.getString(ApiDictionary.FO_API_FIELD_NAME);
                el = new Member(id,s);

                String path = s;
                if (!jo.isNull(ApiDictionary.FO_API_FIELD_PATH)) {

                    path = jo.getString(ApiDictionary.FO_API_FIELD_PATH);
                    //Add current name to path for make better sort order in adapter
                    if (path.isEmpty()) {
                        path = s;
                    } else {
                        path = path + el.getMemberSplitter() + s;
                    }
                }
                el.setPath(path);

                if (jo.isNull(ApiDictionary.FO_API_FIELD_TYPE)) {
                    el.setColorIndex(0);
                } else {
                    el.setColorIndex(jo.getInt(ApiDictionary.FO_API_FIELD_COLOR));
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
}
