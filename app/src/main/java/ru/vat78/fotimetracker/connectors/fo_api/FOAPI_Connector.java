package ru.vat78.fotimetracker.connectors.fo_api;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import ru.vat78.fotimetracker.connectors.WebStream;
import ru.vat78.fotimetracker.controllers.FOTT_Exceptions;

/**
 * Created by vat on 17.11.2015.
 *
 * Use for interact with FengOffice web-application
 * FengOffice web site - http://www.fengoffice.com/web/
 *
 * Requires special plug-in: vatAPI
 * Addition information see at: http://forum.fengoffice.com/index.php?topic=20827.0
 *
 */

public class FOAPI_Connector {

    private static FOAPI_Connector _instance = null;
    private final WebStream web;

    private final String urlOfServer;
    private final boolean onlyTrustedSSL;
    private final String token;

    private FOAPI_Connector(String url, String user, String password, boolean onlyTrustedSSL) throws FOAPI_Exceptions {

        web = WebStream.getInstance();
        this.onlyTrustedSSL = onlyTrustedSSL;
        this.urlOfServer = buildUrlWithCheck(url);
        if (this.onlyTrustedSSL) checkSSLCertificate();
        checkFengOfficeInstallation();
        this.token = checkFengOfficeCredentials(user, password);
        checkVatAPIPlugin();
    }

    public static synchronized FOAPI_Connector getInstance(String url, String user, String password, boolean onlyTrustedSSL) throws FOAPI_Exceptions {
        if (_instance == null)
            _instance = new FOAPI_Connector(url, user, password, onlyTrustedSSL);
        return _instance;
    }

    public boolean isNetworkAvailable(Context context) {
        return web.isNetworkAvailable(context);
    }

    //Execute API operation without arguments
    public JSONObject executeAPI(String method, String service) throws FOAPI_Exceptions {

        if (TextUtils.isEmpty(method) || TextUtils.isEmpty(service)) {
            return null;
        }
        if (TextUtils.isEmpty(this.token))
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.CREDENTIAL_ERROR, FOTT_Exceptions.ExeptionLevels.CRITICAL);

        checkVatAPIPlugin();

        String request = this.urlOfServer + FOAPI_Dictionary.FO_VAPI_REQUEST;
        request = request.replace(FOAPI_Dictionary.FO_API_METHOD, method);
        request = request.replace(FOAPI_Dictionary.FO_API_SERVICE, service);
        request = request.replace(FOAPI_Dictionary.FO_API_TOKEN, this.token);

