package com.jpettersson.wearify;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jpettersson.wearify.volley.AuthenticatedJsonObjectRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;

import org.json.JSONObject;


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
