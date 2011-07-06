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

public class network_traffic implements Plugin_API {
	String[] Colours = {"00CC00", "0066B3", "FF8000", "FFCC00", "330099", "990099", "CCFF00", "FF0000", "808080", "008F00",
			"00487D", "B35A00", "B38F00", "6B006B", "8FB300", "B30000", "BEBEBE", "80FF80", "80C9FF", "FFC080", "FFE680", 
			"AA80FF", "EE00CC", "FF8080", "666600", "FFBFFF", "00FFCC", "CC6699", "999900"};
	@Override
	public String getName() {
		return "Network Traffic";
	}

	@Override
	public String getCat() {
		return "Network";
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
		StringBuffer transconfig = new StringBuffer();
		StringBuffer errorconfig = new StringBuffer();
		StringBuffer transvalue = new StringBuffer();
		StringBuffer errorvalue = new StringBuffer();
		transconfig.append("graph_order down up\n"); 
		transconfig.append("graph_title Network traffic\n");
		transconfig.append("graph_args --base 1000\n");
		transconfig.append("graph_vlabel bits in (-) / out (+) per ${graph_period}\n");
		transconfig.append("graph_category network\n");
		transconfig.append("graph_info This graph shows the traffic of the network interfaces. Please note that the traffic is shown in bits per second, not bytes. IMPORTANT: On 32 bit systems the data source for this plugin uses 32bit counters, which makes the plugin unreliable and unsuitable for most 100Mb (or faster) interfaces, where traffic is expected to exceed 50Mbps over a 5 minute period.  This means that this plugin is unsuitable for most 32 bit production environments. To avoid this problem, use the ip_ plugin instead.  There should be no problems on 64 bit systems running 64 bit kernels.\n");
		errorconfig.append("\nmultigraph Network_Errors\n");
		errorconfig.append("graph_order rcvd trans\n");
		errorconfig.append("graph_title Network errors\n");
		errorconfig.append("graph_args --base 1000\n");
		errorconfig.append("graph_vlabel packets in (-) / out (+) per ${graph_period}\n");
		errorconfig.append("graph_category network\n");
		errorconfig.append("graph_info This graph shows the amount of errors on the network interfaces.\n");
		Pattern network_pattern = Pattern.compile("([\\w\\d]+):\\s+([\\d]+)\\s+[\\d]+\\s+([\\d]+)\\s+[\\d]+\\s+\\s+[\\d]+\\s+[\\d]+\\s+[\\d]+\\s+[\\d]+\\s+([\\d]+)\\s+[\\d]+\\s+([\\d]+)\\s+");
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("/proc/net/dev"));
			String str;
			Integer count = 0;
			while ((str = in.readLine()) != null) {
				Matcher network_matcher = network_pattern.matcher(str);
				if(network_matcher.find()){
					transconfig.append(network_matcher.group(1)+"down.label "+network_matcher.group(1)+" received\n");
					transconfig.append(network_matcher.group(1)+"down.type COUNTER\n");
					transconfig.append(network_matcher.group(1)+"down.graph yes\n");
					transconfig.append(network_matcher.group(1)+"down.colour "+Colours[count]+"\n");
					transconfig.append(network_matcher.group(1)+"down.cdef "+network_matcher.group(1)+"down,-8,*\n");
					transconfig.append(network_matcher.group(1)+"up.label "+network_matcher.group(1)+" uploaded\n");
					transconfig.append(network_matcher.group(1)+"up.type COUNTER\n");
					transconfig.append(network_matcher.group(1)+"up.colour "+Colours[count]+"\n");
					//transconfig.append(network_matcher.group(1)+"up.negative eth0down\n");
					transconfig.append(network_matcher.group(1)+"up.cdef "+network_matcher.group(1)+"up,8,*\n");
					errorconfig.append(network_matcher.group(1)+"rcvd.label "+network_matcher.group(1)+"\n");
					errorconfig.append(network_matcher.group(1)+"rcvd.type COUNTER\n");
					errorconfig.append(network_matcher.group(1)+"rcvd.graph yes\n");
					errorconfig.append(network_matcher.group(1)+"rcvd.colour "+Colours[count]+"\n");
					errorconfig.append(network_matcher.group(1)+"rcvd.warning 1\n");
					errorconfig.append(network_matcher.group(1)+"trans.label "+network_matcher.group(1)+"\n");
					errorconfig.append(network_matcher.group(1)+"trans.type COUNTER\n");
					errorconfig.append(network_matcher.group(1)+"trans.colour "+Colours[count]+"\n");
					//errorconfig.append(network_matcher.group(1)+"trans.negative "+network_matcher.group(1)+"rcvd\n");
					errorconfig.append(network_matcher.group(1)+"trans.warning 1\n");
					transvalue.append(network_matcher.group(1)+"down.value "+network_matcher.group(2)+"\n");
					transvalue.append(network_matcher.group(1)+"up.value "+network_matcher.group(4)+"\n");
					errorvalue.append(network_matcher.group(1)+"rcvd.value "+network_matcher.group(3)+"\n");
					errorvalue.append(network_matcher.group(1)+"trans.value "+network_matcher.group(5)+"\n");
					count++;
					}
			}
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		}
		Bundle bundle = new Bundle();
		bundle.putString("name", this.getName());
		//bundle.putString("config", transconfig.toString());
		//bundle.putString("update", transvalue.toString());
		bundle.putString("config", transconfig.toString()+errorconfig.toString());
		bundle.putString("update", transvalue.toString()+"\nmultigraph Network_Errors\n"+errorvalue.toString());
		Message msg = Message.obtain(handler, 42, bundle);
		handler.sendMessage(msg);
		return null;
	}

}
