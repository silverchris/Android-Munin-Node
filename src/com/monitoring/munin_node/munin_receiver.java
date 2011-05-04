package com.monitoring.munin_node;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class munin_receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("lol alarm");
		Intent service =new Intent(context, munin_service.class);  
        context.startService(service) ;
		//Update update = new Update(context);
		//update.doUpdate();
	}

}
