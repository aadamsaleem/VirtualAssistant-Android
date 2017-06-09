package com.virtualassistant.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.util.Log;

import com.virtualassistant.Constants;
import com.virtualassistant.interfaces.CompletionInterface;
import com.virtualassistant.models.RecognitoImage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kirank on 5/7/17.
 */

public class RecognitoManager {



    private RecognitoManager() {
    }

    /**
     *
     * @param image
     * @return JsonObject result object after uploading a new image to add to the database
     */
    private static JSONObject uploadNewImage(RecognitoImage image) {
        final JSONObject[] jsonResult = {null};

        DefaultHttpClient mHttpClient;
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        mHttpClient = new DefaultHttpClient(params);

        HttpPost httppost = new HttpPost(Constants.NEW_IMAGE_URL);
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        multipartEntity.addPart("file", new FileBody(image.getImageFile()));
        try {


            multipartEntity.addPart("contactid", new StringBody(""+image.getId()));
//            SharedPreferences preferences = context.getSharedPreferences("VA", Context.MODE_PRIVATE);
//            String playerId = preferences.getString("playerId", null);
            multipartEntity.addPart("userid", new StringBody(Constants.userID));
//            multipartEntity.addPart("userid", new StringBody("kiran12345"));
            multipartEntity.addPart("imagename", new StringBody(image.getPersonName() + System.currentTimeMillis()+ ".jpg"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httppost.setEntity(multipartEntity);

        try {
            mHttpClient.execute(httppost, new ResponseHandler<Object>() {


                @Override
                public Object handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                    HttpEntity r_entity = httpResponse.getEntity();
                    String responseString = EntityUtils.toString(r_entity);
                    try {
                        jsonResult[0] = new JSONObject(responseString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("AAAA UPLOAD", responseString);
                    return null;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonResult[0];
    }

    /**
     *
     * @param base64String
     * @return JSONObject result after uploading the image to test for a match
     */
    private static JSONObject uploadToTestImage(String base64String) {
        String resultString = null;
        JSONObject resultJson = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("base64", base64String));
//        SharedPreferences preferences = context.getSharedPreferences("VA", Context.MODE_PRIVATE);
//        String playerId = preferences.getString("playerId", null);
        nameValuePairs.add(new BasicNameValuePair("userid", Constants.userID));
//        nameValuePairs.add(new BasicNameValuePair("userid", "kiran12345"));
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.TEST_IMAGE_URL);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            resultString = EntityUtils.toString(response.getEntity());
            resultJson = new JSONObject(resultString);
            Log.d(Constants.SERVER_UPLOAD_TAG, "In the try Loop" + resultString);
        } catch (Exception e) {
            Log.d(Constants.SERVER_UPLOAD_TAG, "Error in http connection while testing image" + e.toString());
        }
        return resultJson;
    }

    /**
     *
     * @param context
     * @param image
     * @param callBack
     */
    public static void uploadNewImage(final Context context, final RecognitoImage image, final CompletionInterface callBack) {
        Log.d(Constants.SERVER_UPLOAD_TAG, "Trying to upload the image");
        if(!networkAvailable(context)) {
            callBack.onFailure();
//            callBack.onSuccess(null);

        }
        else {
            Thread uploadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    JSONObject resultJson = uploadNewImage(image);
                    callBack.onSuccess(resultJson);
                }
            });
            uploadThread.start();
        }
    }

    /**
     *
     * @param context
     * @param base64String
     * @param callBack
     */
    public static void uploadToTestImage(final Context context, final String base64String, final CompletionInterface callBack) {
        Log.d(Constants.SERVER_UPLOAD_TAG, "Trying to upload to test the image");
        networkAvailable(context);
        if(!networkAvailable(context)) {
            callBack.onFailure();
//            callBack.onSuccess(null);
        }
        else {
            Thread uploadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    JSONObject resultJson = uploadToTestImage(base64String);
                    callBack.onSuccess(resultJson);
                }
            });
            uploadThread.start();
        }
    }

    /**
     *
     * @param context
     * @return if the network is available ot not
     */
    private static boolean networkAvailable(Context context) {
        boolean isPhoneOnline = isOnline(context);
        Log.d(Constants.SERVER_UPLOAD_TAG, "Status of phone " + isPhoneOnline);
//        boolean isServerOnline = isServerAvailable();
//        boolean isServerOnline = isServerReachable(context);
//        Log.d(Constants.SERVER_UPLOAD_TAG, "Status of server " + isServerOnline);
//        return isPhoneOnline && isServerOnline;
        return isPhoneOnline;
    }

    /**
     *
     * @param context
     * @return boolean if the phone is connected to the internet or not
     */
    private static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    /**
     *
     * @param context
     * @return boolean if the server is reachable or not
     */
    private static boolean isServerReachable(Context context) {
        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        final boolean[] status = {false};
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL(Constants.PING_URL);
                final HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            urlConn.connect();
                            if (urlConn.getResponseCode() == 200) {
                                status[0] = true;
                            }
                        }catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }).start();

            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    /**
     *
     * @return boolean if the server is reachable or not
     */

    private static boolean isServerAvailable() {
        boolean exists = false;
        try {
            SocketAddress sockaddr = new InetSocketAddress(Constants.PING_URL, 8000);
            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            int timeoutMs = 3000;
            sock.connect(sockaddr, timeoutMs);
            exists = true;
            sock.close();
        } catch (SocketTimeoutException e) {
            exists = false;
            Log.d(Constants.SERVER_UPLOAD_TAG, "The server is not available");
        }
        catch (IOException e) {
            exists = false;
            Log.d(Constants.SERVER_UPLOAD_TAG, "The server is not available");
        }
        finally {
            return exists;
        }
    }
}
