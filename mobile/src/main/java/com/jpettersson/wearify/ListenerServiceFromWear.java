package com.jpettersson.wearify;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.UnsupportedEncodingException;

/**
 * Created by jpettersson on 12/14/14.
 */
public class ListenerServiceFromWear extends WearableListenerService {

    private static final String WEARIFY_START_PLAYLIST = "/spotify/start_playlist";
    private static final String TAG = "ListenerServiceFromWear";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        /*
         * Receive the message from wear
         */

        if (messageEvent.getPath().equals(WEARIFY_START_PLAYLIST)) {
            try {
                String playlistUri = new String(messageEvent.getData(), "UTF-8");
                Log.i(TAG, "Recieved message: " + playlistUri);

                final Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                Log.i(TAG, "Open playlist: " + playlistUri);
                Uri uri = Uri.parse(playlistUri);
                intent.setData(uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                startActivity(intent);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

    }

}