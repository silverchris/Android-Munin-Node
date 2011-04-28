package com.monitoring.munin_node.plugins;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;

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
	public String getConfig() {
		StringBuffer output = new StringBuffer();
		output.append("graph_title Battery Charge");
		output.append("graph_args --upper-limit 100 -l 0");
		output.append("graph_scale no");
		output.append("graph_vlabel %");
		output.append("graph_category Android Phone");
		output.append("graph_info This graph shows battery charge in %");
		output.append("battery.label Battery Charge");
		return output.toString();
	}
	    
	@Override
	public String getUpdate() {
		System.out.println(context);
		Intent Battery = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		System.out.println(Battery.getIntExtra("level", 0));
		System.out.println(Battery.getIntExtra("level_scale", 0));
		return "";
	}

}
