package com.monitoring.munin_node.plugins;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.monitoring.munin_node.plugin_api.Plugin_API;

public class irqstats implements Plugin_API {
	Map<String, String[]> irqinfo = new HashMap<String, String[]>();

	@Override
	public String getName() {
		return "IRQstats";
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
		try {
			BufferedReader in = new BufferedReader(new FileReader("/proc/interrupts"));
			String str;
			str = in.readLine();
			Pattern cpu_pattern = Pattern.compile("CPU\\d+");
			Matcher cpu_matcher = cpu_pattern.matcher(str);
			int cpu_count = 0;
			while (cpu_matcher.find()){
				cpu_count++;
			}
			StringBuffer pattern = new StringBuffer();
			pattern.append("([\\w\\d]+):[\\s]+");
			for(int i = 0;i < cpu_count;i++){
				pattern.append("([\\d]+)[\\s]+");
			}
			pattern.append("([\\w\\s-:,]+)");
			Pattern line_match = Pattern.compile(pattern.toString());
			while ((str = in.readLine()) != null) {
				Matcher line_matcher = line_match.matcher(str);
				if(line_matcher.find()){
					if(cpu_count > 1){
						//TODO handle multiple cpus
						System.out.println("This Should be handled eventually");
					}
					String[] temp = {line_matcher.group(2),line_matcher.group(3)};
					irqinfo.put(line_matcher.group(1), temp);
				}
			}
			in.close();
		}
		catch (IOException e) {}
		StringBuffer output = new StringBuffer();
		output.append("graph_title Individual interrupts\n");
		output.append("graph_args --base 1000 -l 0\ngraph_vlabel interrupts / ${graph_period}\ngraph_category system\n");
		//TODO Fix Graph order
		for (Map.Entry<String, String[]> entry : irqinfo.entrySet()) {
			output.append("i"+entry.getKey()+".label "+entry.getValue()[1]);
			output.append("\ni"+entry.getKey()+".info Interrupt "+entry.getKey()+", for devices(s): "+entry.getValue()[1]);
			output.append("\ni"+entry.getKey()+".type DERIVE");
			output.append("\ni"+entry.getKey()+".min 0\n");
		}
		StringBuffer output2 = new StringBuffer();
		for (Map.Entry<String, String[]> entry : irqinfo.entrySet()) {
			output2.append("\ni"+entry.getKey()+".value "+entry.getValue()[0]);
		}
		Bundle bundle = new Bundle();
		bundle.putString("name", this.getName());
		bundle.putString("config", output.toString());
		bundle.putString("update", output2.toString());
		Message msg = Message.obtain(handler, 42, bundle);
		handler.sendMessage(msg);
		return null;
	}

}
