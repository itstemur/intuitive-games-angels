package com.visafm.common;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

@SuppressWarnings("deprecation")
public class HttpConnectionD extends AsyncTask<String, String, String>
{
    String TAG = "HttpConnection";
    String urlAppend="";
    HttpURLConnection httpConn;
	Context context;
	Boolean isloading = false ;
	String requestedfor= "";
	boolean isHttpGet = true;
    HashMap<String, String> postDataParams = null;
    BaseClassD delegate;
    boolean isDelegate = false;

	public String getUrl() {
		return urlAppend;
	}

	public void setUrl(String urlAppend) {
		this.urlAppend = urlAppend;
	}

    public HttpConnectionD(BaseClassD delegate, Context context)
    {
        this.delegate = delegate;
        this.context = context;
        isDelegate = true;
    }

    public void setPostDataParams(HashMap<String, String> postDataParams) {
        this.postDataParams = postDataParams;
        isHttpGet = false;
    }

    public String getRequestedfor() {
        return requestedfor;
    }


    public void setRequestedfors(String requestedfor,Context context) {
            this.requestedfor = requestedfor;
    }
    public void setRequestedforss(String requestedfor,Context context,View v) {
        this.requestedfor = requestedfor;
    }
    public Boolean getIsloading() {
        return isloading;
    }

    public void setIsloading(Boolean isloading) {
        this.isloading = isloading;
    }

	@Override
    protected void onPreExecute()
    {
		try {
			if (!Common.isConnectingToInternet(context)) {
				Common.showAlert(context,Common.CHECK_INTERNET_CONNECTION);
			} else {
				if (this.isloading) {
					Common.startProgressDialouge(context,this.getRequestedfor());
				}
			}
			super.onPreExecute();
		}
		catch (Exception e) 
		{	e.printStackTrace();	}
    }

	@Override
	protected void onPostExecute(String outPut) 
	{
		Common.myLog("HttpConnection", "Response:"+ outPut);
		try 
		{
			if(!"Exception".equals(outPut))
			{
                httpConn.disconnect();
                Common.myLog("HttpConnection", "New Response:"+ outPut);
                delegate.httpResponses(outPut, requestedfor,context);
               
			}
			else
			{
				Common.stopProgressDialouge(requestedfor);
                Common.myLog("HttpConnection", "New Response:" + outPut);
				Common.showAlert(context, Common.TECHNICAL_PROBLEM);
                delegate.httpFailure(outPut, requestedfor);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			Common.stopProgressDialouge(requestedfor);
		}
	}

	@Override
	protected String doInBackground(String... arg0) 
	{
		String result ;
		if (!Common.isConnectingToInternet(context))
			return null;

		int timeoutConnection = 60 * 1000;	//in milisecond
        int readTimeout = 60 * 1000;       //in milisecond

        try
        {
            Common.myLog(TAG, "URL:"+Common.SERVER_URL+urlAppend);
            URL url = new URL(Common.SERVER_URL+urlAppend);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setReadTimeout(readTimeout);
            httpConn.setConnectTimeout(timeoutConnection);
            httpConn.setDoOutput(true);
            if(isHttpGet) {
                httpConn.setRequestMethod("GET");
                Log.v(TAG, "In Http GET");
            }
            else {
                Log.v(TAG, "In Http Post");
                Log.v(TAG, "postDataParams:" + postDataParams);
                httpConn.setRequestMethod("POST");
                OutputStream os = httpConn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(Common.getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();

            }

            Log.v("HttpConnection","StatusCode:"+httpConn.getResponseCode());
			if (httpConn.getResponseCode() == HttpsURLConnection.HTTP_OK)
			{
                result = "";
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"UTF-8"));
//                BufferedReader br = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"), 8000);
                while ((line=br.readLine()) != null) {
                    result+=line;
                }
				return result;
			} 
			else
				return "Exception";
		}
		catch (Exception e)
		{
			Common.stopProgressDialouge(requestedfor);
			e.printStackTrace();
			return "Exception";
		}
	}
}