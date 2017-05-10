package com.virtualassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.virtualassistant.interfaces.CompletionInterface;
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


        try {
            EventManager.sendAllEvents(context, playerId, new CompletionInterface() {
                @Override
                public void onSuccess(JSONObject result) {
                }

                @Override
                public void onFailure() {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
