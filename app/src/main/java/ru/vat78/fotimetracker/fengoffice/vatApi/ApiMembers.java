package ru.vat78.fotimetracker.fengoffice.vatApi;

import android.graphics.Color;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.vat78.fotimetracker.IErrorsHandler;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.model.ErrorsType;
import ru.vat78.fotimetracker.model.Member;

/**
 * Created by vat on 27.11.2015.
 */
class ApiMembers {
    private static final String CLASS_NAME = "ApiMembers";

    private ApiConnector connector;
    private IErrorsHandler errorsHandler;

    ApiMembers(ApiConnector connector, IErrorsHandler errorsHandler) {
        this.connector = connector;
        this.errorsHandler = errorsHandler;
    }

    ArrayList<Member> load(){
        JSONArray jo = connector.executeAPI(ApiDictionary.FO_METHOD_MEMBERS, ApiDictionary.FO_MEMBERS_WORKSPACE);
        return convertResults(jo);
    }

    private ArrayList<Member> convertResults(JSONArray data){

        if (data == null) {return null;}
        JSONObject jo;
        ArrayList<Member> res = new ArrayList<>();
        res.add(generateAnyMember());

        if (data == null) {return null;}
        for (int i = 0; i < data.length(); i++) {

            Member el = null;
            try {
                jo = data.getJSONObject(i);

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
            catch (JSONException e) {
                errorsHandler.error(CLASS_NAME, ErrorsType.JSON_PARSING_ERROR, e);
            }
            finally {
                if (el != null)
                    if (!res.add(el)) {break;}
            }
        }
        return res;
    }

    private Member generateAnyMember() {
        Member any = new Member(-1, "Any"); //ToDo: get name from resources getString(R.string.any_category));
        any.setPath("");
        any.setColorIndex(Color.TRANSPARENT);
        return any;
    }
}
