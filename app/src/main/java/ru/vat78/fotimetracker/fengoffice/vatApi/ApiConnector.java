package ru.vat78.fotimetracker.fengoffice.vatApi;


import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import ru.vat78.fotimetracker.IErrorsHandler;
import ru.vat78.fotimetracker.fengoffice.HttpJsonClient;
import ru.vat78.fotimetracker.model.ErrorsType;

import java.util.Properties;

/**
 * Created by vat on 17.11.2015.
 *
 * Use for interact with FengOffice web-application
 */

public class ApiConnector {
    private static final String CLASS_NAME = "ApiConnector";

    private IErrorsHandler errorsHandler;
    private HttpJsonClient jsonClient;

    private String login;
    private String password;
    private String url;
    private String securityToken;

    private boolean useUntrustCA = false;

    public ApiConnector(IErrorsHandler errorsHandler){
        this.errorsHandler = errorsHandler;
        securityToken = "";
        password = "";
        jsonClient = new HttpJsonClient(errorsHandler);
    }

    public boolean setUrl(String url) {
        url = url.trim();
        if (url.length() <3){ return false;}

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        if (!url.endsWith("/")) {url += "/";}
        this.url = url;
        return true;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String Password) {
        this.password = Password;
    }

    public String getPassword() {
        return this.password;
    }

    public void canUseUntrustCert(boolean flag) {
        this.useUntrustCA = flag;
    }

    //This function try to login FengOffice web-application
    public boolean testConnection() {

        if (TextUtils.isEmpty(this.url) || TextUtils.isEmpty(this.login)) {
            errorsHandler.error(CLASS_NAME, ErrorsType.WRONG_URL);
            return false;
        }
        this.securityToken = "";

        //Try to login
        String request = this.url + ApiDictionary.FO_API_CONNECT;
        Properties requestParams = new Properties();
        requestParams.setProperty(ApiDictionary.FO_API_LOGIN,this.login);
        requestParams.setProperty(ApiDictionary.FO_API_PASSWORD,this.password);

        // Download JSON data from URL
        JSONObject jo;
        try {
            jo = jsonClient.getJsonObject(request, requestParams, this.useUntrustCA);
            this.securityToken = jo.getString(ApiDictionary.FO_API_FIELD_TOKEN);
        } catch (JSONException e) {
            errorsHandler.error(CLASS_NAME, ErrorsType.TEST_CONNECTION_ERROR, e);
        }
        return (!this.securityToken.isEmpty());
    }

    //Checks plugin status
    public boolean checkPlugin(String plugin_name){

        if (this.securityToken.isEmpty() || plugin_name.isEmpty()) {return false;}
        Integer res = 0;

        String request = this.url + ApiDictionary.FO_API_CHECK_PLUGIN;
        Properties requestParams = new Properties();
        requestParams.setProperty(ApiDictionary.FO_API_PLUGIN,plugin_name);
        requestParams.setProperty(ApiDictionary.FO_API_TOKEN, securityToken);

        JSONObject jo;
        try {
            jo = jsonClient.getJsonObject(request, requestParams, this.useUntrustCA);
            res = jo.getInt(ApiDictionary.FO_API_FIELD_PLUGIN_STATE);
        } catch (JSONException e) {
            errorsHandler.error(CLASS_NAME, ErrorsType.JSON_PARSING_ERROR, e);
        }
        return (res > 0);
    }


    //Execute API operation without arguments
    public JSONObject executeAPI(String method, String service){
        if (method.isEmpty()) {return null;}
        if (this.securityToken.isEmpty())
            if (!testConnection()) {return null;}

        if (!checkPlugin(ApiDictionary.FO_PLUGIN_NAME)) {return null;}

        String request = this.url + ApiDictionary.FO_VAPI_REQUEST;
        Properties requestParams = new Properties();
        requestParams.setProperty(ApiDictionary.FO_API_TOKEN, securityToken);
        requestParams.setProperty(ApiDictionary.FO_API_METHOD,method);
        requestParams.setProperty(ApiDictionary.FO_API_SERVICE,service);

        return jsonClient.getJsonObject(request, requestParams, this.useUntrustCA);
    }

    //Execute API operation with arguments
    public JSONObject executeAPI(String method, String service, String args[]){
        if (method.isEmpty()) {return null;}
        if (this.securityToken.isEmpty())
            if (!testConnection()) {return null;}

        if (!checkPlugin(ApiDictionary.FO_PLUGIN_NAME)) {return null;}

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
            catch (JSONException e) {
                errorsHandler.error(CLASS_NAME, ErrorsType.JSON_PARSING_ERROR, e);
            }
        }
        String request = this.url + ApiDictionary.FO_VAPI_REQUEST;
        Properties requestParams = new Properties();
        requestParams.setProperty(ApiDictionary.FO_API_TOKEN, securityToken);
        requestParams.setProperty(ApiDictionary.FO_API_METHOD,method);
        requestParams.setProperty(ApiDictionary.FO_API_SERVICE,service);
        requestParams.setProperty(ApiDictionary.FO_API_ARGS,argStr);

        return jsonClient.getJsonObject(request, requestParams, this.useUntrustCA);

    }

    //Execute API operation for delete object and so on
    public JSONObject executeAPI(String method, long id) {
        if (method.isEmpty()) {return null;}
        if (this.securityToken.isEmpty())
            if (!testConnection()) {return null;}

        if (!checkPlugin(ApiDictionary.FO_PLUGIN_NAME)) {return null;}

        String request = this.url + ApiDictionary.FO_VAPI_REQUEST_BY_ID;
        Properties requestParams = new Properties();
        requestParams.setProperty(ApiDictionary.FO_API_TOKEN, securityToken);
        requestParams.setProperty(ApiDictionary.FO_API_METHOD,method);
        requestParams.setProperty(ApiDictionary.FO_API_OBJECT_ID,"" + id);
        requestParams.setProperty(ApiDictionary.FO_API_ACTION,"" + 0);

        return jsonClient.getJsonObject(request, requestParams, this.useUntrustCA);
    }
    
    //Execute API operation for complet task and so on
    public JSONObject executeAPI(String method, long id, String action) {
        if (method.isEmpty()) {return null;}
        if (this.securityToken.isEmpty())
            if (!testConnection()) {return null;}

        if (!checkPlugin(ApiDictionary.FO_PLUGIN_NAME)) {return null;}

        String request = this.url + ApiDictionary.FO_VAPI_REQUEST_BY_ID;
        Properties requestParams = new Properties();
        requestParams.setProperty(ApiDictionary.FO_API_TOKEN, securityToken);
        requestParams.setProperty(ApiDictionary.FO_API_METHOD,method);
        requestParams.setProperty(ApiDictionary.FO_API_OBJECT_ID,"" + id);
        requestParams.setProperty(ApiDictionary.FO_API_ACTION,action);

        return jsonClient.getJsonObject(request, requestParams, this.useUntrustCA);
    }
}
