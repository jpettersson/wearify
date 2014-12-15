package com.jpettersson.wearify;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jpettersson on 12/14/14.
 */
public class DataLayer {

    private static final String TAG = "DataLayer";
    public static String PLAYLISTS_PATH = "/spotify/playlists";

    public static void connect(Context context) {
        GoogleApiManager.getInstance(context).connect();
    }

    public static void disconnect(Context context) {
        GoogleApiManager.getInstance(context).disconnect();
    }

    public static void writePlaylists(ArrayList<HashMap<String, String>> playlistItemList, Context context) {

        ArrayList<DataMap> itemArray = new ArrayList<>();

        for(HashMap<String, String> playlistItem : playlistItemList) {
            DataMap dataMap = new DataMap();
            dataMap.putString("uri", playlistItem.get("uri"));
            dataMap.putString("name", playlistItem.get("name"));
            itemArray.add(dataMap);
        }

        PutDataMapRequest requestDataMap = PutDataMapRequest.create(PLAYLISTS_PATH);

        requestDataMap.getDataMap().putDataMapArrayList("playlistItems", itemArray);
        requestDataMap.getDataMap().putLong("timestamp", System.currentTimeMillis());

        PutDataRequest request = requestDataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(GoogleApiManager.getInstance(context).getGoogleApiClient(), request);


        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.i(TAG, "DataLayer: onResult: " + dataItemResult.toString());
            }
        });

    }

}
