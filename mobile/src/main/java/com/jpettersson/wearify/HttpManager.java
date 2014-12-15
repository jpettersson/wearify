package com.jpettersson.wearify;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
* Created by jpettersson on 12/13/14.
*/
public class HttpManager {

    private static HttpManager mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    private HttpManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized HttpManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HttpManager(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
