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
		output.append("time.label Service\n");
		output.append("time.draw LINE\n");
		output.append("time.info Time Spent running the munin service\n");
		output.append("plugin.label Plugin\n");
		output.append("plugin.draw LINE\n");
		output.append("plugin.info Time Spent running the munin plugins\n");
		output.append("upload.label Upload\n");
		output.append("upload.draw LINE\n");
		output.append("upload.info Time Spent uploading munin data\n");
		Bundle bundle = new Bundle();
		bundle.putString("name", this.getName());
		bundle.putString("config", output.toString());
		StringBuffer update = new StringBuffer();
		if(processing_time < 0){
			update.append("time.value U\n");
		}
		else{
			update.append("time.value "+processing_time.toString()+"\n");
		}
		Long plugin = settings.getLong("new_plugin_end_time", 0)-settings.getLong("plugin_start_time", 0);
        editor.putLong("plugin_start_time", settings.getLong("new_plugin_start_time", 0));
        editor.putLong("plugin_end_time", settings.getLong("new_plugin_end_time", 0));
		if(plugin < 0){
			update.append("plugin.value U\n");
		}
		else{
			update.append("plugin.value "+plugin.toString()+"\n");
		}
		Long upload = settings.getLong("upload_end_time", 0)-settings.getLong("upload_start_time", 0);
        editor.putLong("upload_start_time", settings.getLong("new_upload_start_time", 0));
        editor.putLong("upload_end_time", settings.getLong("new_upload_end_time", 0));
		if(upload < 0){
			update.append("upload.value U\n");
		}
		else{
			update.append("upload.value "+upload.toString()+"\n");
		}
		editor.commit();
		bundle.putString("update", update.toString());
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
