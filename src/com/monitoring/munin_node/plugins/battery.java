package com.monitoring.munin_node.plugins;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.monitoring.munin_node.plugin_api.Plugin_API;

public class battery implements Plugin_API {
	ContextWrapper context = null;
	@Override
	public String getName() {
		return "Battery";
	}

	@Override
	public String getCat() {
		return "Android Phone";
	}
	public Boolean needsContext() {
		return true;
	}
	public Void setContext(Context newcontext) {
		context = new ContextWrapper(newcontext);
		return null;
	}
	
	@Override
	public Void run(Handler handler) {
		StringBuffer output = new StringBuffer();
		output.append("graph_title Battery Charge\n");
		output.append("graph_args --upper-limit 100 -l 0\n");
		output.append("graph_scale no\n");
		output.append("graph_vlabel %\n");
		output.append("graph_category Android Phone\n");
		output.append("graph_info This graph shows battery charge in %\n");
		output.append("battery.label Battery Charge\n");
		output.append("charge.label Charging\n");
		output.append("multigraph Battery_Temp\n");
		output.append("graph_title Battery Temp\n");
		output.append("graph_vlabel Temp\n");
		output.append("graph_category Android Phone\n");
		output.append("graph_info This graph shows battery Temp\n");
		output.append("temp.label Battery Temperature\n");
		output.append("multigraph Battery_Volt\n");
		output.append("graph_title Battery Voltage\n");
		output.append("graph_vlabel Voltage\n");
		output.append("graph_category Android Phone\n");
		output.append("graph_info This graph shows battery Voltage\n");
		output.append("volt.label Battery Voltage");
		StringBuffer output2 = new StringBuffer();
		Intent Battery = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		Float Battery_unscaled = new Float(Battery.getIntExtra("level", 0));
		Float Battery_scale = new Float(Battery.getIntExtra("scale", 0));
		Float Battery_value = (Battery_unscaled/Battery_scale)*100;
		output2.append("battery.value "+Battery_value.toString()+"\n");
		//output.append(Battery_scale.toString()+"\n");
		if(Battery.getIntExtra("plugged", 0)>0){
			output2.append("charge.value 100\n");
		}
		else{
			output2.append("charge.value 0\n");
		}
		output2.append("multigraph Battery_Temp\n");
		Float temp = new Float(Battery.getIntExtra("voltage", 0)/100);
		output2.append("temp.value "+temp.toString()+"\n");
		output2.append("multigraph Battery_Volt\n");
		Float volt = new Float(Battery.getIntExtra("temperature",0)/100);
		output2.append("volt.value "+volt);
		Bundle bundle = new Bundle();
		bundle.putString("name", this.getName());
		bundle.putString("config", output.toString());
		bundle.putString("update", output2.toString());
		Message msg = Message.obtain(handler, 42, bundle);
		handler.sendMessage(msg);
		output = null;
		output2 = null;
		Battery_unscaled = null;
		Battery_scale = null;
		Battery_value = null;
		temp = null;
		volt = null;
		return null;
	}
}
