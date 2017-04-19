package ru.vat78.fotimetracker.fengoffice.vatApi;


import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ru.vat78.fotimetracker.App;
import ru.vat78.fotimetracker.fengoffice.HttpJsonClient;
import ru.vat78.fotimetracker.fengoffice.HttpJsonError;

import java.util.Properties;

/**
 * Created by vat on 17.11.2015.
 *
 * Use for interact with FengOffice web-application
 * TODO error handler
 */

public class ApiConnector {

    private App app;
    private int ErrorCode=0;
    private String ErrorMsg="";
    private HttpJsonClient jsonClient;

    private String FO_User;
    private static String FO_Pwd;
    private static String FO_URL;
    private String FO_Token;

    public ApiConnector(App application){
        app = application;
        FO_Token = "";
        FO_Pwd = "";
        jsonClient = new HttpJsonClient();
    }

    private boolean UseUntrustCA = false;

    public boolean setFO_Url(String foUrl) {
        foUrl = foUrl.trim();
        if (foUrl.length() <3){ return false;}

        if (!foUrl.startsWith("http://") && !foUrl.startsWith("https://")) {
            foUrl = "https://" + foUrl;
        }

        if (!foUrl.endsWith("/")) {foUrl += "/";}
        this.FO_URL = foUrl;
        return true;
    }

    public String getFO_Url() {
        return FO_URL;
    }

    public boolean setFO_User(String FO_User) {
        this.FO_User = FO_User;
        return true;
    }

    public String getFO_User() {
        return this.FO_User;
    }

    public boolean setFO_Pwd(String FO_Pwd) {
        this.FO_Pwd = FO_Pwd;
        return true;
    }

    public String getFO_Pwd() {
        return this.FO_Pwd;
    }

    public void canUseUntrustCert(boolean flag) {
        this.UseUntrustCA = flag;
    }

    public String getError() {
        return this.ErrorMsg;
    }

    public void setError(String error) {
        this.ErrorMsg = error;
    }

    //This function try to login FengOffice web-application
    public boolean testConnection() {
        if (TextUtils.isEmpty(this.FO_URL) || TextUtils.isEmpty(this.FO_User)) {
            return false;
        }

        this.FO_Token = "";
        resetError();

        //Try to login
        String request = this.FO_URL + ApiDictionary.FO_API_CONNECT;
        Properties requestParams = new Properties();
        requestParams.setProperty(ApiDictionary.FO_API_LOGIN,this.FO_User);
        requestParams.setProperty(ApiDictionary.FO_API_PASSWORD,this.FO_Pwd);

        // Download JSON data from URL
        JSONObject jo = null;
        try {
            jo = jsonClient.getJsonObject(request, requestParams, this.UseUntrustCA);
            this.FO_Token = jo.getString(ApiDictionary.FO_API_FIELD_TOKEN);
        } catch (HttpJsonError | JSONException e) {
            this.ErrorMsg = e.getMessage();
        }
        return (!this.FO_Token.isEmpty());
    }

    //Checks plugin status
    public boolean checkPlugin(String plugin_name){

        if (this.FO_Token.isEmpty() || plugin_name.isEmpty()) {return false;}
        Integer res = 0;
        resetError();

        String request = this.FO_URL + ApiDictionary.FO_API_CHECK_PLUGIN;
        Properties requestParams = new Properties();
        requestParams.setProperty(ApiDictionary.FO_API_PLUGIN,plugin_name);
        requestParams.setProperty(ApiDictionary.FO_API_TOKEN,FO_Token);

        JSONObject jo = null;
        try {
            jo = jsonClient.getJsonObject(request, requestParams, this.UseUntrustCA);
            res = jo.getInt(ApiDictionary.FO_API_FIELD_PLUGIN_STATE);
        } catch (HttpJsonError | JSONException e) {
            this.ErrorMsg = e.getMessage();
        }
        return (res > 0);
    }


