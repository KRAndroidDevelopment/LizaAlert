package com.lizaalert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
	final static String TAG = "myLogs";
	volatile boolean activity_active = false;
    public MyService() {
    	Log.d(TAG, "MyService::MyService");
		new Thread(new Runnable() {
	    	@Override
	    	public void run() {
	    		long prev_last_id = 0;
	    		int n = 0;
	    		SharedPreferences mySharedPreferences = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
    			if(mySharedPreferences.contains(MainActivity.APP_PREFERENCES_LAST_ID)) {
    				prev_last_id = mySharedPreferences.getLong(MainActivity.APP_PREFERENCES_LAST_ID, 0);
    			}		
	    		
	    		while(true){
	    			if(delay()) break;
	    			if(activity_active){
	    				n = 0;
	        			if(mySharedPreferences.contains(MainActivity.APP_PREFERENCES_LAST_ID)) {
	        				prev_last_id = mySharedPreferences.getLong(MainActivity.APP_PREFERENCES_LAST_ID, 0);
	        			}		
	    				continue;
	    			}

	            	long last_id = check_updates();
	            	if(last_id == 0 || prev_last_id == last_id){
	            		continue;
	            	}
	            	prev_last_id = last_id;
	            	n++;
	            	Log.d(TAG, "MyService::Thread::run::UPDATE!!!");
	            	lostHumanNotify(n);
	    		}
	    	}
	    	
	    	protected boolean delay(){
	    		try {
//					Thread.sleep(1000L * 30L);
					Thread.sleep(1000L * 60L * 5L);
				} catch (InterruptedException e) {
					return true;
				}
				return false;
	    	}
	    	
	    	protected long check_updates(){
	    		try {
	    	        URL url = new URL("http://narod-fl.ru/lost_humans/check_update.php");
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
//					.setContentText("")
					.setSmallIcon(R.drawable.ic_launcher)
//					.setLargeIcon(aBitmap)
					.build();
	    		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);        
	            notificationManager.notify(0, notification);	    		
	    	}
	    	
	    }).start();
    	
    }
    
    public void onDestroy() {
    	Log.d(TAG, "MyService::onDestroy");
    }
    
    public void onCreate() {
    	Log.d(TAG, "MyService::onCreate");
    }

	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	if(intent == null){
        	Log.d(TAG, "MyService::onStartCommand intent=null flags="+flags+" startId="+startId);
    	}else{
        	Log.d(TAG, "MyService::onStartCommand intent="+(intent.toString())+"flags="+flags+" startId="+startId);
    	}
    	//this.stopSelfResult(startId); // Stop service
        return Service.START_STICKY; //restarts every 7 seconds
        //return Service.START_NOT_STICKY;
    	//return Service.START_REDELIVER_INTENT;
    }    

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
    
}