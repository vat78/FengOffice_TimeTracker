package ru.vat78.fotimetracker;


import android.app.Service;
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
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by vat on 17.11.2015.
 *
 * Use for interact with FengOffice web-application
 */

public class FOConnector {

    private static int ErrorCode=0;
    private static String ErrorMsg="";

    private static String FO_User;
    private static String FO_Pwd;
    private static String FO_URL;
    private static String FO_Token;
    private JSONObject jsonobject;

    protected boolean UseUntrustCA = false;

    protected boolean setFo_Url(String foUrl) {
        foUrl = foUrl.trim();
        if (foUrl.length() <1){ return false;}

        if (!foUrl.startsWith("http://") && !foUrl.startsWith("https://")) {
            foUrl = "https://" + foUrl;
        }

        if (!foUrl.endsWith("/")) {foUrl += "/";}
        FO_URL = foUrl;
        return true;
    }

    protected String getFoUrl() {
        return FO_URL;
    }

    protected boolean setFO_User(String FO_User) {
        FOConnector.FO_User = FO_User;
        return true;
    }

    protected String getFO_User() {
        return FO_User;
    }

    protected boolean setFO_Pwd(String FO_Pwd) {
        FOConnector.FO_Pwd = FO_Pwd;
        return true;
    }

    protected String getFO_Pwd() {
        return FO_Pwd;
    }

    protected String getError() {
        return ErrorMsg;
    }

    protected boolean TestConnection() {
        //TODO this is wrong function. Just for tests. Do I need it?
        if (TextUtils.isEmpty(FO_URL) || TextUtils.isEmpty(FO_User)) {
            return false;
        }

        String request = FO_URL + "index.php?c=api&m=login&username=" + FO_User + "&password=" + FO_Pwd;

        // Download JSON data from URL
        JSONObject js = null;
        js = JSONfunctions.getJSONObjfromURL(request, UseUntrustCA);

        if (js == null) {
            ErrorMsg = JSONfunctions.getError();
        } else {
            try {
                FO_Token = js.getString("token");
            } catch (Exception e) {
                ErrorMsg = e.getMessage();
            }

        }


        return (js != null);
    }

    protected boolean get_object(int oid) {
        if (TextUtils.isEmpty(FO_URL) || TextUtils.isEmpty(FO_User)) {
            return false;
        }
        StringBuilder sb = new StringBuilder();

        String request = FO_URL + "index.php?c=api&m=get_object&oid=" + oid +"&auth=" + FO_Pwd;

        jsonobject = JSONfunctions.getJSONObjfromURL(request, UseUntrustCA);

        if (jsonobject == null) {
            ErrorMsg = JSONfunctions.getError();
        }
        return (jsonobject != null);
    }

    protected Map get_task(int oid) {

        return null;
    }

    protected ArrayList get_workspaces(){
        return null;
    }

    protected ArrayList get_tasks(int workspace){
        return null;
    }

    protected ArrayList get_timeslots(int task_id){
        return null;
    }

    protected Boolean put_timeslot(ArrayList timeslot) {
        return null;
    }



    private Void resetError(){
        ErrorCode = 0;
        ErrorMsg = "";
        return null;
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

        private static int ErrorCode=0;
        private static String ErrorMsg="";

        public static Void resetError(){
            ErrorCode=0;
            ErrorMsg = "";
            return null;
        }

        public static int getErrorID() {
            return ErrorCode;
        }

        public static String getError() {
            return ErrorMsg;
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
            if (ErrorCode != 0) {
                return null;
            }
            if (is == null) {
                ErrorCode = 6;
                ErrorMsg = "";
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
                ErrorCode = 4;
                ErrorMsg = "Error converting result";
                Log.e("log_tag", "Error converting result " + e.toString());
            }
            if (result.startsWith("[")) {
                //Add global JSON object for array
                result = "{\"fo_obj\":" + result + "}";
            }
            return result;
        }

        private static Void FindErrorInData(String data) {
            if (data.isEmpty()){
                ErrorCode = 10;
                ErrorMsg = "EmptyData";
                return null;
            }

            if (data.startsWith("Fatal error")) {
                ErrorCode = 11;
                ErrorMsg = data;
                return null;
            }

            if (data.startsWith("API Response")) {
                ErrorCode = 12;
                ErrorMsg = data;
                return null;
            }

            if (!data.startsWith("{")) {
                ErrorCode = 13;
                ErrorMsg = "Wrong data format";
            }

            return null;
        }


        public static JSONObject getJSONObjfromURL(String url, boolean untrustCA) {

            resetError();
            JSONObject jObj = null;
            String data = getStringFromURL(url, untrustCA);
            FindErrorInData(data);
            if (ErrorCode != 0) {return null;}

            try {

                jObj = new JSONObject(data);

            } catch (JSONException e) {
                ErrorCode = 5;
                ErrorMsg = "Error parsing data";
                Log.e("log_tag", "Error parsing data " + e.toString());
            }

            return jObj;
        }

        public static JSONArray getJSONArrfromURL(String url, boolean untrustCA) {

            JSONArray jArr = null;
            JSONObject jObj = getJSONObjfromURL(url, untrustCA);
            if (ErrorCode != 0) {return null;}

            try {
                jArr = jObj.getJSONArray("fo_obj");
            } catch (JSONException e) {
                ErrorCode = 5;
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
                ErrorCode = 2;
                ErrorMsg = "Couldn't connect this URL";
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
                    ErrorCode = 2;
                    ErrorMsg = "Couldn't connect this URL";
                }
                else
                {
                    ErrorCode = 3;
                    ErrorMsg = "Error in https connection. Check URL or enable using untrusted certificates";
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
