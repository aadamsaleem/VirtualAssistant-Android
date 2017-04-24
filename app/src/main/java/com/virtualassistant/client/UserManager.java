package com.virtualassistant.client;

import android.app.Activity;
import android.content.Context;
import com.facebook.login.LoginManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.virtualassistant.Constants;
import com.virtualassistant.models.User;
import com.virtualassistant.util.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by aadam on 17/12/2016.
 */

public class UserManager {

    //region Public methods
    public static void signup(final User user, Context applicationContext, final CompletionInterface completionInterface) {

        JSONObject json = new JSONObject();
        try {
            json.put("FACEBOOK_ID", user.getFacebookID());
            json.put("USER_NAME", user.getName());
            json.put("EMAIL_ID", user.getEmail());
            json.put("GENDER", user.getGender());
            json.put("FIREBASE_ID", FirebaseInstanceId.getInstance().getToken());

        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = Constants.BASE_URL + Constants.URL_SIGN_UP;

        AsyncHttpClient client = new AsyncHttpClient();
        StringEntity se = null;
        try {
            se = new StringEntity(json.toString());
        } catch (UnsupportedEncodingException e) {
            // handle exceptions properly!
        }
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

//        client.post(applicationContext, url, se, "application/json", new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                if (statusCode == 200) {
//
//                    JSONObject resultJSON = null;
//                    try {
//                        resultJSON = new JSONObject(new String(responseBody));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    completionInterface.onSuccess(resultJSON);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                completionInterface.onFailure();
//            }
//        });


    }

    public static void signOut(Activity activity) {
        PrefUtils.clearCurrentUser(activity);
        LoginManager.getInstance().logOut();
    }


    public static void updatePreferences(Context applicationContext, JSONObject jsonObject, final CompletionInterface completionInterface) {
        String url = Constants.BASE_URL + Constants.URL_UPDATE_PREFERENCES;

        AsyncHttpClient client = new AsyncHttpClient();
        StringEntity se = null;
        try {
            se = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            // handle exceptions properly!
        }
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        client.post(applicationContext, url, se, "application/json", new AsyncHttpResponseHandler() {
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
                completionInterface.onFailure();
            }
        });

    }

    public static void getPreferences(Context context, final CompletionInterface completionInterface) {
        String url = Constants.BASE_URL + Constants.URL_GET_PREFERENCES;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("USER_TOKEN", PrefUtils.getCurrentUser(context).getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        StringEntity se = null;
        try {
            se = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            // handle exceptions properly!
        }
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        client.post(context, url, se, "application/json", new AsyncHttpResponseHandler() {
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
                completionInterface.onFailure();
            }
        });

    }

    //endregion

}
