package com.jpettersson.wearify;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jpettersson.wearify.volley.AuthenticatedJsonObjectRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String CLIENT_ID = "460d2cafee9d4cf39f913b131bcc80b5";
    private static final String REDIRECT_URI = "com-jpettersson-wearify://callback";
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Awake!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DataLayer.connect(this);
    }

    @Override
    protected void onStop() {
        DataLayer.disconnect(this);
        super.onStop();
    }

    public void authenticate(View view) {
        SpotifyAuthentication.openAuthWindow(CLIENT_ID, "token", REDIRECT_URI,
                new String[]{"playlist-read-private"}, null, this);
    }

    public void getPlaylists(View view) {

        String url = "https://api.spotify.com/v1/users/jonathananderspettersson/playlists";

        JsonObjectRequest jsObjRequest = new AuthenticatedJsonObjectRequest
            (accessToken, Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, response.toString());
                    try {
                        JSONArray jsonArray = response.getJSONArray("items");

                        renderList(jsonArray);
                        DataLayer.writePlaylists(jsonArray, getBaseContext());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.i(TAG, "Volley error!");
                    error.printStackTrace();
                }
            });

        // Access the RequestQueue through your singleton class.
        HttpManager.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private void renderList(JSONArray jsonArray) {
        final ArrayList<HashMap<String, String>> playlistItemList = new ArrayList<HashMap<String, String>>();

        try{
            for(int i=0;i < jsonArray.length();i++){
                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Log.i(TAG, jsonObject.toString());

                map.put("uri",  jsonObject.getString("uri"));
                map.put("name", jsonObject.getString("name"));
                playlistItemList.add(map);
            }
        }catch(JSONException e)        {
            Log.e("log_tag", "Error parsing data "+e.toString());
        }

        ListAdapter adapter = new SimpleAdapter(this, playlistItemList , R.layout.playlist_item,
                new String[] { "name", "uri" },
                new int[] { R.id.name_text, R.id.id_text });


        final ListView lv = (ListView)findViewById(R.id.playlist_list);
        lv.setAdapter(adapter);

        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
//                intent.setComponent(new ComponentName("com.spotify.music", "MainActivity"));
                String playlistUri = playlistItemList.get(position).get("uri");
                Log.i(TAG, "Open playlist: " + playlistUri);
                Uri uri = Uri.parse(playlistUri);
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "New intent!");
        Uri uri = intent.getData();
        if (uri != null) {
            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            String accessToken = response.getAccessToken();
            Log.i(TAG, "Authentication successful!");
            Log.i(TAG, "accessToken:" + accessToken);
            this.accessToken = accessToken;
        }else{
            Log.i(TAG, "Authentication failed!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
