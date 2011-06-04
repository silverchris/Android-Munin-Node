package com.monitoring.munin_node;

import org.acra.ACRA;
import org.acra.annotation.*;
import android.app.Application;

//@ReportsCrashes(formKey = "dHlEWldWSVdRQVhtWGZ5UTZKeUtvWHc6MQ")
@ReportsCrashes(formKey = "dFNIMlVOdmVXM0loS1pBNGFJUmF3SFE6MQ")
public class MuninNode extends Application {
	   @Override
	    public void onCreate() {
	        // The following line triggers the initialization of ACRA
	        ACRA.init(this);
	        super.onCreate();
	    }
}
