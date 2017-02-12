package com.lizaalert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ScheduledService extends IntentService {
	volatile boolean activity_active = false;
	final static String TAG = "myLogs";

	public ScheduledService() {
		super("ScheduledService");
		Log.d("myLogs", "ScheduledService::ScheduledService");
	}
/*	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("myLogs", "ScheduledService::onStartCommand");
		return super.onStartCommand(intent, flags, startId);
        //return Service.START_STICKY; //restarts every 7 seconds
    }
*/	
    @Override
    public IBinder onBind(Intent intent) {
    	Log.d(TAG, "MyService::onBind");
    	activity_active = true;
    	return new Binder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
    	Log.d(TAG, "MyService::unBind");
    	activity_active = false;
    	return super.onUnbind(intent);
    }
    
    @Override
    public void onRebind(Intent intent) {
		Log.d(TAG, "MyService::onRebind");
    	activity_active = true;
		super.onRebind(intent);
	}    

	long last_ts = 0;
	int n = 0;
	
    @Override
	protected void onHandleIntent(Intent intent) {
		Log.d("myLogs", "ScheduledService::onHandleIntent");
		if(activity_active){
			n = 0;
			return;
		}
    	long last_change = check_updates();
    	if(last_change == 0 || last_ts == last_change){
    		return;
    	}
    	last_ts = last_change;
    	n++;
    	Log.d(TAG, "ScheduledService::onHandleIntent");
    	lostHumanNotify(n);
//		stopSelf();
	}
    
	protected long check_updates(){
		try {
	        URL url = new URL("http://narod-fl.ru/check_update.php");
	        //URL url = new URL("http://narod-fl.ru/lost_humans.xml");
	        URLConnection conn = url.openConnection();
	        BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
	        char [] buf = new char[128];
        	int c = reader.read(buf);
        	if(c == -1){ 
        		return 0;
        	}
            sb.append(buf, 0, c);
	        reader.close();
			Log.d("myLogs", sb.toString());
			return Integer.parseInt(sb.toString());
		} catch (Exception e) {
			Log.e("myLogs", e.toString());
		}
		return 0;
	}
	
	private void lostHumanNotify(int n) {
		Context context = getBaseContext();
		Notification notification = new Notification.Builder(context)
			.setContentTitle("Пропал человек " + n)
//			.setContentText("")
			.setSmallIcon(R.drawable.ic_launcher)
//			.setLargeIcon(aBitmap)
			.build();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);        
        notificationManager.notify(0, notification);	    		
	}
}