package com.monitoring.munin_node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.acra.ErrorReporter;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import android.util.Base64;

public class Upload {
	String Server = null;
	String Passcode = null;
	ByteArrayOutputStream OUT = null;
	public Upload(String server, String passcode, ByteArrayOutputStream out){
		Server = server;
		Passcode = passcode; 
		OUT = out;
	}
	public void close(){
		Server = null;
		Passcode = null;
		OUT = null;
	}
	public Boolean upload(){
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(Server);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("passcode", Passcode));
		String output = Base64.encodeToString(OUT.toByteArray(),Base64.DEFAULT);
		System.out.println("GZIPPED: "+OUT.size());
		System.out.println("BASE64: "+output.length());
		pairs.add(new BasicNameValuePair("bin", output));
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs));
		} catch (UnsupportedEncodingException e) {
			ErrorReporter.getInstance().handleException(e);
		}
		HttpResponse response = null;
		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			ErrorReporter.getInstance().handleException(e);
			return false;
		} catch (IOException e) {
			ErrorReporter.getInstance().handleException(e);
			return false;
		} catch(IllegalStateException e){
			ErrorReporter.getInstance().handleException(e);
			return false;
		}
		if (response.getStatusLine().getStatusCode() == 200){
			return true;
		}
		else{
			return false;
		}
	}
}
