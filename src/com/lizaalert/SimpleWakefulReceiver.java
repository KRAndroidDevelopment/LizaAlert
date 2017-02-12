package com.lizaalert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class SimpleWakefulReceiver extends WakefulBroadcastReceiver {
//	private static final long PERIOD = 1000 * 60 * 5;
//	private static final long PERIOD = 1000 * 30;
	private static final long PERIOD = 1000 * 120;
	
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("myLogs", "SimpleWakefulReceiver::Starting service @ " + SystemClock.elapsedRealtime());
    	
        // This is the Intent to deliver to our service.
        //Intent service = new Intent(context, SimpleWakefulService.class);
        // Start the service, keeping the device awake while it is launching.
        //startWakefulService(context, service);
        scheduleAlarms(context, 0);
    }
    
    static void scheduleAlarms(Context context, long period) {
		 /*
		Log.d("myLogs", "SimpleWakefulReceiver::scheduleAlarms");
		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, SimpleWakefulService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + PERIOD, PERIOD, pi);
			*/
		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, SimpleWakefulService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		mgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + period, pi);
    }
    
}