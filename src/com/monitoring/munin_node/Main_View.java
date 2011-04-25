package com.monitoring.munin_node;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Main_View extends Activity{
	public static final String PREFS_NAME = "Munin_Node";
	public String Update_Interval = null;
	public String Update_Interval_New = null;
	public String Server = null;
	public Long Port = null;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Server = settings.getString("Server", "Server");
        Update_Interval = settings.getString("Update_Interval", "10");
        Port = settings.getLong("Port", 8080);
        setContentView(R.layout.main_view);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        final EditText server_text = (EditText) findViewById(R.id.Server);
        final EditText port_text = (EditText) findViewById(R.id.Port);
        final Button save = (Button) findViewById(R.id.Save1);
        server_text.setText(Server);
        port_text.setText(Port.toString());
        System.out.println(Server);
        System.out.println(Port.toString());
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
            public void onClick(View v) {  
                Server = server_text.getText().toString();
                Port = Long.parseLong(port_text.getText().toString());
                
                save_settings();
        }  
});  
    }
    public class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) {
        	Update_Interval_New = parent.getItemAtPosition(pos).toString();
        	System.out.println(Update_Interval);
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
          }
    }
    private void save_settings(){
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Server", Server);
        editor.putString("Update_Interval", Update_Interval_New);
        editor.putLong("Port", Port);
        editor.commit();
        System.out.println(Server);
        System.out.println(Port.toString());
        System.out.println(Update_Interval);
    }
}