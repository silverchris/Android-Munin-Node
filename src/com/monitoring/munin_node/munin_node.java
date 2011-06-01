package com.monitoring.munin_node;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

public class munin_node extends TabActivity {
	Intent service = null;
	PendingIntent pendingIntent = null;
	AlarmManager alarmManager;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		service = new Intent(this, munin_receiver.class);
		pendingIntent = PendingIntent.getBroadcast(this, 1234567, service, PendingIntent.FLAG_NO_CREATE);
		if (pendingIntent == null){
	        SharedPreferences settings = getSharedPreferences("Munin_Node", 0);
	        SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("Service_Running", false);
			editor.putBoolean("enabled", true).commit();
			editor.commit();
		}
        SharedPreferences settings = getSharedPreferences("Munin_Node", 0);
        SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("enabled", true).commit();
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

        tabHost.setCurrentTab(0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        SharedPreferences settings = getSharedPreferences("Munin_Node", 0);
        Boolean running = settings.getBoolean("Service_Running", false);
        if(!running){
        	inflater.inflate(R.layout.option_menu_start, menu);
        }
        else if(running){
        	inflater.inflate(R.layout.option_menu_stop, menu);
        }
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
        MenuInflater inflater = getMenuInflater();
        SharedPreferences settings = getSharedPreferences("Munin_Node", 0);
        Boolean running = settings.getBoolean("Service_Running", false);
        if(!running){
        	inflater.inflate(R.layout.option_menu_start, menu);
        }
        else if(running){
        	inflater.inflate(R.layout.option_menu_stop, menu);
        }
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences settings = getSharedPreferences("Munin_Node", 0);
        SharedPreferences.Editor editor = settings.edit();
    	switch (item.getItemId()) {
    	case R.id.start:
            int Update_Interval = Integer.parseInt(settings.getString("Update_Interval", "10"));
            if (pendingIntent == null){
            	service =new Intent(this, munin_receiver.class);
            	pendingIntent = PendingIntent.getBroadcast(this, 1234567, service, 0);
            }
    		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, 60000*Update_Interval, pendingIntent);
    		//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, 5000, pendingIntent);
    		editor.putBoolean("Service_Running", true);
    		editor.commit();
            return true;
    	case R.id.stop:
    		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    		alarmManager.cancel(pendingIntent);
    		editor.putBoolean("Service_Running", false);
    		editor.commit();
            return true;
    	case R.id.force:
        	Intent service =new Intent(this, munin_service.class);  
            this.startService(service) ;
    		return true;
    	}
    	return false;
    }

}