package com.monitoring.munin_node;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.monitoring.munin_node.plugin_api.LoadPlugins;
import com.monitoring.munin_node.plugin_api.Plugin_API;
import com.monitoring.munin_node.plugins.*;

public class Debug_View extends Activity{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_view);
        LoadPlugins loadplugins = new LoadPlugins(this);
        List<Plugin_API> plugins = loadplugins.plugins();
        update_view();
        final Button Update = (Button) findViewById(R.id.Update);
        Update.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
            	update_view();
            }
        });
        /*SharedPreferences settings = getSharedPreferences("Munin_Node", 0);
        int Update_Interval = Integer.parseInt(settings.getString("Update_Interval", "10"));
		Timer timer = new Timer();
		final Update update = new Update(this); 
		TimerTask task = new TimerTask(){
			public void run(){
				update.doUpdate();
			}
		};
		timer.scheduleAtFixedRate(task, 0, 60000*Update_Interval);*/
    }
    private void update_view(){
        LoadPlugins loadplugins = new LoadPlugins(this);
        List<Plugin_API> plugins = loadplugins.plugins();
        StringBuffer config = new StringBuffer();
        StringBuffer update = new StringBuffer();
        /*toXML xmlgen = new toXML();
        SharedPreferences settings = getSharedPreferences("Munin_Node", 0);
        String Server = settings.getString("Server", "Server");
        String Passcode = settings.getString("Passcode", "Passcode");*/
        for (Plugin_API p : plugins){
        	//xmlgen.addPlugin(p.getName(), p.getConfig(), p.getUpdate());
        	config.append(p.getConfig());
        	update.append(p.getUpdate());
        }
        /*Upload uploader = new Upload(Server,Passcode,xmlgen.toString());
        uploader.upload();*/
		final Update updater = new Update(this); 
		updater.doUpdate();
        TextView configView = (TextView) findViewById(R.id.ConfigView);
        TextView updateView = (TextView) findViewById(R.id.UpdateView);
        configView.setText(config.toString());
        updateView.setText(update.toString());
    }
}
