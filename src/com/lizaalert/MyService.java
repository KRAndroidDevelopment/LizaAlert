package com.lizaalert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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
    	if(intent == null){
        	Log.d(TAG, "MyService::onStartCommand intent=null flags="+flags+" startId="+startId);
    	}else{
        	Log.d(TAG, "MyService::onStartCommand intent="+(intent.toString())+"flags="+flags+" startId="+startId);
    	}
		new Thread(new Runnable() {
	    	@Override
	    	public void run() {
	    		while(true){
		    		String xml = check_updates();
		    		if(xml != null){
		    			
		    		}
		    		try {
						//Thread.sleep(1000 * 10);
						Thread.sleep(1000L * 60L * 5L);
					} catch (InterruptedException e) {
						break;
					}
	    		}
	    	}
	    	
	    	protected String check_updates(){
	    		try {
	    	        URL url = new URL("http://narod-fl.ru/lost_humans.xml");
	    	        URLConnection conn = url.openConnection();
	    	        BufferedReader reader = new BufferedReader(
	    	                new InputStreamReader(conn.getInputStream()));
	    			StringBuilder sb = new StringBuilder();
	    	        char [] buf = new char[128];
	    	        while ( true ){
	    	        	int c = reader.read(buf);
	    	        	if(c == -1){ 
	    	        		break;
	    	        	}
	    	            sb.append(buf, 0, c);
	    	        }
	    	        reader.close();
	    			//Log.d("myLogs", sb.toString());
	    			return sb.toString();
	    		} catch (Exception e) {
	    			Log.e("myLogs", e.toString());
	    		}
	    		return null;
	    	}
	    }).start();
    	
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