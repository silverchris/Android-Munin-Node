package com.monitoring.munin_node.plugins;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.monitoring.munin_node.plugin_api.Plugin_API;

public class fw_packets implements Plugin_API {

	@Override
	public String getName() {
		return "Firewall Packets";
	}

	@Override
	public String getCat() {
		return "Network";
	}

	/*@Override
	public String getConfig() {
		
		return output.toString();
	}

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
		output.append("graph_title Firewall Throughput\n");
		output.append("graph_args --base 1000 -l 0\n");
		output.append("graph_vlabel Packets/${graph_period}\n");
		output.append("graph_category network\n");
		output.append("received.label Received\n");
		output.append("received.draw AREA\n");
		output.append("received.type DERIVE\n");
		output.append("received.min 0\n");
		output.append("forwarded.label Forwarded\n");
		output.append("forwarded.draw LINE2\n");
		output.append("forwarded.type DERIVE\n");
		output.append("forwarded.min 0");
		BufferedReader in = null;
		String received = "";
		String forwarded = "";
		try {
			in = new BufferedReader(new FileReader("/proc/net/snmp"));
			in.readLine();
			String ipline =  in.readLine();
			final Pattern ip = Pattern.compile("^Ip:[\\s]+[\\d]+[\\s]+[\\d]+[\\s]+([\\d]+)[\\s]+[\\d]+[\\s]+[\\d]+[\\s]+([\\d])+.*");
			Matcher ip_matcher = ip.matcher(ipline);
			if(ip_matcher.find()){
				received = ip_matcher.group(1);
				forwarded = ip_matcher.group(2);
			}
		} catch (FileNotFoundException e) {
			received = "U";
			forwarded = "U";
		} catch (IOException e) {
			received = "U";
			forwarded = "U";
		}
		Bundle bundle = new Bundle();
		bundle.putString("name", this.getName());
		bundle.putString("config", output.toString());
		bundle.putString("update", "received.value "+received+"\nforwarded.value "+forwarded);
		Message msg = Message.obtain(handler, 42, bundle);
		handler.sendMessage(msg);
		return null;
	}

}
