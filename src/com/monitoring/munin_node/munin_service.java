package com.monitoring.munin_node;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

public class munin_service extends Service{
	final int MUNIN_NOTIFICATION = 1;
    /*public munin_service() {
		super("Munin Node Service");
	}*/

    @Override
    public void onDestroy() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		mNotificationManager.cancel(MUNIN_NOTIFICATION);
    }
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wakeLock = pm.newWakeLock(pm.PARTIAL_WAKE_LOCK, "wakelock");
		wakeLock.acquire();
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.notification;
		CharSequence tickerText = "Munin Node Started";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		Context context = getApplicationContext();
		CharSequence contentTitle = "Munin Node";
		CharSequence contentText = "Just letting you know I am running";
		Intent notificationIntent = new Intent(this, munin_node.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		mNotificationManager.notify(MUNIN_NOTIFICATION, notification);
		
		final Update update = new Update(this); 
		update.doUpdate();
		mNotificationManager.cancel(MUNIN_NOTIFICATION);
		wakeLock.release();
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

    
}
