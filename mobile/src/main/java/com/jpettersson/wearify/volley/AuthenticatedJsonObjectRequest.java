package com.jpettersson.wearify.volley;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jpettersson on 12/13/14.
 */
public class AuthenticatedJsonObjectRequest extends JsonObjectRequest {

    private final String accessToken;

    public AuthenticatedJsonObjectRequest(String accessToken, int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);

        this.accessToken = accessToken;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<>();
        String auth = "Bearer " + accessToken;
        params.put("Authorization", auth);
        return params;
    }
}
