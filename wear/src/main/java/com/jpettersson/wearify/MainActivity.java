package com.jpettersson.wearify;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private ListView mPlaylistListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mPlaylistListView = (ListView) stub.findViewById(R.id.playlist_list);

                byte[] playlistByteArray = readPlaylistFile();
                if(playlistByteArray != null) {
                    DataMap dataMap = DataMap.fromByteArray(playlistByteArray);
                    Log.i(TAG, dataMap.toString());

                    ArrayList<DataMap> playlistItems = dataMap.getDataMapArrayList("playlistItems");
                    renderList(playlistItems);
                }else{
                    Log.i(TAG, "Playlist file was null!");
                }
            }
        });

    }

    private void renderList(ArrayList<DataMap> playlistDataMaps) {
        final ArrayList<HashMap<String, String>> playlistItemList = new ArrayList<HashMap<String, String>>();

        for(DataMap dataMap : playlistDataMaps) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("uri",  dataMap.getString("uri"));
            map.put("name", dataMap.getString("name"));
            playlistItemList.add(map);
        }

        ListAdapter adapter = new SimpleAdapter(this, playlistItemList , R.layout.playlist_item,
                new String[] { "name" },
                new int[] { R.id.name_text });


//        final ListView lv = (ListView)findViewById(R.id.playlist_list);
        mPlaylistListView.setAdapter(adapter);

        mPlaylistListView.setTextFilterEnabled(true);
        mPlaylistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String playlistUri = playlistItemList.get(position).get("uri");
                Log.i(TAG, "Open playlist: " + playlistUri);
            }
        });
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
