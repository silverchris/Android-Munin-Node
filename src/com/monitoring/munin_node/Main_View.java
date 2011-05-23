	package com.monitoring.munin_node;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Main_View extends Activity{
	public static final String PREFS_NAME = "Munin_Node";
	public String Update_Interval = null;
	public String Update_Interval_New = null;
	public String Server = null;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Server = settings.getString("Server", "Server");
        Update_Interval = settings.getString("Update_Interval", "10");
        setContentView(R.layout.main_view);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        final EditText server_text = (EditText) findViewById(R.id.Server);
        final TextView android_id = (TextView) findViewById(R.id.ANDROID_ID);
        android_id.setText("Android ID: "+Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID));
        final Button save = (Button) findViewById(R.id.Save1);
        server_text.setText(Server);
        System.out.println(Server);
        System.out.println(Update_Interval);
        if (Update_Interval.contentEquals("5")){
        	spinner.setSelection(0, true);
        	System.out.println("setting spinner to 5");
        }
        else if (Update_Interval.contentEquals("10")){
        	spinner.setSelection(1, true);
        	System.out.println("setting spinner to 10");
        }
        else if (Update_Interval.contentEquals("15")){
        	spinner.setSelection(2,true);
        	System.out.println("setting spinner to 15");
        }
        
        save.setOnClickListener(new View.OnClickListener() {  
        	final Handler test_handler = new Handler(){
    			@Override
    			public void handleMessage(Message msg){
    				System.out.println("Recieved Message");
    				super.handleMessage(msg);
    				Bundle bundle = (Bundle)msg.obj;
    				Toast toast = Toast.makeText(Main_View.this, bundle.getString("result") , Toast.LENGTH_LONG);
    				toast.setGravity(Gravity.BOTTOM, -30, 50);
    				toast.show();
    			}
    		};
    		public void onClick(View v) {
    			Pattern URL = Pattern.compile("http\\:\\/\\/.+\\/$");
    			Matcher match1 = URL.matcher(server_text.getText().toString());
    			Boolean ok = false;
    			while (match1.find()) {
    				ok = true;
    			}
    			if (ok){
    				new Thread(new Runnable(){
    					public void run(){
    						Server = server_text.getText().toString();
    						Test_Settings test = new Test_Settings();
    						Integer test_value = test.Run_Test(Server);
    						String result = null;
    						System.out.println(test_value);
    						switch(test_value){
    						case Test_Settings.OK:
    							save_settings();
    							result = "Saved Successfully";
    							break;
    						case Test_Settings.FAILURE:
    							result = "General Failure, Check URL and try again";
    							break;
    						}
    						Bundle bundle = new Bundle();
    						bundle.putString("result", result);
    						Message msg = Message.obtain(test_handler, 42, bundle);
    						test_handler.sendMessage(msg);
    					}
    				}
    				).start();
    			}
    			else{
    				Toast toast = Toast.makeText(Main_View.this,"URL is Invalid" , Toast.LENGTH_LONG);
    				toast.setGravity(Gravity.BOTTOM, -30, 50);
    				toast.show();
    			}
        		}   
        	});
        CheckBox onboot = (CheckBox) findViewById(R.id.onBoot);
        onboot.setChecked(settings.getBoolean("onBoot", false));
        onboot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        SharedPreferences.Editor editor = settings.edit();
		        editor.putBoolean("onBoot", isChecked);
		        editor.commit();
			}
		});
    }
        
    public class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) {
        	Update_Interval_New = parent.getItemAtPosition(pos).toString();
        	System.out.println(Update_Interval);
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
          }
    }
    private void save_settings(){
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Server", Server);
        editor.putString("Update_Interval", Update_Interval_New);
        editor.commit();
        System.out.println(Server);
        System.out.println(Update_Interval);
    }
}