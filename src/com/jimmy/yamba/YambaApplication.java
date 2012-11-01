package com.jimmy.yamba;

import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class YambaApplication extends Application implements OnSharedPreferenceChangeListener{
	private static final String TAG = YambaApplication.class.getSimpleName();
	public Twitter twitter;
	private SharedPreferences prefs;
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		Log.i(TAG, "onCreated");
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminated");
	}
	
	public synchronized Twitter getTwitter() {
		if (this.twitter == null) {
			String username = prefs.getString("username", "test_user_beyondsora");
            String password = prefs.getString("password", "test_user_beyondsora");
            String apiRoot = prefs.getString("apiRoot", "http://www.Identi.ca/api");
            if (!TextUtils.isEmpty(username) && 
            	!TextUtils.isEmpty(password) && 
            	!TextUtils.isEmpty(apiRoot) ) {
            	twitter = new Twitter(username, password);
                twitter.setAPIRootUrl(apiRoot);
            }
		}
		return this.twitter;
	}
	
	public synchronized void 
	onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		this.twitter = null;
	}
}
