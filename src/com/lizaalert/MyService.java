package com.lizaalert;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
	final static String TAG = "myLogs";
    public MyService() {
    	Log.d(TAG, "MyService::MyService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.d(TAG, "MyService::onStartCommand flags="+flags+" startId="+startId);
        return Service.START_STICKY; //restarts every 7 seconds
        //return Service.START_NOT_STICKY;
    	//return Service.START_REDELIVER_INTENT;
    }    

    @Override
    public IBinder onBind(Intent intent) {
    	Log.d(TAG, "MyService::onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}