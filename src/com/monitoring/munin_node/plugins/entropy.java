package com.monitoring.munin_node.plugins;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.monitoring.munin_node.plugin_api.Plugin_API;

public class entropy implements Plugin_API {

	@Override
	public String getName() {
		return "Entropy";
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
		output.append("graph_title Available entropy\n");
		output.append("graph_args --base 1000 -l 0\n");
		output.append("graph_vlabel entropy (bytes)\n");
		output.append("graph_scale no\n");
		output.append("graph_category system\n");
		output.append("graph_info This graph shows the amount of entropy available in the system.\n");
		output.append("entropy.label entropy\n");
		output.append("entropy.info The number of random bytes available. This is typically used by cryptographic applications.");
		BufferedReader in = null;
		String entropy = "";
		try {
			in = new BufferedReader(new FileReader("/proc/sys/kernel/random/entropy_avail"));
		} catch (FileNotFoundException e) {
			entropy = "U";
		}
		
		try {
			entropy = in.readLine();
		} catch (IOException e) {
			entropy = "U";
		}
		Bundle bundle = new Bundle();
		bundle.putString("name", this.getName());
		bundle.putString("config", output.toString());
		bundle.putString("update", "entropy.value "+entropy);
		Message msg = Message.obtain(handler, 42, bundle);
		handler.sendMessage(msg);
		return null;
	}

}
