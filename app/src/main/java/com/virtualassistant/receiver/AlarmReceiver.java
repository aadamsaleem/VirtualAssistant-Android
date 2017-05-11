package com.virtualassistant.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.virtualassistant.LoggedIn.HomeActivity;
import com.virtualassistant.interfaces.CompletionInterface;
import com.virtualassistant.client.NewsManager;
import com.virtualassistant.client.WeatherManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by aadam on 10/5/2017.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private static String ordinal(int i) {
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];

        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        SharedPreferences prefs = context.getSharedPreferences("VA", MODE_PRIVATE);
        boolean morningNotification = prefs.getBoolean("morningWishSwitch", true);

        if (morningNotification) {
            final Handler handler = new Handler();

            new Thread() {
                public void run() {
                    final String message = "Good Morning! " + WeatherManager.getHighLow(context) + ". The top 5 news are. .";

                    handler.post(new Runnable() {
                        public void run() {

                            NewsManager.getArticles(context, new CompletionInterface() {
                                @Override
                                public void onSuccess(JSONObject result) {

                                    String newsMessage = message;
                                    JSONArray articlesArray = null;
                                    try {
                                        articlesArray = result.getJSONArray("articles");

                                        for (int i = 0; i < 5; i++) {
                                            JSONObject article = articlesArray.getJSONObject(i);

                                            newsMessage = newsMessage + ". " + ordinal(i + 1) + "." + article.getString("title");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Intent speechIntent = new Intent();
                                    speechIntent.setClass(context, HomeActivity.class);
                                    speechIntent.putExtra("MESSAGE", newsMessage);
                                    speechIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    context.startActivity(speechIntent);
                                }

                                @Override
                                public void onFailure() {

                                }
                            });
                        }
                    });

                }
            }.start();
        }


    }
}
