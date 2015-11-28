package net.swaas.drinfo.core;

import android.support.design.widget.TabLayout;
import android.widget.LinearLayout;

import net.swaas.drinfo.core.exception.NetworkException;
import net.swaas.drinfo.logger.LogTracer;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by SwaaS on 5/18/2015.
 */
public class CoreREST {

    private static final LogTracer LOG_TRACER = LogTracer.instance(CoreREST.class);
    private static final String METHOD_POST = "POST";
    private static final String METHOD_GET = "GET";
    //public static final String SERVER = "http://192.168.0.121:3333/";
    public static final String SERVER = "http://doctorbonddcqaws.hidoctor.me/";
    //public static final String SERVER = "http://doctorbonddcws.hidoctor.me/";
    public static final String MESSAGE_NETWORK_ERROR = "No Network! Turn ON data or WiFi or check your network conncetivity.";

    public static String _addContext(String url, Object[] context) {
        StringBuilder sb = new StringBuilder();
        if(url != null) {
            sb.append(url);
            if(context != null && context.length > 0) {
                for(int i=0;i<=context.length-1;i++) {
                    sb.append(context[i]);
                    sb.append("/");
                }
            }
        }
        return sb.toString();
    }
    public static String _rawString(String url, String requestType, Object[] context, Object data, Map<String,String> headers) throws Exception {
        HttpURLConnection con = null;
        URL obj = null;
        BufferedReader in = null;
        try {
            String sUri = _addContext(url, context);
            obj = new URL(sUri);
            con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(25000);
            // optional default is GET
            con.setRequestMethod(requestType);

            //add request header
            //con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept-Encoding", "");
            //Add Dynamic Headers to connection
            if(headers != null && headers.size() > 0) {
                Set<Map.Entry<String, String>> entries = headers.entrySet();
                Iterator<Map.Entry<String, String>> itr = entries.iterator();
                Map.Entry<String, String> ent = null;
                while(itr.hasNext()) {
                    ent = itr.next();
                    con.setRequestProperty(ent.getKey(), ent.getValue().toString());
                }
            }

            LOG_TRACER.d("Url: " + sUri);
            // Send post request
            if(data != null) {
                LOG_TRACER.d("Sending Data " + data.toString());
                DataOutputStream wr = null;
                try {
                    con.setDoOutput(true);
                    wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(data.toString());
                } catch (Exception e) {
                    //LogTracer.e(TAG, e.toString(), e);
                    LOG_TRACER.e(e.toString(), e);
                } finally {
                    if(wr != null) {
                        try {
                            wr.flush();
                            wr.close();
                        } catch (Exception e) {
                            //LogTracer.e(TAG, e.toString(), e);
                            LOG_TRACER.e(e.toString(), e);
                        }
                    }
                }
            }

            int responseCode = con.getResponseCode();
            Map<String, List<String>> respHeaders = con.getHeaderFields();
            Set<Map.Entry<String, List<String>>> entries = respHeaders.entrySet();
            Iterator<Map.Entry<String, List<String>>> itr = entries.iterator();
            Map.Entry<String, List<String>> entry = null;
            while (itr.hasNext()) {
                entry = itr.next();
                LOG_TRACER.d("Header Key: " + entry.getKey());
                LOG_TRACER.d("    Header Values: " + entry.getValue());
            }
            LOG_TRACER.d("Response code for URL: " + sUri + " - " + responseCode);
            if(responseCode == 200) {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                LOG_TRACER.d(response.toString());
                return response.toString();
            } else {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                LOG_TRACER.e(response.toString());
                throw new Exception(response.toString());
            }
        } catch (Exception e) {
            LOG_TRACER.e(e.toString(), e);
            if (e instanceof IOException) {
                throw new NetworkException(e, MESSAGE_NETWORK_ERROR);
            } else if (e instanceof ConnectException) {
                throw new NetworkException(e, MESSAGE_NETWORK_ERROR);
            }
            throw e;
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    LOG_TRACER.e(e.toString(), e);
                }
            }
            if(con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                    LOG_TRACER.e(e.toString(), e);
                }
            }
        }
    }

    public static String _upload(String url, Object[] context, Map<String, File> files
            , Map<String, Object> params, MultipartUtility.OnProgress onProgress) throws Exception {
        String charset = "UTF-8";
        try {
            String sUri = _addContext(url, context);
            MultipartUtility multipart = new MultipartUtility(sUri, charset);
            if(params != null && params.size() > 0) {
                Set<Map.Entry<String, Object>> entries = params.entrySet();
                Iterator<Map.Entry<String, Object>> itr = entries.iterator();
                Map.Entry<String, Object> ent = null;
                while(itr.hasNext()) {
                    ent = itr.next();
                    multipart.addFormField(ent.getKey(), ent.getValue().toString());
                }
            }
            if(files != null && files.size() > 0) {
                Set<Map.Entry<String, File>> entries = files.entrySet();
                Iterator<Map.Entry<String, File>> itr = entries.iterator();
                Map.Entry<String, File> ent = null;
                while(itr.hasNext()) {
                    ent = itr.next();
                    multipart.addFilePart(ent.getKey(), ent.getValue(), onProgress);
                }
            }

            String resopnse = multipart.finish();
            return resopnse;
        } catch (Exception e) {
            LOG_TRACER.e(e);
            throw e;
        }
    }

    public static final String postMethodFile(String url, Object[] context, Map<String, File> files
            , Map<String, Object> params, MultipartUtility.OnProgress onProgress) throws Exception {
        return _upload(url, context, files, params, onProgress);
    }

    public static final String postMethodString(String url, Object[] context, Object data) throws Exception {
        return _rawString(url, METHOD_POST, context, data, null);
    }

    public static final String postMethodString(String url, Object[] context, Object data, Map<String, String> headers) throws Exception {
        return _rawString(url, METHOD_POST, context, data, headers);
    }

    public static final String getMethodString(String url, Object[] context, Object data) throws Exception {
        return _rawString(url, METHOD_GET, context, data, null);
    }

    public static final String getMethodString(String url, Object[] context, Object data, Map<String,String> headers) throws Exception {
        return _rawString(url, METHOD_GET, context, data, headers);
    }

    // example to use MultipartFileUtility
    /*public class MultipartFileUploader {

        public static void main(Object[] args) {
            String charset = "UTF-8";
            File uploadFile1 = new File("e:/Test/PIC1.JPG");
            File uploadFile2 = new File("e:/Test/PIC2.JPG");
            String requestURL = "http://localhost:8080/FileUploadSpringMVC/uploadFile.do";

            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("User-Agent", "CodeJava");
                multipart.addHeaderField("Test-Header", "Header-Value");

                multipart.addFormField("description", "Cool Pictures");
                multipart.addFormField("keywords", "Java,upload,Spring");

                multipart.addFilePart("fileUpload", uploadFile1);
                multipart.addFilePart("fileUpload", uploadFile2);

                List<String> response = multipart.finish();

                System.out.println("SERVER REPLIED:");

                for (String line : response) {
                    System.out.println(line);
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }*/
}
