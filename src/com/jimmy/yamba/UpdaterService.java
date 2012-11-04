package com.jimmy.yamba;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
    private static final String TAG = "UpdaterService";

    static final int DELAY = 60000;     // a minute
    private boolean runFlag = false;
    private Updater updater;
    private YambaApplication yamba;

    //DbHelper dbHelper;
    //SQLiteDatabase db;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.yamba = (YambaApplication) getApplication();
        this.updater = new Updater();

        //dbHelper = new DbHelper(this);

        Log.d(TAG, "onCreated");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        this.runFlag = true;
        this.updater.start();
        this.yamba.setServiceRunning(true);

        Log.d(TAG, "onStarted");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.runFlag = false;
        this.updater.interrupt();
        this.updater = null;
        this.yamba.setServiceRunning(false);

        Log.d(TAG, "onDestroyed");
    }

    // Thread that performs the actual update from the online service
    private class Updater extends Thread {
        //List<winterwell.jtwitter.Status> timeline;

        public Updater () {
            super("UpdaterService-Updater");
        }

        @Override
        public void run () {
            UpdaterService updaterService = UpdaterService.this;
            while (updaterService.runFlag) {
                Log.d(TAG, "Running background thread");
                try {
                	YambaApplication yamba = (YambaApplication) updaterService.getApplication();
                	int newUpdates = yamba.fetchStatusUpdates();
                	if(newUpdates > 0) {
                		Log.d(TAG, "We have a new status");
                		
                	}
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    updaterService.runFlag = false;
                }
            }
        }
    }
}