    //Execute API operation without arguments
    public JSONObject executeAPI(String method, String service){
        if (method.isEmpty()) {return null;}
        if (this.FO_Token.isEmpty())
            if (!testConnection()) {return null;}

        resetError();
        if (!checkPlugin(ApiDictionary.FO_PLUGIN_NAME)) {return null;}
        resetError();
        String request = this.FO_URL + ApiDictionary.FO_VAPI_REQUEST;
        Properties requestParams = new Properties();
        requestParams.setProperty(ApiDictionary.FO_API_TOKEN,FO_Token);
        requestParams.setProperty(ApiDictionary.FO_API_METHOD,method);
        requestParams.setProperty(ApiDictionary.FO_API_SERVICE,service);

        JSONObject jo = null;
        try {
            jo = jsonClient.getJsonObject(request, requestParams, this.UseUntrustCA);
        } catch (HttpJsonError e) {
            this.ErrorMsg = e.getMessage();
        }

        return jo;
    }

    //Execute API operation with arguments
    public JSONObject executeAPI(String method, String service, String args[]){
        if (method.isEmpty()) {return null;}
        if (this.FO_Token.isEmpty())
            if (!testConnection()) {return null;}

        resetError();

        JSONObject jo = null;

        if (!checkPlugin(ApiDictionary.FO_PLUGIN_NAME)) {return null;}
        resetError();
        String argStr = "{}";
        if (args.length > 0){
            JSONObject jsargs = new JSONObject();
            try {
                for (int i = 1; i < args.length; i += 2) {
                    if (args[i] != null) jsargs.put(args[i-1], args[i]);
                }
                argStr = jsargs.toString();
                argStr = argStr.replaceAll("\"%5b","%5b");
                argStr = argStr.replaceAll("%5d\"","%5d");
            }
            catch (Exception e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
        }
        String request = this.FO_URL + ApiDictionary.FO_VAPI_REQUEST;
        Properties requestParams = new Properties();
        requestParams.setProperty(ApiDictionary.FO_API_TOKEN,FO_Token);
        requestParams.setProperty(ApiDictionary.FO_API_METHOD,method);
        requestParams.setProperty(ApiDictionary.FO_API_SERVICE,service);
        requestParams.setProperty(ApiDictionary.FO_API_ARGS,argStr);

        try{
            jo = jsonClient.getJsonObject(request, requestParams, this.UseUntrustCA);
        } catch (HttpJsonError e) {
            this.ErrorMsg = e.getMessage();
        }

        return jo;
    }

    //Execute API operation for delete object and so on
    public JSONObject executeAPI(String method, long id) {
        if (method.isEmpty()) {return null;}
        if (this.FO_Token.isEmpty())
            if (!testConnection()) {return null;}

        resetError();

        JSONObject jo = null;

        if (!checkPlugin(ApiDictionary.FO_PLUGIN_NAME)) {return null;}
        resetError();

        String request = this.FO_URL + ApiDictionary.FO_VAPI_REQUEST_BY_ID;
        Properties requestParams = new Properties();
        requestParams.setProperty(ApiDictionary.FO_API_TOKEN,FO_Token);
        requestParams.setProperty(ApiDictionary.FO_API_METHOD,method);
        requestParams.setProperty(ApiDictionary.FO_API_OBJECT_ID,"" + id);
        requestParams.setProperty(ApiDictionary.FO_API_ACTION,"" + 0);

        try {
            jo = jsonClient.getJsonObject(request, requestParams, this.UseUntrustCA);
        } catch (HttpJsonError e) {
            this.ErrorMsg = e.getMessage();
        }

        return jo;
    }
    
    //Execute API operation for complet task and so on
    public JSONObject executeAPI(String method, long id, String action) {
        if (method.isEmpty()) {return null;}
        if (this.FO_Token.isEmpty())
            if (!testConnection()) {return null;}

        resetError();

        JSONObject jo;

        if (!checkPlugin(FOAPI_Dictionary.FO_PLUGIN_NAME)) {return null;}
        resetError();

        String request = this.FO_URL + FOAPI_Dictionary.FO_VAPI_REQUEST_BY_ID;
        Properties requestParams = new Properties();
        requestParams.setProperty(ApiDictionary.FO_API_TOKEN,FO_Token);
        requestParams.setProperty(ApiDictionary.FO_API_METHOD,method);
        requestParams.setProperty(ApiDictionary.FO_API_OBJECT_ID,"" + id);
        requestParams.setProperty(ApiDictionary.FO_API_ACTION,action);

        try {
            jo = jsonClient.getJsonObject(request, requestParams, this.UseUntrustCA);
        } catch (HttpJsonError e) {
            this.ErrorMsg = e.getMessage();
        }

        return jo;
    }

    private void resetError(){
        this.ErrorCode = 0;
        this.ErrorMsg = "";
    }
}
