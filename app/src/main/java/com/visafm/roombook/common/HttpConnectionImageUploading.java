package com.visafm.roombook.common;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

@SuppressWarnings("deprecation")
public class HttpConnectionImageUploading extends AsyncTask<String, String, String> {
    String TAG = "HttpConnection";
//        String addressUrl = "http://5.175.13.128:90/api/";
    String addressUrl = Common.SERVER_URL;
    String urlAppend = "";
    HttpURLConnection httpConn;
    static Context context;
    Boolean isloading = false;
    String requestedfor = "";
    boolean isHttpGet = true;
    boolean isImageUploading = false;
    File imageFile;
    Uri imageUri;
    HashMap<String, String> postDataParams = null;
    private String boundary;
    private static final String LINE_FEED = "\r\n";
    BaseClass delegate;
    boolean isDelegate = false;

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public void setIsImageUploading(boolean isImageUploading) {
        this.isImageUploading = isImageUploading;
    }

    public String getUrl() {
        return urlAppend;
    }

    public void setUrl(String urlAppend) {
        this.urlAppend = urlAppend;
    }

    public HttpConnectionImageUploading(BaseClass delegate, Context context) {
        this.delegate = delegate;
        HttpConnectionImageUploading.context = context;
        isDelegate = true;
    }

    public void setPostDataParams(HashMap<String, String> postDataParams) {
        this.postDataParams = postDataParams;
        isHttpGet = false;
    }

    public String getRequestedfor() {
        return requestedfor;
    }

    public void setRequestedfor(String requestedfor) {
        this.requestedfor = requestedfor;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        HttpConnectionImageUploading.context = context;
    }

    public Boolean getIsloading() {
        return isloading;
    }

    public void setIsloading(Boolean isloading) {
        this.isloading = isloading;
    }

    @Override
    protected void onPreExecute() {
        try {
            if (!Common.isConnectingToInternet(context)) {
                Common.showAlert(context, Common.CHECK_INTERNET_CONNECTION);
            } else {
                if (this.isloading) {
                    Common.startProgressDialouge(context, this.getRequestedfor());
                }
            }
            super.onPreExecute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String outPut) {
        Common.myLog("HttpConnection", "Response:" + outPut);
        try {
            if (!"Exception".equals(outPut)) {
                httpConn.disconnect();
                //outPut = deSerializeJSON(outPut);
                Common.myLog("HttpConnection", "New Response:" + outPut);

                if (isDelegate)
                    delegate.httpResponse(outPut, requestedfor);
            } else {
                Common.stopProgressDialouge(requestedfor);
                Common.myLog("HttpConnection", "New Response:" + outPut);
                Common.showAlert(context, "There is some technical problem.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Common.stopProgressDialouge(requestedfor);
        }
    }

    @Override
    protected String doInBackground(String... arg0) {
        String result;
        if (!Common.isConnectingToInternet(context))
            return null;

        int timeoutConnection = 20 * 1000;    //in milisecond
        int readTimeout = 20 * 1000;       //in milisecond
        boundary = "===" + System.currentTimeMillis() + "===";

        try {

            URL url = new URL(addressUrl + urlAppend);
            Log.v(TAG, "addressUrl URL:" + addressUrl + urlAppend);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setReadTimeout(readTimeout);
            httpConn.setConnectTimeout(timeoutConnection);
            httpConn.setDoOutput(true);
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
            httpConn.setRequestProperty("Test", "Bonjour");

            OutputStream os = httpConn.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);

            if (postDataParams != null) {
                Iterator it = postDataParams.entrySet().iterator();
                while (it.hasNext()) {

                    Map.Entry pair = (Map.Entry) it.next();
                    addFormField(writer, pair.getKey() + "", pair.getValue() + "");
                    Log.v("ImageUploading", "Key:" + pair.getKey() + "===value:" + pair.getValue());
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
            addFilePart(writer, "ImageName", imageFile, imageUri, os);

            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            Log.v("HttpConnection", "StatusCode:" + httpConn.getResponseCode());
            if (httpConn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                result = "";
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    result += line;
                }
                return result;
            } else
                return "Exception";
        } catch (Exception e) {
            Common.stopProgressDialouge(requestedfor);
            e.printStackTrace();
            return "Exception";
        }
    }

    public void addFormField(PrintWriter writer, String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + "UTF-8").append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(PrintWriter writer, String fieldName, File uploadFile, Uri uploadUri, OutputStream outputStream) throws IOException {
        String fileName = "";
        if(uploadFile != null){
            fileName = uploadFile.getName();
        }
        else{
            fileName = Common.getFileName(context.getContentResolver(), uploadUri);
        }

        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();


        InputStream inputStream = uploadUri != null ? getContext().getContentResolver().openInputStream(uploadUri) :  new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        writer.append(LINE_FEED);
        writer.flush();
    }
}