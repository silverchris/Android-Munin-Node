package com.monitoring.munin_node;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;

import com.monitoring.munin_node.plugin_api.LoadPlugins;
import com.monitoring.munin_node.plugin_api.PluginFactory;
import com.monitoring.munin_node.plugin_api.Plugin_API;

public class munin_service extends Service{
	final int MUNIN_NOTIFICATION = 1;
    /*public munin_service() {
		super("Munin Node Service");
	}*/

    @Override
    public void onDestroy() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		mNotificationManager.cancel(MUNIN_NOTIFICATION);
    }
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Munin Wake Lock");
		wakeLock.acquire();
		long when = System.currentTimeMillis();
        final SharedPreferences settings = this.getSharedPreferences("Munin_Node", 0);
        final Editor editor = settings.edit();
        editor.putLong("new_start_time", when);
        editor.commit();
		String ns = Context.NOTIFICATION_SERVICE;
		final NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.notification;
		CharSequence tickerText = "Munin Node Started";
        
		Notification notification = new Notification(icon, tickerText, when);
		Context context = getApplicationContext();
		CharSequence contentTitle = "Munin Node";
		CharSequence contentText = "Just letting you know I am running";
		Intent notificationIntent = new Intent(this, munin_node.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		mNotificationManager.notify(MUNIN_NOTIFICATION, notification);
		class count{
			Integer count = 0;
			public void increment(){
				count++;
			}
			public Integer getCount(){
				return count;
			}
		}
		class upload_thread extends Thread{
			Handler handler = null;
        	String Server = null;
        	String Passcode = null;
        	String XML = null;
        	public  upload_thread(Handler newhandler, String server, String passcode, String xml){
    			handler = newhandler;
            	Server = server;
            	Passcode = passcode;
            	XML = xml;
        	}
           	@Override
        	public void run(){
				Upload uploader = new Upload(Server,Passcode,XML);
                uploader.upload();
        			Bundle bundle = new Bundle();
        			bundle.putString("name", "");
        			bundle.putString("config", "");
        			bundle.putString("update", "");
        			Message msg = Message.obtain(handler, 43, bundle);
    				handler.sendMessage(msg);
        		}
        	};
		final count finished = new count();
		final count running = new count();
		final toXML xmlgen = new toXML();
		final Handler service_Handler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				if(msg.what == 42){
					Bundle bundle = (Bundle)msg.obj;
					xmlgen.addPlugin(bundle.getString("name"), bundle.getString("config"), bundle.getString("update"));
					finished.increment();
					if(running.getCount() == finished.getCount()){
						System.out.println("Finishing up");
						final String Server = settings.getString("Server", "Server");
						final String Passcode = settings.getString("Passcode", "Passcode");
						new upload_thread(this,Server,Passcode,xmlgen.toString()).start();
					}
				}
				else if (msg.what == 43){
					System.out.println("Upload Finished");
					mNotificationManager.cancel(MUNIN_NOTIFICATION);
					long now = System.currentTimeMillis();
			        editor.putLong("end_time", now);
			        editor.commit();
					wakeLock.release();
				}
			}
		};
		System.out.println();
        //
        LoadPlugins loadplugins = new LoadPlugins();
        List<String> plugin_list = loadplugins.getPluginList(context);
        class plugin_thread extends Thread{
        	Context Context = null;
        	String p = null;
        	public plugin_thread(Context newcontext, String newp){
        		Context = new ContextWrapper(newcontext);
        		p = newp;
        	}
           	@Override
        	public void run(){
           		SharedPreferences settings = Context.getSharedPreferences("Munin_Node", 0);
        		Plugin_API plugin = (Plugin_API)PluginFactory.getPlugin(p);
        		Boolean enabled = settings.getBoolean(plugin.getName(), true);
        		if(enabled){
        			if(plugin.needsContext()){
        				plugin.setContext(Context);
        			}
        			plugin.run(service_Handler);
        		}
        		else{
        			Bundle bundle = new Bundle();
        			bundle.putString("name", "");
        			bundle.putString("config", "");
        			bundle.putString("update", "");
        			Message msg = Message.obtain(service_Handler, 42, bundle);
    				service_Handler.sendMessage(msg);
        		}
        	}
        }
        List<plugin_thread> plugin_threads = new ArrayList<plugin_thread>();
        for(final String p : plugin_list){
        	plugin_thread thread = new plugin_thread(this,p);
        	thread.run();
        	plugin_threads.add(thread);
        	running.increment();
        }
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

    
}
