package com.monitoring.munin_node.plugins;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.SystemClock;
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

	@Override
	public String getConfig() {
		
		return "";
	}

	@Override
	public String getUpdate() {
		TelephonyManager TelManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		PhoneStateListener mSignalListener = new PhoneStateListener(){ 

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
			    }
			  }; 
			  TelManager.listen(mSignalListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			  SystemClock.sleep(10000);
			  //TelManager.listen(mSignalListener,PhoneStateListener.LISTEN_NONE ); 
			  System.out.println(ASU);
		return "";
	}

	@Override
	public Boolean needsContext() {
		return true;	}

	@Override
	public Void setContext(Context newcontext) {
		context = new ContextWrapper(newcontext);
		return null;
	}

}
