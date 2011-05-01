package com.monitoring.munin_node;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;

public class Upload {
	 String Server = null;
	 String Passcode = null;
	 String XML = null;
	public Upload(Context context, String passcode, String Xml){
		SharedPreferences settings = context.getSharedPreferences("Munin_Node", 0);
		Server = settings.getString("Server", "Server");
		Passcode = passcode; 
		XML = Xml;
	}
	public Upload(String server, String passcode, String Xml){
		Server = server;
		Passcode = passcode; 
		XML = Xml;
	}
	public Upload(Context context){
		SharedPreferences settings = context.getSharedPreferences("Munin_Node", 0);
		Server = settings.getString("Server", "Server");
	}
	public Upload(String server){
		Server = server;
	}
	public Boolean upload(){
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(Server);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("passcode", Passcode));
		pairs.add(new BasicNameValuePair("xml", XML));
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs));
		} catch (UnsupportedEncodingException e) {
			//lol probably should do shit here
		}
		HttpResponse response = null;
		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch(IllegalStateException e){
			return false;
		}
		if (response.getStatusLine().getStatusCode() == 200){
			return true;
		}
		else{
			return false;
		}
	}
	public void setPasscode(String passcode){
		Passcode = passcode; 
	}
	public void setXML(String Xml){
		XML = Xml;
	}
	

}
