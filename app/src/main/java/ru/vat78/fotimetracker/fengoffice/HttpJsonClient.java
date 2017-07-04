package ru.vat78.fotimetracker.fengoffice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.vat78.fotimetracker.IErrorsHandler;
import ru.vat78.fotimetracker.model.ErrorsType;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

/**
 * Created by vat on 16.04.17.
 *
 * TODO: find, how load certificate from Stream
 */
public class HttpJsonClient {
    private static final String CLASS_NAME = "HttpJsonClient";

    private IErrorsHandler errorsHandler;

    public HttpJsonClient(IErrorsHandler errorsHandler) {
        this.errorsHandler = errorsHandler;
    }

    public JSONArray getJsonObject(String url, Properties params, boolean untrustCA) {

        JSONArray jObj = null;
        String data = getStringFromURL(prepareUrl(url,params), untrustCA);
        if (data != null && data.startsWith("{")) {
            data = "[" + data + "]";
        }
        findErrorInData(data);

        if (!errorsHandler.hasStopError()) {
            try {
                jObj = new JSONArray(data);
            } catch (JSONException e) {
                errorsHandler.info(CLASS_NAME, ErrorsType.JSON_PARSING_ERROR, e);
            }
        }
        return jObj;
    }

    private String prepareUrl(String url, Properties params) {
        for (String p : params.stringPropertyNames()) {
            url = url.replace(p, removeWrongSymbols(params.getProperty(p)));
        }
        return url;
    }

    private String getStringFromURL(String url, boolean untrustCA) {

        InputStream is;
        String result = "";

        // Download JSON data from URL
        if (url.startsWith("https://")) {
            is = getFromHTTPS(url, untrustCA);
        } else {
            is = getFromHTTP(url);
        }

        if (!errorsHandler.hasStopError()) {
            if (is == null) {
                errorsHandler.error(CLASS_NAME, ErrorsType.CANT_CONNECT_TO_SERVER);
            } else {

                // Convert response to string
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    result = sb.toString();
                } catch (IOException e) {
                    errorsHandler.error(CLASS_NAME, ErrorsType.WRONG_WEB_ANSWER, e);
                } finally {
                    try {
                        is.close();
                    } catch (IOException ignored) {}
                }

                /*
                if (result.startsWith("[")) {
                    //Add global JSON object for array
                    result = "{\"" + ApiDictionary.FO_API_MAIN_OBJ + "\":" + result + "}";
                } else if (result.startsWith("false") || result.startsWith("true")) {
                    result = "{\"" + ApiDictionary.FO_API_FIELD_RESULT + "\":\"" + result + "\"}";
                }
                */
            }
        }
        return result;
    }

    private void findErrorInData(String data) {
        if (data == null){
            errorsHandler.error(CLASS_NAME, ErrorsType.WRONG_WEB_ANSWER);
            return;
        }

        if (data.isEmpty()){
            errorsHandler.error(CLASS_NAME, ErrorsType.WRONG_WEB_ANSWER);
            return;
        }

        if (data.startsWith("Fatal error")) {
            errorsHandler.error(CLASS_NAME, ErrorsType.WRONG_WEB_ANSWER, data);
            return;
        }

        if (data.startsWith("API Response")) {
            errorsHandler.error(CLASS_NAME, ErrorsType.WRONG_WEB_ANSWER, data);
            return;
        }

        if (!data.startsWith("[")) {
            errorsHandler.error(CLASS_NAME, ErrorsType.WRONG_WEB_ANSWER, data);
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
        catch (IOException e)
        {
            errorsHandler.error(CLASS_NAME, ErrorsType.CANT_CONNECT_TO_SERVER, e);
        }
        return content;
    }

    private InputStream getFromHTTPS(String url, boolean untrustCA){

        InputStream content = null;
        try {
            URL url_obj = new URL(url);
            HttpsURLConnection httpsclient = (HttpsURLConnection) url_obj.openConnection();

            if (untrustCA) {
                //Disable SSL checks
                httpsclient.setHostnameVerifier(new NullHostNameVerifier());
                SSLContext context = SSLContext.getInstance("TLS");
                TrustManager[] tmlist = {new MyTrustManager()};
                context.init(null, tmlist, null);

                httpsclient.setSSLSocketFactory(context.getSocketFactory());
            }
            content = httpsclient.getInputStream();
        }
        catch (NoSuchAlgorithmException | KeyManagementException e) {
            errorsHandler.error(CLASS_NAME, ErrorsType.SYSTEM_ERROR, e);
        }
        catch (IOException e)
        {
            errorsHandler.error(CLASS_NAME, ErrorsType.CANT_CONNECT_TO_SERVER, e);
        }
        return content;
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
