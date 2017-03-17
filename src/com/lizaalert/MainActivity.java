package com.lizaalert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class MainActivity extends Activity implements OnTouchListener {
	final static String TAG = "myLogs";
	// это будет именем файла настроек
	public static final String APP_PREFERENCES = "mysettings"; 	Intent intent;
	public static final String APP_PREFERENCES_LAST_ID = "last_id";

	public SharedPreferences mySharedPreferences;
	
	ServiceConnection sConn;
	boolean bound;

    private static final float MOVE_LENGTH =150;
	private ViewFlipper flipper = null;
    private float fromPosition;
	boolean flag_slide = true;
	int flipper_index = 0;
	public List<LostHumanItem> lost_humans = new ArrayList<LostHumanItem>();
	
    public boolean onTouch(View view, MotionEvent event){
    	//EditText et;
    	//et.setAutoLinkMask(android.text.util.Linkify.ALL);
    	//et.setText(url);
        switch (event.getAction()){
/*        
        case MotionEvent.ACTION_DOWN:
            fromPosition = event.getX();
            break;
        case MotionEvent.ACTION_UP:
            float toPosition = event.getX();
            if (fromPosition > toPosition){
                flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.go_next_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.go_next_out));
                flipper.showNext();
            }else if (fromPosition < toPosition){
                flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.go_prev_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.go_prev_out));
                flipper.showPrevious();
            }
*/            
        case MotionEvent.ACTION_DOWN:
            fromPosition = event.getX();
            break;
    	// ¬место ACTION_UP
	    case MotionEvent.ACTION_MOVE:
	    	if(!flag_slide){
	    		break;
	    	}
	        float toPosition = event.getX();
	        // MOVE_LENGTH - рассто€ние по оси X, после которого можно переходить на след. экран
	        // ¬ моем тестовом примере MOVE_LENGTH = 150
	        if ((fromPosition - MOVE_LENGTH) > toPosition){
	    		flag_slide = false;
	//        	fromPosition = toPosition;
	            flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.go_next_in));
	            flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.go_next_out));
	            flipper.showNext();
	            flipper_index++;
	        }else if ((fromPosition + MOVE_LENGTH) < toPosition){
	    		flag_slide = false;
	//        	fromPosition = toPosition;
	            flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.go_prev_in));
	            flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.go_prev_out));
	            flipper.showPrevious();
	            if(flipper_index > 0){
	            	flipper_index--;
	            }
	        }  
	        break;
    	case MotionEvent.ACTION_UP:
    		flag_slide = true;
	        break;
        default:
            break;
        }
        return true;
    }
	
	
/*
	String load_xml(){
		return load_url("http://narod-fl.ru/lost_humans/lost_humans.php");
	}
*/
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "MainActivity::onCreate");
		//parse_xml();
		super.onCreate(savedInstanceState);
//		ImageView iv = new ImageView();
		
		setContentView(R.layout.activity_main);
		
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        mainLayout.setOnTouchListener(this);
        
        flipper = (ViewFlipper) findViewById(R.id.flipper);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        flipper.addView(inflater.inflate(R.layout.first, null));
		
//		startService(new Intent(this, SimpleWakefulService.class));
		startService(new Intent(this, MyService.class));
//		lostHumanNotify();
		TimeNotification.scheduleAlarms(this, 0);
//		SimpleWakefulReceiver.scheduleAlarms(this, 0);	//Start service
		//Toast.makeText(this, "" + Build.VERSION.SDK_INT, Toast.LENGTH_SHORT).show();
    	Log.d(TAG, "Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
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
		
		mySharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
		//Save settings
		//Editor editor = mySharedPreferences.edit();
		//editor.putInt(key, value)
		//editor.putString(APP_PREFERENCES_LAST_ID, strNickName);
		//editor.apply();

		//Load settings
		//if(mySharedPreferences.contains(APP_PREFERENCES_LAST_ID)) {
		//	mySharedPreferences.getString(APP_PREFERENCES_LAST_ID, "");
		//}	
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
		// Ќа случай, если мы ранее запускали активити, а потом помен€ли врем€,
		// откажемс€ от уведомлени€
		am.cancel(pendingIntent);
		// ”станавливаем разовое напоминание
		am.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
	}

	public void refresh(View v) {
		Log.d(TAG, "refresh");
		new XML_Downloader(this).execute();
		Log.d(TAG, "/refresh");
/*		
	    //Intent intent = new Intent(this, AboutActivity.class);
	    //startActivity(intent);
		
		
		//ImageView bmImage;
		//Bitmap result;
		//InputStream in = new java.net.URL(url).openStream()
		//result = BitmapFactory.decodeStream(in);
		//bmImage.setImageBitmap(result);
		XmlPullParser parser = LoadXML();
		if(parser == null){
			Log.d(TAG, "parser == null");
			return;
		}
		parse_xml(parser);
		flipper_index = 0;
		LostHumanItem i = lost_humans.get(0);
		Bitmap result = BitmapFactory.decodeFile(i.photo_file);
		ImageView iv = (ImageView) findViewById(R.id.imageView1);
		iv.setImageBitmap(result);
		Log.d(TAG, "/refresh");
*/		
	}	
	
}
