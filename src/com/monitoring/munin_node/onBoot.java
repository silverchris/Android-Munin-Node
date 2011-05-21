package com.monitoring.munin_node;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class onBoot extends BroadcastReceiver {
	  @Override
	  public void onReceive(Context context, Intent arg1) {
		  SharedPreferences settings = context.getSharedPreferences("Munin_Node", 0);
	      if(settings.getBoolean("onBoot", false) == true){
	    	  Intent service = new Intent(context, munin_receiver.class);
	    	  PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1234567, service, 0);
	    	  AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	          int Update_Interval = Integer.parseInt(settings.getString("Update_Interval", "10"));
	    	  alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, 60000*Update_Interval, pendingIntent);
	      }
	  }
	}