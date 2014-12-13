package com.jpettersson.wearify;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String CLIENT_ID = "460d2cafee9d4cf39f913b131bcc80b5";
    private static final String REDIRECT_URI = "com-jpettersson-wearify://callback";

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
