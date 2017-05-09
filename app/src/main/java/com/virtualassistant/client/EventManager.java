package com.virtualassistant.client;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.virtualassistant.Constants;
import com.virtualassistant.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by aadam on 8/5/2017.
 */

public class EventManager {


    public static void sendAllEvents(final Activity activity, final JSONObject requestJSON, final CompletionInterface completionInterface) {
        final Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;

                try {
                    HttpPost post = new HttpPost(Constants.EVENT_URL);

                    org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity( requestJSON.toString());
                    se.setContentType(new org.apache.http.message.BasicHeader(org.apache.http.protocol.HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    /*Checking response */
                    if(response!=null){
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                        final JSONArray resultJson = new JSONArray(Util.convertStreamToString(in));
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                JSONObject result = new JSONObject();
                                try {
                                    result.put("result", resultJson);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                completionInterface.onSuccess(result);
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
}
