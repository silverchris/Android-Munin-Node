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

public class interrupts implements Plugin_API {

	@Override
	public String getName() {
		return "Interrupts";
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
		output.append("graph_title Interrupts and context switches\n");
		output.append("graph_args --base 1000 -l 0\n");
		output.append("graph_vlabel interrupts & ctx switches / ${graph_period}\n");
		output.append("graph_category system\n");
		output.append("graph_info This graph shows the number of interrupts and context switches on the system. These are typically high on a busy system.\n");
		output.append("intr.info Interrupts are events that alter sequence of instructions executed by a processor. They can come from either hardware (exceptions, NMI, IRQ) or software.\n");
		output.append("ctx.info A context switch occurs when a multitasking operatings system suspends the currently running process, and starts executing another.\n");
		output.append("intr.label interrupts\n");
		output.append("ctx.label context switches\n");
		output.append("intr.type DERIVE\n");
		output.append("ctx.type DERIVE\n");
		output.append("intr.max 100000\n");
		output.append("ctx.max 100000\n");
		output.append("intr.min 0\n");
		output.append("ctx.min 0");
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
		final Pattern intr_pattern = Pattern.compile("intr[\\s]+([\\d]+).*ctxt[\\s]+([\\d]+).*", Pattern.DOTALL);
		Matcher intr_matcher = intr_pattern.matcher(statbuffer.toString());
		intr_matcher.find();
		String intr = intr_matcher.group(1);
		String ctxt = intr_matcher.group(2);
		Bundle bundle = new Bundle();
		bundle.putString("name", this.getName());
		bundle.putString("config", output.toString());
		bundle.putString("update", "intr.value "+intr+"\nctx.value "+ctxt);
		Message msg = Message.obtain(handler, 42, bundle);
		handler.sendMessage(msg);
		return null;
	}

}
