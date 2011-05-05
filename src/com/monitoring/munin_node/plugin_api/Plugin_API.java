package com.monitoring.munin_node.plugin_api;
import android.content.Context;
import android.os.Handler;

public interface Plugin_API{
	String getName();
	String getCat();
	Void run(Handler handler);
	String toString();
	Boolean needsContext();
	Void setContext(Context context);
}
