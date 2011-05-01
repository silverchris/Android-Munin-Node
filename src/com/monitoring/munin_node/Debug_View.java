package com.monitoring.munin_node;

import java.util.List;

import android.app.Activity;
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
        loadplugins.plugins();
        update_view();
        final Button Update = (Button) findViewById(R.id.Update);
        Update.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
            	update_view();
            }
        });
    }
    private void update_view(){
        LoadPlugins loadplugins = new LoadPlugins(this);
        List<Plugin_API> plugins = loadplugins.plugins();
        StringBuffer config = new StringBuffer();
        StringBuffer update = new StringBuffer();
        for (Plugin_API p : plugins){
        	if(p.needsContext()){
    			p.setContext(this);
    		}
        	config.append(p.getConfig());
        	update.append(p.getUpdate());
        }
		final Update updater = new Update(this); 
		updater.doUpdate();
        TextView configView = (TextView) findViewById(R.id.ConfigView);
        TextView updateView = (TextView) findViewById(R.id.UpdateView);
        configView.setText(config.toString());
        updateView.setText(update.toString());
    }
}
