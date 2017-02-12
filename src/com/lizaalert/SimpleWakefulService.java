package com.lizaalert;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class SimpleWakefulService extends IntentService {
    public SimpleWakefulService() {
        super("SimpleWakefulService");
        Log.i("myLogs", "SimpleWakefulService::SimpleWakefulService");
    }

	public void onCreate() {
		super.onCreate();
		Log.d("myLogs", "SimpleWakefulService::onCreate");
	}
 
	public void onDestroy() {
		super.onDestroy();
		Log.d("myLogs", "SimpleWakefulService::onDestroy");
	}
	
	public IBinder onBind(Intent arg0) {
		Log.d("myLogs", "SimpleWakefulService::onBind");
		return null;
	}
/*
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("myLogs", "SimpleWakefulService::onStartCommand");
        SimpleWakefulReceiver.completeWakefulIntent(intent);
		//stopSelf(startId);
        this.stopSelfResult(startId);
		//this.stopService(intent);
		return 0;
		//return IntentService.START_STICKY;
		//return IntentService.START_NOT_STICKY;
	}	
  */   
    @Override
    protected void onHandleIntent(Intent intent) {
		//Intent i2 = new Intent(this, SimpleWakefulService.class);
		//SimpleWakefulReceiver.scheduleAlarms(this, 30000);	//Start service
        // At this point SimpleWakefulReceiver is still holding a wake lock
        // for us.  We can do whatever we need to here and then tell it that
        // it can release the wakelock.  This sample just does some slow work,
        // but more complicated implementations could take their own wake
        // lock here before releasing the receiver's.
        //
        // Note that when using this approach you should be aware that if your
        // service gets killed and restarted while in the middle of such work
        // (so the Intent gets re-delivered to perform the work again), it will
        // at that point no longer be holding a wake lock since we are depending
        // on SimpleWakefulReceiver to that for us.  If this is a concern, you can
        // acquire a separate wake lock here.
        for (int i=0; i<2; i++) {
            Log.i("myLogs", "Running service " + (i+1)
                    + "/5 @ " + SystemClock.elapsedRealtime());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }
        //Thread.currentThread().getId()
        Log.i("myLogs", "Completed service @ " + SystemClock.elapsedRealtime());
        Log.i("myLogs", "Completed service @ " + SystemClock.currentThreadTimeMillis());
        SimpleWakefulReceiver.completeWakefulIntent(intent);
        //this.stopSelfResult(startId);
        super.stopSelf();
    }
}