package ru.vat78.fotimetracker.fengoffice;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.vat78.fotimetracker.fengoffice.vatApi.ApiDictionary;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;

/**
 * Created by vat on 16.04.17.
 */
public class HttpJsonClient {
    private String error="";

    public JSONObject getJsonObject(String url, Properties params, boolean untrustCA) throws HttpJsonError {

        resetError();
        JSONObject jObj = null;
        String data = getStringFromURL(prepareUrl(url,params), untrustCA);
        FindErrorInData(data);
        if (!error.isEmpty())
            throw new HttpJsonError(error);

        try {
            jObj = new JSONObject(data);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
            throw new HttpJsonError("Error parsing data", e);
        }
        return jObj;
    }

    public JSONArray getJsonArray(String url, Properties params, boolean untrustCA, String ErrorMsg) throws HttpJsonError {

        JSONArray jArr = null;
        JSONObject jObj = getJsonObject(url, params, untrustCA);

        try {
            jArr = jObj.getJSONArray("fo_obj");
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
            throw new HttpJsonError("Error parsing data", e);
        }
        return jArr;
    }

    private String prepareUrl(String url, Properties params) {
        for (String p : params.stringPropertyNames()) {
            url = url.replace(p, removeWrongSymbols(params.getProperty(p)));
        }
        return url;
    }

    private void resetError(){
        error = "";
    }

    private String getStringFromURL(String url, boolean untrustCA) {

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
            result = "{\""+ ApiDictionary.FO_API_MAIN_OBJ+"\":" + result + "}";
        }
        if (result.startsWith("false") || result.startsWith("true"))
            result = "{\"" + ApiDictionary.FO_API_FIELD_RESULT+ "\":\"" + result + "\"}";
        return result;
    }

    private void FindErrorInData(String data) {
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

    private String removeWrongSymbols(String str) {
        String res = str.replace("%","%25");
        res = res.replace(" ","%20");
        res = res.replace("!","%21");
        res = res.replace("\"","%22");
        res = res.replace("#","%23");
        res = res.replace("&","%26");
        res = res.replace("'","%27");
        res = res.replace("*","%2a");
        res = res.replace("<","%3c");
        res = res.replace("=","%3d");
        res = res.replace(">","%3e");
        res = res.replace("?","%3f");
        res = res.replace("[","%5b");
        res = res.replace("]","%5d");
        res = res.replace("^","%5e");
        res = res.replace("`","%60");
        res = res.replace("{","%7b");
        res = res.replace("|","%7c");
        res = res.replace("}","%7d");

        return res;
    }

    private InputStream getFromHTTP(String url){

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

    private InputStream getFromHTTPS(String url, boolean untrustCA){

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
    private SSLContext getSelfSignedSSL(String url) throws Exception{
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
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf =TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context =SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(),null);
        return context;
    }

    private class MyTrustManager implements X509TrustManager
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

    private class NullHostNameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }

    }
}
