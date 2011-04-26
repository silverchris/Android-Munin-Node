package com.monitoring.munin_node;

import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

public class munin_service extends IntentService{
    public munin_service() {
		super("Munin Node Service");
		// TODO Auto-generated constructor stub
	}

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
    }
    
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
        SharedPreferences settings = getSharedPreferences("Munin_Node", 0);
        int Update_Interval = Integer.parseInt(settings.getString("Update_Interval", "10"));
		Timer timer = new Timer();
		final Update update = new Update(this); 
		TimerTask task = new TimerTask(){
			public void run(){
				update.doUpdate();
			}
		};
		timer.scheduleAtFixedRate(task, 0, 60000*Update_Interval);
	}

    
}
