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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "MainActivity";
    private ListView mPlaylistListView;
    Node mNode; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    private static final String WEARIFY_START_PLAYLIST = "/spotify/start_playlist";
    private boolean mResolvingError=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Connect the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();

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
                sendMessage(playlistUri);
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

    /**
     * Send message to mobile handheld
     */
    private void sendMessage(String playlistUri) {

        if (mNode != null && mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), WEARIFY_START_PLAYLIST, playlistUri.getBytes(Charset.forName("UTF-8"))).setResultCallback(

                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e("TAG", "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }else{
            //Improve your code
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    /*
     * Resolve the node = the connected device to send the message to
     */
    private void resolveNode() {

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    mNode = node;
                }
            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {
        resolveNode();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Improve your code
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Improve your code
    }
}
