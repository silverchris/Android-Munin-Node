package com.monitoring.munin_node;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class munin_service extends Service{
	final int MUNIN_NOTIFICATION = 1;
	Timer timer;
    /*public munin_service() {
		super("Munin Node Service");
	}*/

    @Override
    public void onDestroy() {
    	timer.cancel();
		//String ns = Context.NOTIFICATION_SERVICE;
		//NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		//mNotificationManager.cancel(MUNIN_NOTIFICATION);
        SharedPreferences settings = getSharedPreferences("Munin_Node", 0);
        SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("Service_Running", false);
		editor.commit();
		stopForeground(true);
    }
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//String ns = Context.NOTIFICATION_SERVICE;
		//NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
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
		//mNotificationManager.notify(MUNIN_NOTIFICATION, notification);
		startForeground(MUNIN_NOTIFICATION, notification);
				
        SharedPreferences settings = getSharedPreferences("Munin_Node", 0);
        int Update_Interval = Integer.parseInt(settings.getString("Update_Interval", "10"));
		timer = new Timer();
		final Update update = new Update(this); 
		TimerTask task = new TimerTask(){
			public void run(){
				update.doUpdate();
			}
		};
		timer.scheduleAtFixedRate(task, 0, 60000*Update_Interval);
        SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("Service_Running", true);
		editor.commit();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

    
}
