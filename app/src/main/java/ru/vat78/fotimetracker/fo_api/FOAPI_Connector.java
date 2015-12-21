package ru.vat78.fotimetracker.fo_api;


import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.model.FOTT_Task;
import ru.vat78.fotimetracker.model.FOTT_Timeslot;

/**
 * Created by vat on 17.11.2015.
 *
 * Use for interact with FengOffice web-application
 * TODO error handler
 */

public class FOAPI_Connector {

    private FOTT_App app;
    private int ErrorCode=0;
    private String ErrorMsg="";

    private String FO_User;
    private static String FO_Pwd;
    private static String FO_URL;
    private String FO_Token;

    public FOAPI_Connector(FOTT_App application){
        app = application;
    }

    private boolean UseUntrustCA = false;

    public boolean setFO_Url(String foUrl) {
        foUrl = foUrl.trim();
        if (foUrl.length() <1){ return false;}

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

    //This function try to login FengOffice web-application
    public boolean testConnection() {
        if (TextUtils.isEmpty(this.FO_URL) || TextUtils.isEmpty(this.FO_User)) {
            return false;
        }

        this.FO_Token = "";
        resetError();

        //Try to login
        String request = this.FO_URL + FOAPI_Dictionary.FO_API_CONNECT;
        request = request.replace(FOAPI_Dictionary.FO_API_LOGIN,this.FO_User);
        request = request.replace(FOAPI_Dictionary.FO_API_PASSWORD,this.FO_Pwd);

        // Download JSON data from URL
        JSONObject jo = null;
        jo = JSONfunctions.getJSONObjfromURL(request, this.UseUntrustCA, this.ErrorMsg);

        if (jo == null) {return false;}

        try {
            this.FO_Token = jo.getString(FOAPI_Dictionary.FO_API_FIELD_TOKEN);
        } catch (Exception e) {
            this.ErrorMsg = e.getMessage();
        }
        return (!this.FO_Token.isEmpty());
    }

    //Checks plugin status
    public boolean checkPlugin(String plugin_name){

        if (this.FO_Token.isEmpty() || plugin_name.isEmpty()) {return false;}
        Integer res = 0;
        resetError();

        String request = this.FO_URL + FOAPI_Dictionary.FO_API_CHECK_PLUGIN;
        request = request.replace(FOAPI_Dictionary.FO_API_PLUGIN,plugin_name);
        request = request.replace(FOAPI_Dictionary.FO_API_TOKEN,FO_Token);

        JSONObject jo = null;
        jo = JSONfunctions.getJSONObjfromURL(request, this.UseUntrustCA, this.ErrorMsg);
        if (jo == null) {return false;}

        try {
            res = jo.getInt(FOAPI_Dictionary.FO_API_FIELD_PLUGIN_STATE);
        } catch (Exception e) {
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
        if (!checkPlugin(FOAPI_Dictionary.FO_PLUGIN_NAME)) {return null;}
        resetError();
        String request = this.FO_URL + FOAPI_Dictionary.FO_VAPI_REQUEST;
        request = request.replace(FOAPI_Dictionary.FO_API_METHOD,method);
        request = request.replace(FOAPI_Dictionary.FO_API_SERVICE,service);
        request = request.replace(FOAPI_Dictionary.FO_API_TOKEN, this.FO_Token);

        JSONObject jo = null;
        jo = JSONfunctions.getJSONObjfromURL(request, this.UseUntrustCA, this.ErrorMsg);

        return jo;
    }

    //Execute API operation with arguments
    public JSONObject executeAPI(String method, String service, String args[]){
        if (method.isEmpty()) {return null;}
        if (this.FO_Token.isEmpty())
            if (!testConnection()) {return null;}

        resetError();

        JSONObject jo = null;

        if (!checkPlugin(FOAPI_Dictionary.FO_PLUGIN_NAME)) {return null;}
        resetError();
        String argStr = "{}";
        if (args.length > 0){
            JSONObject jsargs = new JSONObject();
            try {
                for (int i = 0; i < args.length; i += 2) {
                    jsargs.put(args[i], args[i + 1]);
                }
                argStr = jsargs.toString();
            }
            catch (Exception e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
        }
        String request = this.FO_URL + FOAPI_Dictionary.FO_VAPI_REQUEST;
        request = request.replace(FOAPI_Dictionary.FO_API_METHOD,method);
        request = request.replace(FOAPI_Dictionary.FO_API_SERVICE,service);
        request = request.replace(FOAPI_Dictionary.FO_API_ARGS,argStr);
        request = request.replace(FOAPI_Dictionary.FO_API_TOKEN,this.FO_Token);

        jo = JSONfunctions.getJSONObjfromURL(request, this.UseUntrustCA, this.ErrorMsg);

        return jo;
    }



    private void resetError(){
        this.ErrorCode = 0;
        this.ErrorMsg = "";
    }

    public ArrayList<FOTT_Member> loadMembers() {
        return FOAPI_Members.load(app);
    }

    public ArrayList<FOTT_Task> loadTasks(Date lastSync) {
        return lastSync == null ? FOAPI_Tasks.load(app) : FOAPI_Tasks.load(app, lastSync);
    }

    public ArrayList<FOTT_Timeslot> loadTimeslots(Date lastSync) {
        return lastSync == null ? FOAPI_Timeslots.load(app) : FOAPI_Timeslots.load(app, lastSync);
    }

    private static class MyTrustManager implements X509TrustManager
    {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException
        {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException
        {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return null;
        }

    }

    private static class NullHostNameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }

    }

    private static class JSONfunctions {

        private static String error="";

        private static void resetError(){
            error = "";
        }

        private static String getStringFromURL(String url, boolean untrustCA) {

            InputStream is = null;
            String result = "";

            // Download JSON data from URL
            if (url.startsWith("https://")) {
                is = getFromHTTPS(url, untrustCA);
            } else {
                is = getFromHTTP(url);
            }
            if (!error.isEmpty()) {
                return null;
            }
            if (is == null) {
                error = "Server didn't responde";
                return null;
            }

            // Convert response to string
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                result = sb.toString();

            } catch (Exception e) {
                error = "Error converting result";
                Log.e("log_tag", "Error converting result " + e.toString());
            }
            if (result.startsWith("[")) {
                //Add global JSON object for array
                result = "{\""+ FOAPI_Dictionary.FO_API_MAIN_OBJ+"\":" + result + "}";
            }
            return result;
        }

        private static void FindErrorInData(String data) {
            if (data == null){
                error = "EmptyData";
                return;
            }

            if (data.isEmpty()){
                error = "EmptyData";
                return;
            }

            if (data.startsWith("Fatal error")) {
                error = data;
                return;
            }

            if (data.startsWith("API Response")) {
                error = data;
                return;
            }

            if (!data.startsWith("{")) {
                error = "Wrong data format";
            }
        }


        public static JSONObject getJSONObjfromURL(String url, boolean untrustCA, String ErrorMsg) {

            resetError();
            JSONObject jObj = null;
            String data = getStringFromURL(url, untrustCA);
            FindErrorInData(data);
            if (!error.isEmpty()) {ErrorMsg = error; return null;}

            try {

                jObj = new JSONObject(data);

            } catch (JSONException e) {
                ErrorMsg = "Error parsing data";
                Log.e("log_tag", "Error parsing data " + e.toString());
            }

            return jObj;
        }

        public static JSONArray getJSONArrfromURL(String url, boolean untrustCA, String ErrorMsg) {

            JSONArray jArr = null;
            JSONObject jObj = getJSONObjfromURL(url, untrustCA, ErrorMsg);
            if (!ErrorMsg.isEmpty()) {return null;}

            try {
                jArr = jObj.getJSONArray("fo_obj");
            } catch (JSONException e) {
                ErrorMsg = "Error parsing data";
                Log.e("log_tag", "Error parsing data " + e.toString());
            }

            return jArr;
        }

        private static InputStream getFromHTTP(String url){

            InputStream content = null;
            try {
                URL url_obj = new URL(url);
                URLConnection httpclient = url_obj.openConnection();
                content = httpclient.getInputStream();
            }
            catch (Exception e)
            {
                error = "Couldn't connect this URL";
                Log.e("log_tag", "Error in http connection " + e.toString());
            }
            return content;
        }

        private static InputStream getFromHTTPS(String url, boolean untrustCA){

            InputStream content = null;
            try {
                URL url_obj = new URL(url);
                HttpsURLConnection httpsclient = (HttpsURLConnection) url_obj.openConnection();

                if (untrustCA) {
                    //Add Self-sign certificate
                    //SSLContext context = getSelfSignedSSL(url);

                    //Disable SSL checks
                    httpsclient.setHostnameVerifier(new NullHostNameVerifier());
                    SSLContext context = SSLContext.getInstance("TLS");
                    TrustManager[] tmlist = {new MyTrustManager()};
                    context.init(null, tmlist, null);

                    httpsclient.setSSLSocketFactory(context.getSocketFactory());
                }
                content = httpsclient.getInputStream();
            }
            catch (Exception e)
            {
                if (untrustCA) {
                    error = "Couldn't connect this URL";
                }
                else
                {
                    error = "Error in https connection. Check URL or enable using untrusted certificates";
                }
                Log.e("log_tag", "Error in https connection " + e.toString());
            }
            return content;
        }

        /* ----
        This function dosn't work properly
        TODO: find, how load certificate from Stream
        ----- */
        private static SSLContext getSelfSignedSSL(String url) throws Exception{
            // Load CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream caInput =new BufferedInputStream(new FileInputStream("local.cer"));
            Certificate ca;
            try{
                ca = cf.generateCertificate(caInput);
                System.out.println("ca="+((X509Certificate) ca).getSubjectDN());
            }finally{
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore =KeyStore.getInstance(keyStoreType);
            keyStore.load(null,null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm =TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf =TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context =SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(),null);
            return context;
        }

    }
}
