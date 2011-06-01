package com.monitoring.munin_node;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class munin_receiver extends BroadcastReceiver {
	Intent service = null;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(service == null){
			service = new Intent(context, munin_service.class);  
		}
        context.startService(service) ;
        System.out.println("Receiver Ending");
	}

}
