package com.monitoring.munin_node;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Debug_View extends Activity{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_view);
        update_view();
        final Button Update = (Button) findViewById(R.id.Update);
        Update.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
            	update_view();
            }
        });
    }
    private void update_view(){
    	Intent service =new Intent(this, munin_service.class);  
        this.startService(service) ;
    }
}
