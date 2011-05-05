package com.monitoring.munin_node.plugins;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.monitoring.munin_node.plugin_api.Plugin_API;

public class forks implements Plugin_API {

	@Override
	public String getName() {
		return "Fork Rate";
	}

	@Override
	public String getCat() {
		return "Processes";
	}

	/*
	@Override
	public String getUpdate() {
		
	}*/

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
		StringBuffer output = new StringBuffer();
		output.append("graph_title Fork rate\n");
		output.append("graph_args --base 1000 -l 0 \n");
		output.append("graph_vlabel forks / ${graph_period}\n");
		output.append("graph_category processes\n");
		output.append("graph_info This graph shows the number of forks (new processes started) per second.\n");
		output.append("forks.label forks\n");
		output.append("forks.type DERIVE\n");
		output.append("forks.min 0\n");
		output.append("forks.max 100000\n");
		output.append("forks.info The number of forks per second.");
		StringBuffer statbuffer = new StringBuffer();

		try {
			BufferedReader in = new BufferedReader(new FileReader("/proc/stat"));
			String str;
			while ((str = in.readLine()) != null) {
				statbuffer.append(str+"\n");						
			}
			in.close();
		}
		catch (IOException e) {}
		Pattern proccesses_pattern = Pattern.compile("processes[\\s]+([\\d]+).*", Pattern.DOTALL);
		Matcher proccesses_matcher = proccesses_pattern.matcher(statbuffer.toString());
		proccesses_matcher.find();
		Bundle bundle = new Bundle();
		bundle.putString("name", this.getName());
		bundle.putString("config", output.toString());
		bundle.putString("update", "forks.value "+proccesses_matcher.group(1));
		Message msg = Message.obtain(handler, 42, bundle);
		handler.sendMessage(msg);
		return null;
	}

}
