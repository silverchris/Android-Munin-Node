package com.monitoring.munin_node.plugins;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.monitoring.munin_node.plugin_api.Plugin_API;

public class network_traffic implements Plugin_API {

	@Override
	public String getName() {
		return "Network Traffic";
	}

	@Override
	public String getCat() {
		return "Network";
	}

	@Override
	public String getConfig() {
		StringBuffer output = new StringBuffer();
		StringBuffer output2 = new StringBuffer();
		output.append("graph_order down up\n"); 
		output.append("graph_title Network traffic\n");
		output.append("graph_args --base 1000\n");
		output.append("graph_vlabel bits in (-) / out (+) per ${graph_period}\n");
		output.append("graph_category network\n");
		output.append("graph_info This graph shows the traffic of the network interfaces. Please note that the traffic is shown in bits per second, not bytes. IMPORTANT: On 32 bit systems the data source for this plugin uses 32bit counters, which makes the plugin unreliable and unsuitable for most 100Mb (or faster) interfaces, where traffic is expected to exceed 50Mbps over a 5 minute period.  This means that this plugin is unsuitable for most 32 bit production environments. To avoid this problem, use the ip_ plugin instead.  There should be no problems on 64 bit systems running 64 bit kernels.\n");
		output2.append("\nmultigraph Network_Errors\n");
		output2.append("graph_order rcvd trans\n");
		output2.append("graph_title Network errors\n");
		output2.append("graph_args --base 1000\n");
		output2.append("graph_vlabel packets in (-) / out (+) per ${graph_period}\n");
		output2.append("graph_category network\n");
		output2.append("graph_info This graph shows the amount of errors on the network interfaces.\n");
		Pattern network_pattern = Pattern.compile("([\\w\\d]+):\\s+([\\d]+)\\s+[\\d]+\\s+([\\d]+)\\s+[\\d]+\\s+\\s+[\\d]+\\s+[\\d]+\\s+[\\d]+\\s+[\\d]+\\s+([\\d]+)\\s+[\\d]+\\s+([\\d]+)\\s+");
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("/proc/net/dev"));
			String str;
			while ((str = in.readLine()) != null) {
				Matcher network_matcher = network_pattern.matcher(str);
				if(network_matcher.find()){
					output.append(network_matcher.group(1)+"down.label received\n");
					output.append(network_matcher.group(1)+"down.type COUNTER\n");
					output.append(network_matcher.group(1)+"down.graph no\n");
					output.append(network_matcher.group(1)+"down.cdef "+network_matcher.group(1)+"down,8,*\n");
					output.append(network_matcher.group(1)+"up.label bps\n");
					output.append(network_matcher.group(1)+"up.type COUNTER\n");
					output.append(network_matcher.group(1)+"up.negative "+network_matcher.group(1)+"down\n");
					output.append(network_matcher.group(1)+"up.cdef "+network_matcher.group(1)+"up,8,*\n");
					output2.append(network_matcher.group(1)+"rcvd.label packets\n");
					output2.append(network_matcher.group(1)+"rcvd.type COUNTER\n");
					output2.append(network_matcher.group(1)+"rcvd.graph no\n");
					output2.append(network_matcher.group(1)+"rcvd.warning 1\n");
					output2.append(network_matcher.group(1)+"trans.label packets\n");
					output2.append(network_matcher.group(1)+"trans.type COUNTER\n");
					output2.append(network_matcher.group(1)+"trans.negative "+network_matcher.group(1)+"rcvd\n");
					output2.append(network_matcher.group(1)+"trans.warning 1\n");
					}
			}
		} catch (FileNotFoundException e) {
			return "";
		} catch (IOException e) {
			return "";
		}
		return output.toString()+output2.toString();
	}

	@Override
	public String getUpdate() {
		StringBuffer output = new StringBuffer();
		StringBuffer output2 = new StringBuffer();
		output2.append("\nmultigraph Network_Errors\n");
		Pattern network_pattern = Pattern.compile("([\\w\\d]+):\\s+([\\d]+)\\s+[\\d]+\\s+([\\d]+)\\s+[\\d]+\\s+\\s+[\\d]+\\s+[\\d]+\\s+[\\d]+\\s+[\\d]+\\s+([\\d]+)\\s+[\\d]+\\s+([\\d]+)\\s+");
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("/proc/net/dev"));
			String str;
			while ((str = in.readLine()) != null) {
				Matcher network_matcher = network_pattern.matcher(str);
				if(network_matcher.find()){
					output.append(network_matcher.group(1)+"down.value "+network_matcher.group(2)+"\n");
					output.append(network_matcher.group(1)+"up.value "+network_matcher.group(4)+"\n");
					output2.append(network_matcher.group(1)+"rcvd.value "+network_matcher.group(3)+"\n");
					output2.append(network_matcher.group(1)+"trans.value "+network_matcher.group(5)+"\n");
					}
			}
		} catch (FileNotFoundException e) {
			return "";
		} catch (IOException e) {
			return "";
		}
		return output.toString()+output2.toString();
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
