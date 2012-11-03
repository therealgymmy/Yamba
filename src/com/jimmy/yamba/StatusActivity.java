package com.jimmy.yamba;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

public class StatusActivity extends Activity implements OnClickListener,
                                                        TextWatcher,
                                                        OnSharedPreferenceChangeListener {
    private static final String TAG = "StatusActivity";
    EditText editText;
    Button updateButton;
    Twitter twitter;
    TextView textCount;
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // Find views
        editText = (EditText) findViewById(R.id.editText);
        updateButton = (Button) findViewById(R.id.buttonUpdate);
        updateButton.setOnClickListener(this);

        textCount = (TextView) findViewById(R.id.textCount);
        textCount.setText(Integer.toString(140));
        textCount.setTextColor(Color.GREEN);
        editText.addTextChangedListener(this);

        twitter = new Twitter("test_user_beyondsora", "test_user_beyondsora");
        twitter.setAPIRootUrl("http://www.Identi.ca/api");

        // Setup preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_status, menu);
        return true;
    }

    // Called when an options item is clicked
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, PrefsActivity.class));
                break;
            case R.id.itemServiceStart:
                startService(new Intent(this, UpdaterService.class));
                break;
            case R.id.itemServiceStop:
                stopService(new Intent(this, UpdaterService.class));
                break;
        }

        return true;
    }

    // Called when button is clicked
    public void onClick(View v) {
        String status = editText.getText().toString();
        new PostToTwitter().execute(status);
        Log.d(TAG, "onClicked");
    }

    public void onSharedPreferenceChanged (SharedPreferences prefs, String key) {
        // invalidate twitter object
        twitter = null;
    }

    // Asynchronously posts to twitter
    class PostToTwitter extends AsyncTask<String, Integer, String> {
        // Called to initiate the background activity
        @Override
        protected String doInBackground (String... statuses) {
            try {
                YambaApplication yamba = ((YambaApplication) getApplication());
                winterwell.jtwitter.Status status = yamba.getTwitter().updateStatus(statuses[0]);
                return status.text;
            } catch (TwitterException e) {
                Log.e(TAG, "Failed to connect to twitter service", e);
                e.printStackTrace();
                return "Failed to post";
            }
        }

        // Called when there's a status to be updated
        @Override
        protected void onProgressUpdate (Integer... values) {
            super.onProgressUpdate(values);
            // Not used in this case.
        }

        // Called once the background activity has completed
        @Override
        protected void onPostExecute (String result) {
            Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }

    // TextWatcher methods
    public void afterTextChanged (Editable statusText) {
        int count = 140 - statusText.length();
        textCount.setText(Integer.toString(count));
        textCount.setTextColor(Color.GREEN);
        if (count < 10) {
            textCount.setTextColor(Color.YELLOW);
        }
        if (count < 0) {
            textCount.setTextColor(Color.RED);
        }
    }

    public void beforeTextChanged (CharSequence s, int start, int count, int after) {}
    public void onTextChanged (CharSequence s, int start, int count, int after) {}

    private Twitter getTwitter () {
        if (twitter == null) {
            String username, password, apiRoot;
            username = prefs.getString("username", "");
            password = prefs.getString("password", "");
            apiRoot = prefs.getString("apiRoot", "http://www.Identi.ca/api");

            // Connect to twitter.com
            twitter = new Twitter(username, password);
            twitter.setAPIRootUrl(apiRoot);
        }
        return twitter;
    }
}
