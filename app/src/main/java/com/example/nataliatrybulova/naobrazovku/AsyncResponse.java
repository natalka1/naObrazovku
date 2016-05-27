package com.example.nataliatrybulova.naobrazovku;

import org.json.JSONObject;

/**
 * Created by nataliatrybulova on 09.05.16.
 */
public interface AsyncResponse {
    void processFinish(JSONObject output);
}