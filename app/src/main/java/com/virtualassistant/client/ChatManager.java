package com.virtualassistant.client;

import android.app.Activity;
import android.os.Looper;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.virtualassistant.Constants;
import com.virtualassistant.interfaces.CompletionInterface;
import com.virtualassistant.util.Util;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;


/**
 * Created by aadam on 25/4/2017.
 */

public class ChatManager {

    public static void sendMessage(final Activity activity, final JSONObject jsonObject, final CompletionInterface completionInterface) {

        final Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;

                try {
                    HttpPost post = new HttpPost(Constants.CHATBOT_URL);

                    StringEntity se = new StringEntity( jsonObject.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    /*Checking response */
                    if(response!=null){
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                        final JSONObject resultJson = new JSONObject(Util.convertStreamToString(in));
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                completionInterface.onSuccess(resultJson);
                            }
                        });


                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }



    public static void sendAudio(final Activity activity, String filePath, final CompletionInterface completionInterface){
        RequestParams params = new RequestParams();
        final File file = new File(filePath);
        try {
            params.put("file", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.CHATBOT_SPEECH_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("statusCode "+statusCode);
                try {
                    completionInterface.onSuccess(new JSONObject(new String(responseBody)));
                    file.delete();
                } catch (JSONException e) {
                    file.delete();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                completionInterface.onFailure();
            }
        });
    }

    public static void sendAnalysisAudio(String filePath, final CompletionInterface completionInterface){
        RequestParams params = new RequestParams();
        final File file = new File(filePath);
        try {
            params.put("file", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.CHATBOT_SPEECH_ANALYSIS_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("statusCode ",""+statusCode);
                Log.e("aaaahahahaa",""+new String(responseBody));
                try {
                    completionInterface.onSuccess(new JSONObject(new String(responseBody)));
                    file.delete();
                } catch (JSONException e) {
                    file.delete();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                completionInterface.onFailure();
            }
        });
    }
}
