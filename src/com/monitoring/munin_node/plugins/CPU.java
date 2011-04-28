package com.monitoring.munin_node.plugins;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.*;

import android.content.Context;

import com.monitoring.munin_node.plugin_api.Plugin_API;

//TODO cleanup new lines so that there are no extras throught the output. 
public class CPU implements Plugin_API{
	public String getName(){
		return "CPU";
	}
	public String getCat(){
		return "System";
	}
	public String getConfig(){
		StringBuffer statbuffer = new StringBuffer();

		try {
			BufferedReader in = new BufferedReader(new FileReader("/proc/stat"));
			String str;
			while ((str = in.readLine()) != null) {
				statbuffer.append(str);						
			}
			in.close();
		}
		catch (IOException e) {}
		Pattern extinfo_regex = Pattern.compile("^cpu +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+");
		Matcher match1 = extinfo_regex.matcher(statbuffer.toString());
		boolean extinfo = false;
		while (match1.find()) {
			extinfo = true;
		}
		Pattern extextinfo_regex = Pattern.compile("^cpu +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+");
		Matcher match2 = extextinfo_regex.matcher(statbuffer.toString());
		boolean extextinfo = false;
		while (match2.find()) {
			extextinfo = true;
		}
		StringBuffer output = new StringBuffer();
		Pattern cpunumber_regex = Pattern.compile("cpu[0-9]+");
		Matcher match = cpunumber_regex.matcher(statbuffer.toString());
		int NCPU = 0;
		while (match.find()){
			NCPU++;
		}
		int graphlimit = NCPU*100;
		output.append("graph_title CPU usage\n");
		output.append("graph_order system user nice idle");
		if(extinfo == true){
			output.append(" iowait irq softirq\n");
		}
		else{
			output.append("\n");
		}
		output.append("graph_args --base 1000 -r --lower-limit 0 --upper-limit "+graphlimit+"\n");
		output.append("graph_vlabel %\n");
		output.append("graph_scale no\n");
		output.append("graph_info This graph shows how CPU time is spent.\n");
		output.append("graph_category system\n");
		output.append("graph_period second\n");
		output.append("system.label system\n");
		output.append("system.draw AREA\n");
		output.append("system.min 0\n");
		output.append("system.type DERIVE\n");
		output.append("system.info CPU time spent by the kernel in system activities\n");
		output.append("user.label user\n");
		output.append("user.draw STACK\n");
		output.append("user.min 0\n");
		output.append("user.type DERIVE\n");
		output.append("user.info CPU time spent by normal programs and daemons\n");
		output.append("nice.label nice\n");
		output.append("nice.draw STACK\n");
		output.append("nice.min 0\n");
		output.append("nice.type DERIVE\n");
		output.append("nice.info CPU time spent by nice(1)d programs\n");
		output.append("idle.label idle\n");
		output.append("idle.draw STACK\n");
		output.append("idle.min 0\n");
		output.append("idle.type DERIVE\n");
		output.append("idle.info Idle CPU time");
		if(extinfo == true){
			output.append("\niowait.label iowait\n");
			output.append("iowait.draw STACK\n");
			output.append("iowait.min 0\n");
			output.append("iowait.type DERIVE\n");
			output.append("iowait.info CPU time spent waiting for I/O operations to finish when there is nothing else to do.\n");
			output.append("irq.label irq\n");
			output.append("irq.draw STACK\n");
			output.append("irq.min 0\n");
			output.append("irq.type DERIVE\n");
			output.append("irq.info CPU time spent handling interrupts\n");
			output.append("softirq.label softirq\n");
			output.append("softirq.draw STACK\n");
			output.append("softirq.min 0\n");
			output.append("softirq.type DERIVE\n");
			output.append("softirq.info CPU time spent handling batched interrupts");
		}
		if (extextinfo == true){
			output.append("\nsteal.label steal\n");
			output.append("steal.draw STACK\n");
			output.append("steal.min 0\n");
			output.append("steal.type DERIVE\n");
			output.append("steal.info The time that a virtual CPU had runnable tasks, but the virtual CPU itself was not running");
		}
		return output.toString();
	}
	public String getUpdate(){
		StringBuffer statbuffer = new StringBuffer();

		try {
			BufferedReader in = new BufferedReader(new FileReader("/proc/stat"));
			String str;
			while ((str = in.readLine()) != null) {
				statbuffer.append(str);						
			}
			in.close();
		}
		catch (IOException e) {}
		Pattern extinfo_regex = Pattern.compile("^cpu +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+");
		Matcher match1 = extinfo_regex.matcher(statbuffer.toString());
		boolean extinfo = false;
		while (match1.find()) {
			extinfo = true;
		}
		Pattern extextinfo_regex = Pattern.compile("^cpu +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+");
		Matcher match2 = extextinfo_regex.matcher(statbuffer.toString());
		boolean extextinfo = false;
		while (match2.find()) {
			extextinfo = true;
		}
		StringBuffer output = new StringBuffer();
		Pattern split_regex = Pattern.compile("\\s+");
		String[] items = split_regex.split(statbuffer.toString());
		output.append("user.value "+items[1]+"\n");
		output.append("nice.value "+items[2]+"\n");
		output.append("system.value "+items[3]+"\n");
		output.append("idle.value "+items[4]);
		if(extinfo == true){
			output.append("\niowait.value "+items[5]+"\n");
			output.append("irq.value "+items[6]+"\n");
			output.append("softirq.value "+items[7]);
		}
		if(extextinfo == true){
			output.append("\nsteal.value "+items[8]);
		}
		return output.toString();
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
