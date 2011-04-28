package com.monitoring.munin_node.plugins;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import android.content.Context;

import com.monitoring.munin_node.plugin_api.Plugin_API;

public class load implements Plugin_API {

	@Override
	public String getName() {
		return "Load";
	}

	@Override
	public String getCat() {
		return "System";
	}

	@Override
	public String getConfig() {
		StringBuffer output = new StringBuffer();
		output.append("graph_title Load average\n");
		output.append("graph_args --base 1000 -l 0\n");
		output.append("graph_vlabel load\n");
		output.append("graph_scale no\n");
		output.append("graph_category system\n");
		output.append("load.label load\n");
		output.append("graph_info The load average of the machine describes how many processes are in the run-queue (scheduled to run immediately).\n");
		output.append("load.info 5 minute load average");
		return output.toString();
	}

	@Override
	public String getUpdate() {
		StringBuffer loadbuffer = new StringBuffer();

		try {
			BufferedReader in = new BufferedReader(new FileReader("/proc/loadavg"));
			String str;
			while ((str = in.readLine()) != null) {
				loadbuffer.append(str);						
			}
			in.close();
		}
		catch (IOException e) {}
		Pattern split_regex = Pattern.compile("\\s+");
		String[] items = split_regex.split(loadbuffer.toString());
		return "load.value "+items[1];
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
