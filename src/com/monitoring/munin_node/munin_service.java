package com.monitoring.munin_node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.acra.ErrorReporter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings.Secure;

import com.monitoring.munin_node.protos.Plugins;
import com.monitoring.munin_node.plugin_api.LoadPlugins;
import com.monitoring.munin_node.plugin_api.PluginFactory;
import com.monitoring.munin_node.plugin_api.Plugin_API;

public class munin_service extends Service{
	final int MUNIN_NOTIFICATION = 1;
    List<Plugin_API> plugin_objects;
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

        final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);        
		Notification notification = new Notification(R.drawable.notification, "Munin Node Started", when);
		Context context = getApplicationContext();
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,  new Intent(this, munin_node.class), 0);
		notification.setLatestEventInfo(context, "Munin Node", "Just letting you know I am running", contentIntent);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		mNotificationManager.notify(MUNIN_NOTIFICATION, notification);
		class Count{
			int ran = 0;
			int done = 0;
			public void ranincrement(){
				ran++;
			}
			public void doneincrement(){
				done++;
			}
			public Boolean Done(){
				if(done == ran){
					return true;
				}
				else{
					return false;
				}
			}
			public void Reset(){
				ran = 0;
				done = 0;
			}
		}

		final Count count = new Count();
		final Plugins.Builder plugins = Plugins.newBuilder();
		final Handler service_Handler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				if(msg.what == 42){
					Bundle bundle = (Bundle)msg.obj;
					Plugins.Plugin.Builder plugin = Plugins.Plugin.newBuilder();
					plugin.setName(bundle.getString("name")).setConfig(bundle.getString("config")).setUpdate(bundle.getString("update"));
					plugins.addPlugin(plugin);
					count.doneincrement();
					if(count.Done()){
						count.Reset();
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						GZIPOutputStream gzipped = null;
						try {
							gzipped = new GZIPOutputStream(out);
							plugins.build().writeTo(gzipped);
							gzipped.close();
							gzipped = null;
							plugins.clear();
						} catch (IOException e) {
							ErrorReporter.getInstance().handleException(e);
						}
			            editor.putLong("new_plugin_end_time", System.currentTimeMillis());
						String Server = settings.getString("Server", "Server");
						Server = Server+Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
			            editor.putLong("new_upload_start_time", System.currentTimeMillis()).commit();
						new UploadURL(this,Server,out).start();
						try {
							out.close();
							out = null;
						} catch (IOException e) {
							ErrorReporter.getInstance().handleException(e);
						}
					}
				}
				else if (msg.what == 43){
		            editor.putLong("new_upload_end_time", System.currentTimeMillis()).commit();
					System.out.println("Upload Finished");
					mNotificationManager.cancel(MUNIN_NOTIFICATION);//Cancel Notification that the "service" is running
			        editor.putLong("end_time", System.currentTimeMillis()).commit();
			        System.gc();
			        wakeLock.release();
					
				}
			}
		};
        LoadPlugins loadplugins = new LoadPlugins();
        List<String> plugin_list = loadplugins.getPluginList(context);
        /*class plugin_thread extends Thread{
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
        		return;
        	}
        }*/
        editor.putLong("new_plugin_start_time", System.currentTimeMillis());
        editor.commit();
        if (plugin_objects == null){
        	plugin_objects = new ArrayList<Plugin_API>();
        	for (String p :plugin_list){
        		Plugin_API plugin = (Plugin_API)PluginFactory.getPlugin(p);
        		Boolean enabled = settings.getBoolean(plugin.getName(), true);
        		if(plugin.needsContext()){
        			plugin.setContext(this);
        		}
        		if(enabled){
        			count.ranincrement();
        			plugin.run(service_Handler);
        		}
        		plugin_objects.add(plugin);
        	}
        }
        else{
        	for(Plugin_API plugin : plugin_objects){
        		Boolean enabled = settings.getBoolean(plugin.getName(), true);
        		if(enabled){
        			count.ranincrement();
        			plugin.run(service_Handler);
        		}
        	}
        }
        /*for(final String p : plugin_list){
        	plugin_thread thread = new plugin_thread(this,p);
        	thread.setDaemon(true);
        	thread.setName(p);
        	thread.start();
        	count.ranincrement();
        }*/
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

    
}
