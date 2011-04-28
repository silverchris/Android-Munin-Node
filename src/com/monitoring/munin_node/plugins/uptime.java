package com.monitoring.munin_node.plugins;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.*;

import android.content.Context;

import com.monitoring.munin_node.plugin_api.Plugin_API;

public class uptime implements Plugin_API{
	public String getName(){
		return "uptime";
	}
	public String getCat(){
		return "System";
	}
	public String getConfig(){
		StringBuffer output = new StringBuffer();
		output.append("graph_title Uptime\n");
		output.append("graph_args --base 1000 -l 0\n");
		output.append("graph_scale no\n");
		output.append("graph_vlabel uptime in days\n");
		output.append("graph_category system\n");
		output.append("uptime.label uptime\n");
		output.append("uptime.draw AREA");
		return output.toString();
	}
	public String getUpdate(){
		StringBuffer statbuffer = new StringBuffer();

		try {
			BufferedReader in = new BufferedReader(new FileReader("/proc/uptime"));
			String str;
			while ((str = in.readLine()) != null) {
				statbuffer.append(str);						
			}
			in.close();
		}
		catch (IOException e) {}
		Pattern split_regex = Pattern.compile("\\s+");
		String[] items = split_regex.split(statbuffer.toString());
		Float uptime = Float.parseFloat(items[0])/86400;
		return "uptime.value "+uptime.toString();
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

}
