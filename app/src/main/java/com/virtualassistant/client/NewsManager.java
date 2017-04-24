package com.virtualassistant.client;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.virtualassistant.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by aadam on 24/4/2017.
 */

public class NewsManager {

    public static void getArticles(Context context, final CompletionInterface completionInterface) {
        String url = Constants.NEWS_ARTICLES_URL;


        AsyncHttpClient client = new AsyncHttpClient();

        client.get(context, url, null, "application/json", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    JSONObject resultJSON = null;
                    try {
                        resultJSON = new JSONObject(new String(responseBody));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    completionInterface.onSuccess(resultJSON);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Log.e("failed", error.getMessage());
                completionInterface.onFailure();
            }
        });
    }
}
