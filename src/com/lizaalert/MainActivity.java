package com.lizaalert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	final static String TAG = "myLogs";
	Intent intent;
	ServiceConnection sConn;
	boolean bound;
	
	String load_url(String url){
		try {
	        URL u = new URL(url);
	        URLConnection conn = u.openConnection();
	        BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
	        char [] buf = new char[128];
	        while(true){
	        	int c = reader.read(buf);
	        	if(c <= 0){ 
        			break;
        		}
	        	sb.append(buf, 0, c);
	        }
	        reader.close();
			//Log.d("myLogs", sb.toString());
			return sb.toString();
		} catch (Exception e) {
			Log.e("myLogs", "Exception::load_url::" + e.toString());
	    	Log.d(TAG, e.getStackTrace().toString());
		}
		return null;
	}

	String load_xml(){
		return load_url("http://narod-fl.ru/lost_humans.xml");
	}

	public static final String md5(final String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuilder hexString = new StringBuilder();
	        for (byte aMessageDigest : messageDigest) {
	            String h = Integer.toHexString(0xFF & aMessageDigest);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (Exception e) {
	    	Log.d(TAG, "Exception::md5::" + e.toString());
	    	Log.d(TAG, e.getStackTrace().toString());
	    }
	    return "";
	}	
	
	String cache_photo(String photo_url){
		String md5 = md5(photo_url) + ".jpg";
    	Log.d(TAG, md5);
		File sdCardFile = new File(this.getExternalCacheDir(), md5);
		if(!sdCardFile.exists()){
			String img = load_url(photo_url);
			try {
				FileWriter fWriter = new FileWriter(sdCardFile, true);
				fWriter.write(img);
				fWriter.close();
			} catch (Exception e) {
		    	Log.d(TAG, "Exception::cache_photo::" + e.toString());
		    	Log.d(TAG, e.getStackTrace().toString());
			}
		}
		return md5;
	}
		
	
	void parse_xml(){
    	XmlPullParser parser = getResources().getXml(R.xml.lost_humans);
    	
    	// продолжаем, пока не достигнем конца документа
    	try {
			int event;
			Entry e = new Entry();
			while ((event = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
				String tag = "";
				switch(event){
					case XmlPullParser.START_TAG:
						tag = parser.getName();
						if(tag.equals("entry")){
							e = new Entry();
						}
						break;
					case XmlPullParser.END_TAG:
						tag = parser.getName();
						if(tag.equals("entry")){
							
						}
						break;
					case XmlPullParser.TEXT:
						String text = parser.getText();
						switch(tag){
							case "date":
								e.date = text;
								break;
							case "photo_url":
								e.photo_url = text;
								cache_photo(e.photo_url);								
								break;
							case "src_url":
								e.src_url = text;
								break;
							case "description":
								e.description = text;
								break;
						}
						break;
				}
			    parser.next();
/*    		
			    if (parser.getEventType() == XmlPullParser.START_TAG
			            && parser.getName().equals("contact")) {
			        list.add(parser.getAttributeValue(0) + " "
			                + parser.getAttributeValue(1) + "\n"
			                + parser.getAttributeValue(2));
			    }
*/    	    
			}
		} catch (Exception e) {
	    	Log.d(TAG, "Exception::parse_xml::" + e.toString());
	    	Log.d(TAG, e.getStackTrace().toString());
		}    	
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "MainActivity::onCreate");
		parse_xml();
		super.onCreate(savedInstanceState);
//		ImageView iv = new ImageView();
		
		setContentView(R.layout.activity_main);
//		startService(new Intent(this, SimpleWakefulService.class));
		startService(new Intent(this, MyService.class));
//		lostHumanNotify();
		TimeNotification.scheduleAlarms(this, 0);
//		SimpleWakefulReceiver.scheduleAlarms(this, 0);	//Start service
		//Toast.makeText(this, "" + Build.VERSION.SDK_INT, Toast.LENGTH_SHORT).show();
    	Log.d(TAG, "Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
    	//SharedPreferences.
    	intent = new Intent("com.LizaAlert.MyService");
		sConn = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder binder) {
				Log.d(TAG, "MainActivity onServiceConnected");
				bound = true;
			}
			 
			public void onServiceDisconnected(ComponentName name) {
				Log.d(TAG, "MainActivity onServiceDisconnected");
				bound = false;
			}
		};    	
	}

    @Override
    protected void onPause() {
    	Log.d(TAG, "MainActivity::onPause");
    	if (bound){
            unbindService(sConn);
            bound = false;
    	}
        super.onPause();
    }

    @Override
    protected void onResume() {
    	Log.d(TAG, "MainActivity::onResume");
        bindService(intent, sConn, BIND_AUTO_CREATE);        
        super.onResume();
    }
    
    @Override
    protected void onStop(){
    	Log.d(TAG, "MainActivity::onStop");
        super.onStop();
    }
    
    @Override
    protected void onDestroy(){
    	Log.d(TAG, "MainActivity::onDestroy");
    	super.onDestroy();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void lostHumanNotify() {
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, TimeNotification.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
		intent, PendingIntent.FLAG_CANCEL_CURRENT );
		// На случай, если мы ранее запускали активити, а потом поменяли время,
		// откажемся от уведомления
		am.cancel(pendingIntent);
		// Устанавливаем разовое напоминание
		am.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
	}
	
	public void refresh(View v) {
	    //Intent intent = new Intent(this, AboutActivity.class);
	    //startActivity(intent);
	}	
	
}
