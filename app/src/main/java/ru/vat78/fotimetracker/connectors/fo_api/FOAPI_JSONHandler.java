package ru.vat78.fotimetracker.connectors.fo_api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FOAPI_JSONHandler {

    private final JSONObject object;

    public FOAPI_JSONHandler(JSONObject object) {
        this.object = object;
    }

    public String getString(String field, String defaultValue) {
        String result = defaultValue;
        try {
            if (!object.isNull(field))
                result = object.getString(field);
        } catch (JSONException e) {}
        return result;
    }

    public int  getInt(String field, int defaultValue) {
        int result = defaultValue;
        try {
            if (!object.isNull(field))
                result = object.getInt(field);
        } catch (JSONException e) {}
        return result;
    }

    public long getLong(String field, long defaultValue) {
        long result = defaultValue;
        try {
            if (!object.isNull(field))
                result = object.getLong(field);
        } catch (JSONException e) {}
        return result;
    }

    public long getDateTime(String field, long defaultValue) {
        long result = defaultValue;

        String tmp = this.getString(field, FOAPI_Dictionary.FO_API_FALSE);
        if (!tmp.equalsIgnoreCase(FOAPI_Dictionary.FO_API_FALSE)) {
            result = this.getLong(field, defaultValue) * FOAPI_Dictionary.FO_API_DATE_CONVERTOR;
        }
        return result;
    }

    public boolean getBoolean(String field, boolean defaultValue) {

        String tmp = (defaultValue ? FOAPI_Dictionary.FO_API_TRUE : "");
        return (this.getString(field,tmp).equalsIgnoreCase(FOAPI_Dictionary.FO_API_TRUE));
    }

    public String[] getArray(String field) {

        String[] result = null;
        try {
            if (!object.isNull(field)) {
                JSONArray ja = object.getJSONArray(field);

                result = new String[ja.length()];
                for (int i=0; i<ja.length(); i++) {
                    result[i] = ja.getString(i);
                }
            }
        } catch (JSONException e) {}

        return result;
    }
}
