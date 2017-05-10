package com.virtualassistant.client;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import com.virtualassistant.Constants;
import com.virtualassistant.LoggedIn.HomeActivity;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by aadam on 8/5/2017.
 */

public class EventManager {


    public static void sendAllEvents(final Context context, final String playerId, final CompletionInterface completionInterface) {

        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"), new String[]{"calendar_id", "_id", "title", "description", "dtstart"}, null, null, null);
        cursor.moveToFirst();

        final JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("onsignal_playerId", playerId);

            Date date = new Date();

            JSONArray events = new JSONArray();
            for (int i = 0; i < cursor.getCount(); i++) {

                JSONObject eventJson = null;
                if (date.before(new Date(getDate(Long.parseLong(cursor.getString(4)))))) {
                    eventJson = new JSONObject();

                    eventJson.put("Calender Id", cursor.getString(0));
                    eventJson.put("Event id", cursor.getString(1));
                    eventJson.put("summary", cursor.getString(2));
                    eventJson.put("description", cursor.getString(3));
                    eventJson.put("start_date", getDate(Long.parseLong(cursor.getString(4))));


                    events.put(eventJson);
                }
                cursor.moveToNext();

            }

            requestJson.put("onesignal_events", events);

            Log.e("aaaa", requestJson.toString());


            final Thread t = new Thread() {

                public void run() {
                    Looper.prepare(); //For Preparing Message Pool for the child Thread
                    HttpClient client = new DefaultHttpClient();
                    HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                    HttpResponse response;

                    try {
                        HttpPost post = new HttpPost(Constants.EVENT_URL);

                        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestJson.toString());
                        se.setContentType(new org.apache.http.message.BasicHeader(org.apache.http.protocol.HTTP.CONTENT_TYPE, "application/json"));
                        post.setEntity(se);
                        response = client.execute(post);

                    /*Checking response */
                        if (response != null) {
                            InputStream in = response.getEntity().getContent(); //Get the data in the entity
                            final JSONArray resultJson = new JSONArray(Util.convertStreamToString(in));
                            JSONObject result = new JSONObject();
                                    try {
                                        result.put("result", resultJson);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    completionInterface.onSuccess(result);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Looper.loop(); //Loop in the message queue
                }
            };

            t.start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "MMM dd, yyyy h:mm:ss a Z");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
