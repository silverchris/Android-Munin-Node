package com.monitoring.munin_node;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.monitoring.munin_node.plugin_api.LoadPlugins;
import com.monitoring.munin_node.plugin_api.Plugin_API;
import com.monitoring.munin_node.plugins.*;

public class Update {
	private Context context;
	public Update(Context _context){
		context = _context;
	}
	public void doUpdate(){
        SharedPreferences settings = context.getSharedPreferences("Munin_Node", 0);
        LoadPlugins loadplugins = new LoadPlugins(context);
        List<Plugin_API> plugins = loadplugins.plugins();
        toXML xmlgen = new toXML();
        for (Plugin_API p : plugins){
        	Boolean enabled = settings.getBoolean(p.getName(), true);
        	if (enabled){
        		xmlgen.addPlugin(p.getName(), p.getConfig(), p.getUpdate());
        	}
        }
        String Server = settings.getString("Server", "Server");
        String Passcode = settings.getString("Passcode", "Passcode");
        Upload uploader = new Upload(Server,Passcode,xmlgen.toString());
        uploader.upload();
	}

}
