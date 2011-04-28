package com.monitoring.munin_node.plugin_api;
import android.content.Context;

public interface Plugin_API {
	String getName();
	String getCat();
	String getConfig();
	String getUpdate();
	String toString();
	Boolean needsContext();
	Void setContext(Context context);
}
