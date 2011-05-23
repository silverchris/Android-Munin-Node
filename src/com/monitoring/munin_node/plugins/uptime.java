package com.monitoring.munin_node.plugins;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.monitoring.munin_node.plugin_api.Plugin_API;

public class uptime implements Plugin_API{
	static final String output = "graph_title Uptime\ngraph_args --base 1000 -l 0\ngraph_scale no\ngraph_vlabel uptime in days\ngraph_category system\nuptime.label uptime\nuptime.draw AREA";
	public String getName(){
		return "uptime";
	}
	public String getCat(){
		return "System";
	}
	@Override
	public Boolean needsContext() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Void setContext(Context context) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Void run(Handler handler) {
		String uptimestring = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader("/proc/uptime"));
			uptimestring = in.readLine();
			in.close();
		}
		catch (IOException e) {}
		Pattern split_regex = Pattern.compile("\\s+");
		String[] items = split_regex.split(uptimestring.toString());
		Float uptime = Float.parseFloat(items[0])/86400;
		Bundle bundle = new Bundle();
		bundle.putString("name", this.getName());
		bundle.putString("config", output);
		bundle.putString("update", "uptime.value "+uptime.toString());
		Message msg = Message.obtain(handler, 42, bundle);
		handler.sendMessage(msg);
		return null;
	}

}
