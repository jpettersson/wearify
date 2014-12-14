package com.jpettersson.wearify;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by jpettersson on 12/14/14.
 */
public class GoogleApiManager {

    private static final String TAG = "GoogleApiManager";
    private static GoogleApiManager mInstance;
    private final Context mCtx;
    private final GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    private GoogleApiManager(Context context) {
        mCtx = context;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle connectionHint) {
                    Log.d(TAG, "onConnected: " + connectionHint);
                    // Now you can use the Data Layer API
                }
                @Override
                public void onConnectionSuspended(int cause) {
                    Log.d(TAG, "onConnectionSuspended: " + cause);
                }
            })
            .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult result) {
                    Log.d(TAG, "onConnectionFailed: " + result);
                }
            })
                    // Request access only to the Wearable API
            .addApi(Wearable.API)
            .build();
    }
    
    public static synchronized GoogleApiManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GoogleApiManager(context);
        }
        return mInstance;
    }

    public void connect() {
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

}
