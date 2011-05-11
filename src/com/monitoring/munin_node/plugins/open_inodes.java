package com.monitoring.munin_node.plugins;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.monitoring.munin_node.plugin_api.Plugin_API;

public class open_inodes implements Plugin_API {

	@Override
	public String getName() {
		return "Inode Table Usage";
	}

	@Override
	public String getCat() {
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
		StringBuffer output = new StringBuffer();
		output.append("graph_title Inode table usage\n");
		output.append("graph_args --base 1000 -l 0\n");
		output.append("graph_vlabel number of open inodes\n");
		output.append("graph_category system\n");
		output.append("graph_info This graph monitors the Linux open inode table.\n");
		output.append("used.label open inodes\n");
		output.append("used.info The number of currently open inodes.\n");
		output.append("max.label inode table size\n");
		output.append("max.info The size of the system inode table. This is dynamically adjusted by the kernel.");
		BufferedReader in = null;
		String result = null;
		try {
			in = new BufferedReader(new FileReader("/proc/sys/fs/inode-nr"));
		} catch (FileNotFoundException e) {
			result = "used.value U";
		}
		try {
			String inode_nr = in.readLine();
			Pattern split_regex = Pattern.compile("\\s+");
			String[] items = split_regex.split(inode_nr);
			Long max = Long.parseLong(items[0]);
			Long used = max-Long.parseLong(items[1]);
			result = "used.value "+used.toString()+"\nmax.value "+max.toString();
		} catch (IOException e) {
			result = "used.value U";
		}
		Bundle bundle = new Bundle();
		bundle.putString("name", this.getName());
		bundle.putString("config", output.toString());
		bundle.putString("update", result);
		Message msg = Message.obtain(handler, 42, bundle);
		handler.sendMessage(msg);
		return null;
	}

}
