package com.virtualassistant.interfaces;

import org.json.JSONObject;

/**
 * Created by aadam on 18/12/2016.
 */

public interface CompletionInterface {
    void onSuccess(JSONObject result);

    void onFailure();
}
