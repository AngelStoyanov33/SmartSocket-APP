package com.smartsocket.app.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HTTPRequestManager {

    public static final String SERVER_LOCATION = "http://192.168.0.105:5000";


    public void sendJSONRequestVolley(final String URL, HashMap<String, String> params_, Context context, HashMap<String, String> response){
        HashMap<String, String> params = params_;

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                response1 -> {
                    Log.d("Response", response1.toString());
                    try {
                        response.put("device_code", (String) response1.get("device_code"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> VolleyLog.e("Error: ", error.getMessage()));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(req);
    }


    public static HashMap<String, String> toMap(JSONObject jsonobj)  throws JSONException {
        HashMap<String, String> map = new HashMap<String, String>();
        Iterator<String> keys = jsonobj.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            Object value = jsonobj.get(key);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, (String) value);
        }   return map;
    }
    protected static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }
            else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }   return list;
    }

}
