package com.monitoring.munin_node.plugins;

import android.content.Context;
import android.os.Handler;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Message;


import com.monitoring.munin_node.plugin_api.Plugin_API;

public class munin_time implements Plugin_API {
	ContextWrapper context = null;
	
	@Override
	public String getName() {
		return "Munin Time";
	}

	@Override
	public String getCat() {
		return "Munin";
	}

	@Override
	public Void run(Handler handler) {
        final SharedPreferences settings = context.getSharedPreferences("Munin_Node", 0);
        Long start = settings.getLong("start_time", 0);
        Long end = settings.getLong("end_time", 0);
        final Editor editor = settings.edit();
        editor.putLong("start_time", settings.getLong("new_start_time", 0));
        editor.commit();
        Long processing_time = end-start;
		StringBuffer output = new StringBuffer();
		output.append("graph_title Munin Processing Time\n");
		output.append("graph_vlabel ms\n");
		output.append("graph_info This graph shows how much time is spent running the munin service, its data is delayed by one run.\n");
		output.append("graph_category munin\n");
		output.append("graph_scale no\n");
		output.append("time.label Time\n");
		output.append("time.draw LINE\n");
		output.append("time.info Time Spent running the munin service\n");
		Bundle bundle = new Bundle();
		bundle.putString("name", this.getName());
		bundle.putString("config", output.toString());
		bundle.putString("update", "time.value "+processing_time.toString());
		Message msg = Message.obtain(handler, 42, bundle);
		handler.sendMessage(msg);
		return null;
	}

	@Override
	public Boolean needsContext() {
		return true;
	}

	@Override
	public Void setContext(Context newcontext) {
		context = new ContextWrapper(newcontext);
		return null;
	}

}
