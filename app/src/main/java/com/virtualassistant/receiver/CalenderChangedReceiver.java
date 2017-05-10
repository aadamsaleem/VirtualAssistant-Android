package com.virtualassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.virtualassistant.client.CompletionInterface;
import com.virtualassistant.client.EventManager;

import org.json.JSONObject;

/**
 * Created by aadam on 9/5/2017.
 */

public class CalenderChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences preferences = context.getSharedPreferences("VA", Context.MODE_PRIVATE);
        String playerId = preferences.getString("playerId", null);
        EventManager.sendAllEvents(context, playerId, new CompletionInterface() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("ccccc", result.toString());
            }

            @Override
            public void onFailure() {

            }
        });

    }
}
