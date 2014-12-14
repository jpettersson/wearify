package com.jpettersson.wearify;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by jpettersson on 12/14/14.
 */
public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayerListenerService";
    public static final String PLAYLISTS_PATH = "/spotify/playlists";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.i(TAG, "onDataChanged: " + dataEvents);

        final List<DataEvent> events = FreezableUtils
                .freezeIterable(dataEvents);


        // Loop through the events and send a message
        // to the node that created the data item.
        for (DataEvent event : events) {
            Log.i(TAG, event.toString());
            if(event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(PLAYLISTS_PATH)) {
                    DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.v("myTag", "DataMap received on watch: " + dataMap.toString());
                    writeFile(dataMap.toByteArray());
                }
            }
        }
    }

    private void writeFile(byte[] byteArray) {
        Log.i(TAG, "Writing data to file..");
        String filename = "playlists";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(byteArray);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}