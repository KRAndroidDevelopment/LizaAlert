package com.lizaalert;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lostHumanNotify();
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
