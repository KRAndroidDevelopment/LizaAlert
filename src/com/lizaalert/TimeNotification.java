package com.lizaalert;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeNotification extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = (Notification) new Notification(R.drawable.ic_launcher, "������ �������", System.currentTimeMillis());
		//������ ��� ��������, ������� �� ����� ��������� ��� ������� �� �����������
		Intent intentTL = new Intent(context, MainActivity.class);
		//notification.setLatestEventInfo(context, "Test", "������ �������",
		//		PendingIntent.getActivity(context, 0, intentTL,	PendingIntent.FLAG_CANCEL_CURRENT));
		notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		nm.notify(1, notification);
		// ��������� ��������� �����������.
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
		intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY, pendingIntent);
	}

}