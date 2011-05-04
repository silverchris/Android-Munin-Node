package com.monitoring.munin_node.plugins;

import android.content.Context;
import android.os.SystemClock;

import com.monitoring.munin_node.plugin_api.Plugin_API;

public class android_uptime implements Plugin_API {

	@Override
	public String getName() {
		return "Android Uptime";
	}

	@Override
	public String getCat() {
		return "Android Phone";
	}

	@Override
	public String getConfig() {
		StringBuffer output = new StringBuffer();
		output.append("graph_title Android Uptime\n");
		output.append("graph_args --base 1000 -l 0\n");
		output.append("graph_scale no\n");
		output.append("graph_vlabel time in days\n");
		output.append("graph_category system\n");
		output.append("booted.label booted\n");
		output.append("booted.draw AREA\n");
		output.append("awake.label awake\n");
		output.append("awake.draw AREA");
		return output.toString();
	}

	@Override
	public String getUpdate() {
		Float booted = new Float((new Float(SystemClock.elapsedRealtime())/1000)/86400);
		Float awake = new Float((new Float(SystemClock.uptimeMillis())/1000)/86400);
		System.out.println(booted);
		System.out.println(awake);
		return "booted.value "+booted.toString()+"\nawake.value "+awake.toString();
	}

	@Override
	public Boolean needsContext() {
		return false;
	}

	@Override
	public Void setContext(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

}
