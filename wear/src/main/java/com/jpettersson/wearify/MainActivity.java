package com.jpettersson.wearify;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        byte[] playlistByteArray = readPlaylistFile();
        if(playlistByteArray != null) {
            DataMap dataMap = DataMap.fromByteArray(playlistByteArray);

            Log.i(TAG, dataMap.toString());
        }else{
            Log.i(TAG, "Playlist file was null!");
        }
    }

    private byte[] readPlaylistFile() {
        try {

            FileInputStream fi = openFileInput("playlists");
            BufferedInputStream buf = new BufferedInputStream(fi);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();


            while (buf.available() > 0) {
                bos.write(buf.read());
            }

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
