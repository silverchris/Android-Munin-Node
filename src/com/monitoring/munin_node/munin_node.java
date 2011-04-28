package com.monitoring.munin_node;

import android.app.TabActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

public class munin_node extends TabActivity {
	Intent service = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, Main_View.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("main").setIndicator("Main",res.getDrawable(R.drawable.ic_tab_main_view)).setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, Plugins_View.class);
        spec = tabHost.newTabSpec("plugins").setIndicator("Plugins",res.getDrawable(R.drawable.ic_tab_plugins_view)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, Debug_View.class);
        spec = tabHost.newTabSpec("debug").setIndicator("Debug",res.getDrawable(R.drawable.ic_tab_debug_view)).setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.option_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.start:
    		service =new Intent(this, munin_service.class);  
            this.startService(service) ;
            return true;
    	case R.id.stop:
    		service = new Intent(this, munin_service.class);  
            this.stopService(service) ;
            return true;        
    	}
    	return false;
    }

}