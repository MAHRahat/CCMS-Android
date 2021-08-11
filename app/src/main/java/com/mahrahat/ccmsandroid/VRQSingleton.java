package com.mahrahat.ccmsandroid;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
 * Singleton class for Volley RequestQueue
 */

public class VRQSingleton {
    @SuppressLint("StaticFieldLeak")
    private static VRQSingleton instance;
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;
    private RequestQueue requestQueue;

    private VRQSingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VRQSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VRQSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
