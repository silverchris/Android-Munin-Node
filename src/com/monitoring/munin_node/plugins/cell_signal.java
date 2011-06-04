package com.monitoring.munin_node.plugins;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.monitoring.munin_node.plugin_api.Plugin_API;

public class cell_signal implements Plugin_API {
	ContextWrapper context = null;
	public Integer ASU = -1;

	@Override
	public String getName() {
		return "Cell Signal";
	}

	@Override
	public String getCat() {
		return "Android Phone";
	}

	/*@Override
	public String getConfig() {

		return "";
	}

	@Override
	public String getUpdate() {

		return "";
	}*/

	@Override
	public Boolean needsContext() {
		return true;	}

	@Override
	public Void setContext(Context newcontext) {
		context = new ContextWrapper(newcontext);
		return null;
	}

	@Override
	public Void run(final Handler handler) {
		final TelephonyManager TelManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final PhoneStateListener mSignalListener = new PhoneStateListener(){ 

			@Override
			public void onSignalStrengthsChanged(SignalStrength signalStrength) 
			{ 
				if (signalStrength.isGsm()) 
					ASU = signalStrength.getGsmSignalStrength(); 
				else{ 
					int strength = -1; 
					if (signalStrength.getEvdoDbm() < 0) 
						strength = signalStrength.getEvdoDbm(); 
					else if (signalStrength.getCdmaDbm() < 0) 
						strength = signalStrength.getCdmaDbm(); 
					if (strength < 0){ 
						// convert to asu 
						ASU = Math.round((strength + 113f) / 2f); 
					} 
				} 
				super.onSignalStrengthsChanged(signalStrength);
				StringBuffer output = new StringBuffer();
				output.append("graph_title Signal Stregth \n");
				output.append("graph_args --upper-limit 31 -l 0\n");
				output.append("graph_scale no\n");
				output.append("graph_vlabel ASU\n");
				output.append("graph_category Android Phone\n");
				output.append("graph_info This graph shows Cellular Signal in ASU\n");
				output.append("signal.label Signal\n");
				Bundle bundle = new Bundle();
				bundle.putString("name", "Cell Signal");
				bundle.putString("config", output.toString());
				bundle.putString("update", "signal.value "+ASU);
				Message msg = Message.obtain(handler, 42, bundle);
				handler.sendMessage(msg);
				output = null;
				ASU = null;
			}
		}; 
		TelManager.listen(mSignalListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		Timer Timer = new Timer();
		TimerTask TimerTask = new TimerTask(){
			@Override
			public void run(){
				TelManager.listen(mSignalListener, PhoneStateListener.LISTEN_NONE);
			}
		};
		Timer.schedule(TimerTask, 1000);
		return null;
	}

}
