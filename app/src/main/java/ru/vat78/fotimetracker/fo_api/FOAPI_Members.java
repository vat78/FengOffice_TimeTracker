package ru.vat78.fotimetracker.fo_api;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.vat78.fotimetracker.database.FOTT_DBContract;

/**
 * Created by vat on 27.11.2015.
 */
public class FOAPI_Members {

    public static ArrayList<ContentValues> load(FOAPI_Connector FOApp){

        JSONObject jo = FOApp.executeAPI(FOAPI_Dictionary.FO_METHOD_MEMBERS,FOAPI_Dictionary.FO_MEMBERS_WORKSPACE);
        return convertResults(jo);
    }

    private static ArrayList<ContentValues> convertResults(JSONObject data){

        if (data == null) {return null;}
        JSONArray list = null;
        JSONObject jo;
        ArrayList<ContentValues> res = new ArrayList<>();
        try {
            list = data.getJSONArray(FOAPI_Dictionary.FO_API_MAIN_OBJ);
        } catch (Exception e) {
            Log.e("FOTT",e.getMessage());
        }

        if (list == null) {return null;}
        for (int i = 0; i < list.length(); i++) {
            try {
                ContentValues el = new ContentValues();
                jo = list.getJSONObject(i);

                el.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_MEMBER_ID, jo.getInt(FOAPI_Dictionary.FO_API_FIELD_ID));
                String name = jo.getString(FOAPI_Dictionary.FO_API_FIELD_NAME);
                el.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_NAME, name);

                String path = jo.getString(FOAPI_Dictionary.FO_API_FIELD_PATH);

                //Add current name to path for make better sort order in adapter
                if (path.isEmpty()) {
                    path = name;
                } else {
                    path = path + "/" + name;
                }
                String mpath[] = path.split("/");
                el.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_PATH, path);
                el.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_LEVEL, mpath.length);
                el.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_TYPE, jo.getString(FOAPI_Dictionary.FO_API_FIELD_TYPE));
                el.put(FOTT_DBContract.FOTT_DBMembers.COLUMN_NAME_COLOR, jo.getString(FOAPI_Dictionary.FO_API_FIELD_COLOR));

                if (!res.add(el)) {
                    break;
                }

            } catch (Exception e) {
                Log.e("FOTT", e.getMessage());
            }
        }
        return res;
    }
}
