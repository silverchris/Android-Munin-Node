package com.monitoring.munin_node.plugins;

import com.monitoring.munin_node.plugin_api.Plugin_API;

import android.content.Context;
import android.telephony.*;

public class cell_signal implements Plugin_API {

	@Override
	public String getName() {
		return "Cell Signal";
	}

	@Override
	public String getCat() {
		return "Android Phone";
	}

	@Override
	public String getConfig() {
		
		return null;
	}

	@Override
	public String getUpdate() {
		// TODO Auto-generated method stub
		return null;
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