        JSONObject jo = getJSONObjfromURL(request);
        return jo;
    }

    //Execute API operation with arguments
    public JSONObject executeAPI(String method, String service, HashMap<String, String> args) throws FOAPI_Exceptions {

        if (TextUtils.isEmpty(method) || TextUtils.isEmpty(service)) {
            return null;
        }
        if (TextUtils.isEmpty(this.token))
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.CREDENTIAL_ERROR, FOTT_Exceptions.ExeptionLevels.CRITICAL);

        checkVatAPIPlugin();

        String argStr = "{}";
        if (args.size() > 0) {
            JSONObject jsargs = new JSONObject();
            try {
                for (Map.Entry<String, String> entry : args.entrySet())
                    if (!entry.getKey().isEmpty())
                        jsargs.put(entry.getKey(), removeWrongSymbols(entry.getValue()));

                argStr = jsargs.toString();
                argStr = argStr.replaceAll("\"%5b", "%5b");
                argStr = argStr.replaceAll("%5d\"", "%5d");
            } catch (Exception e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
        }

        String request = this.urlOfServer + FOAPI_Dictionary.FO_VAPI_REQUEST;
        request = request.replace(FOAPI_Dictionary.FO_API_METHOD, method);
        request = request.replace(FOAPI_Dictionary.FO_API_SERVICE, service);
        request = request.replace(FOAPI_Dictionary.FO_API_ARGS, argStr);
        request = request.replace(FOAPI_Dictionary.FO_API_TOKEN, this.token);

        JSONObject jo = getJSONObjfromURL(request);
        return jo;
    }

    //Execute API operation for delete object and so on
    public JSONObject executeAPI(String method, long id) throws FOAPI_Exceptions {

        if (TextUtils.isEmpty(method)) {
            return null;
        }
        if (TextUtils.isEmpty(this.token))
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.CREDENTIAL_ERROR, FOTT_Exceptions.ExeptionLevels.CRITICAL);

        checkVatAPIPlugin();

        String request = this.urlOfServer + FOAPI_Dictionary.FO_VAPI_REQUEST_BY_ID;
        request = request.replace(FOAPI_Dictionary.FO_API_METHOD, method);
        request = request.replace(FOAPI_Dictionary.FO_API_OBJECT_ID, "" + id);
        request = request.replace(FOAPI_Dictionary.FO_API_ACTION, "" + 0);
        request = request.replace(FOAPI_Dictionary.FO_API_TOKEN, this.token);

        JSONObject jo = getJSONObjfromURL(request);
        return jo;
    }

    //Execute API operation for complet task and so on
    public JSONObject executeAPI(String method, long id, String action) throws FOAPI_Exceptions {

        if (TextUtils.isEmpty(method) || TextUtils.isEmpty(action)) {
            return null;
        }
        if (TextUtils.isEmpty(this.token))
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.CREDENTIAL_ERROR, FOTT_Exceptions.ExeptionLevels.CRITICAL);

        checkVatAPIPlugin();

        String request = this.urlOfServer + FOAPI_Dictionary.FO_VAPI_REQUEST_BY_ID;
        request = request.replace(FOAPI_Dictionary.FO_API_METHOD, method);
        request = request.replace(FOAPI_Dictionary.FO_API_OBJECT_ID, "" + id);
        request = request.replace(FOAPI_Dictionary.FO_API_ACTION, action);
        request = request.replace(FOAPI_Dictionary.FO_API_TOKEN, this.token);

        JSONObject jo = getJSONObjfromURL(request);
        return jo;
    }


    private String buildUrlWithCheck(String url) throws FOAPI_Exceptions {

        if (TextUtils.isEmpty(url))
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.WRONG_URL, FOTT_Exceptions.ExeptionLevels.CRITICAL);

        if (!url.endsWith("/")) {
            url += "/";
        }

        if (!url.substring(0, 4).toLowerCase().equals("http")) {
            url = determineProtocol(url);
        } else {
            try {
                web.getDataFromURL(url, false);
            } catch (IOException e) {
                throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.WRONG_URL, FOTT_Exceptions.ExeptionLevels.CRITICAL);
            }
        }

        return url;
    }

    private String determineProtocol(String url) throws FOAPI_Exceptions {

        String result = "http://";
        try {
            web.getDataFromURL(result + url, false);
        } catch (IOException e) {
            result = "";
        }

        if (result.isEmpty()) {
            result = "https://";
            try {
                web.getDataFromURL(result + url, false);
            } catch (IOException e) {
                throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.WRONG_URL, FOTT_Exceptions.ExeptionLevels.CRITICAL);
            }
        }

        return result;
    }

    private void checkSSLCertificate() throws FOAPI_Exceptions {
        try {
            web.getDataFromURL(urlOfServer, onlyTrustedSSL);
        } catch (IOException e) {
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.CERTIFICATE_ERROR, FOTT_Exceptions.ExeptionLevels.CRITICAL);
        }
    }

    private void checkFengOfficeInstallation() throws FOAPI_Exceptions {
        try {
            web.getDataFromURL(this.urlOfServer + FOAPI_Dictionary.FO_API_CHECK_FO, this.onlyTrustedSSL);
        } catch (IOException e) {
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.NO_FENGOFFICE, FOTT_Exceptions.ExeptionLevels.CRITICAL);
        }
    }

    private void checkVatAPIPlugin() throws FOAPI_Exceptions {

        if (this.token.isEmpty()) {
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.CREDENTIAL_ERROR, FOTT_Exceptions.ExeptionLevels.CRITICAL);
        }

        String request = this.urlOfServer + FOAPI_Dictionary.FO_API_CHECK_PLUGIN;
        request = request.replace(FOAPI_Dictionary.FO_API_PLUGIN, FOAPI_Dictionary.FO_PLUGIN_NAME);
        request = request.replace(FOAPI_Dictionary.FO_API_TOKEN, token);

        JSONObject jo = getJSONObjfromURL(request);
        if (jo == null) {
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.NO_VATAPI_PLUGIN, FOTT_Exceptions.ExeptionLevels.CRITICAL);
        }

        int pluginState;
        try {
            pluginState = jo.getInt(FOAPI_Dictionary.FO_API_FIELD_PLUGIN_STATE);
        } catch (Exception e) {
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.NO_VATAPI_PLUGIN, FOTT_Exceptions.ExeptionLevels.CRITICAL);
        }

        if (pluginState == 0)
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.VATAPI_PLUGIN_INACTIVE, FOTT_Exceptions.ExeptionLevels.CRITICAL);
    }

    private String checkFengOfficeCredentials(String user, String password) throws FOAPI_Exceptions {

        if (TextUtils.isEmpty(user)) {
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.CREDENTIAL_ERROR, FOTT_Exceptions.ExeptionLevels.CRITICAL);
        }

        String request = this.urlOfServer + FOAPI_Dictionary.FO_API_CONNECT;
        request = request.replace(FOAPI_Dictionary.FO_API_LOGIN, user);
        request = request.replace(FOAPI_Dictionary.FO_API_PASSWORD, password);

        JSONObject jo = getJSONObjfromURL(request);

        if (jo == null) {
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.CREDENTIAL_ERROR, FOTT_Exceptions.ExeptionLevels.CRITICAL);
        }

        String result;
        try {
            result = jo.getString(FOAPI_Dictionary.FO_API_FIELD_TOKEN);
        } catch (JSONException e) {
            throw new FOAPI_Exceptions(FOAPI_Exceptions.ECodes.JSON_OBJECT_MISMATCH, FOTT_Exceptions.ExeptionLevels.CRITICAL);
        }
        return result;

    }

    private static String removeWrongSymbols(String str) {
        String res = str.replace("%", "%25");
        res = res.replace(" ", "%20");
        res = res.replace("!", "%21");
        res = res.replace("\"", "%22");
        res = res.replace("#", "%23");
        res = res.replace("&", "%26");
        res = res.replace("'", "%27");
        res = res.replace("*", "%2a");
        res = res.replace("<", "%3c");
        res = res.replace("=", "%3d");
        res = res.replace(">", "%3e");
        res = res.replace("?", "%3f");
        res = res.replace("[", "%5b");
        res = res.replace("]", "%5d");
        res = res.replace("^", "%5e");
        res = res.replace("`", "%60");
        res = res.replace("{", "%7b");
        res = res.replace("|", "%7c");
        res = res.replace("}", "%7d");

        return res;
    }


    private JSONObject getJSONObjfromURL(String url) throws FOAPI_Exceptions {

        JSONObject jObj = null;
        String data;

        try {
            data = web.getDataFromURL(url, onlyTrustedSSL);
        } catch (IOException e) {
            throw new FOAPI_Exceptions(url, FOAPI_Exceptions.ECodes.API_REQUEST_ERROR, FOTT_Exceptions.ExeptionLevels.WARNING);
        }

        data = FindErrorInData(data);

        try {
            jObj = new JSONObject(data);

        } catch (JSONException e) {
            throw new FOAPI_Exceptions(url + "\n" + data, FOAPI_Exceptions.ECodes.API_WRONG_DATA, FOTT_Exceptions.ExeptionLevels.WARNING);
        }
        return jObj;
    }

    private JSONArray getJSONArrfromURL(String url) throws FOAPI_Exceptions {

        JSONArray jArr = null;
        JSONObject jObj = getJSONObjfromURL(url);

        try {
            jArr = jObj.getJSONArray("fo_obj");
        } catch (JSONException e) {
            throw new FOAPI_Exceptions(url + "\n" + jObj.toString(), FOAPI_Exceptions.ECodes.API_WRONG_DATA, FOTT_Exceptions.ExeptionLevels.WARNING);
        }

        return jArr;
    }

    private String FindErrorInData(String data) throws FOAPI_Exceptions {

        if (TextUtils.isEmpty(data)) {
            throw new FOAPI_Exceptions(data, FOAPI_Exceptions.ECodes.API_EMPTY_DATA, FOTT_Exceptions.ExeptionLevels.WARNING);
        }

        if (data.startsWith("Fatal error")) {
            throw new FOAPI_Exceptions(data, FOAPI_Exceptions.ECodes.API_FATAL_ERROR, FOTT_Exceptions.ExeptionLevels.WARNING);
        }

        if (data.startsWith("API Response")) {
            throw new FOAPI_Exceptions(data, FOAPI_Exceptions.ECodes.API_WRONG_DATA, FOTT_Exceptions.ExeptionLevels.WARNING);
        }

        if (data.startsWith("[")) {
            //Add global JSON object for array
            data = "{\"" + FOAPI_Dictionary.FO_API_MAIN_OBJ + "\":" + data + "}";
        }
        if (data.startsWith("false") || data.startsWith("true"))
            data = "{\"" + FOAPI_Dictionary.FO_API_FIELD_RESULT + "\":\"" + data + "\"}";

        if (!data.startsWith("{")) {
            throw new FOAPI_Exceptions(data, FOAPI_Exceptions.ECodes.API_WRONG_DATA, FOTT_Exceptions.ExeptionLevels.WARNING);
        }

        return data;
    }
}