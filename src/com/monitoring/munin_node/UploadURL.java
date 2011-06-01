package com.monitoring.munin_node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.Handler;
import android.os.Message;

public class UploadURL extends Thread {
	final static String charset = "UTF-8";
	boolean status = false;
	Handler handler = null;
	String Server = null;
	ByteArrayOutputStream OUT = null;
	public  UploadURL(Handler newhandler, String server, ByteArrayOutputStream out){
		handler = newhandler;
		Server = server;
		OUT = out;
	}
	@Override
	public void run(){
		try {
			URLConnection connection = new URL(Server).openConnection();
			connection.setDoOutput(true); // Triggers POST.
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", "application/octet-stream");
			connection.setRequestProperty("Content-Length", Integer.toString(OUT.size()));
			OutputStream output = null;
			output = connection.getOutputStream();
			OUT.writeTo(output);
			connection.connect();
			if (output != null) try { output.close(); } catch (IOException e){}
			if (((HttpURLConnection) connection).getResponseCode() == 200){
				status = true;
				output = null;
				OUT = null;
				connection = null;
			}
			else{
				status = false;
				output = null;
				OUT = null;
				connection = null;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message msg = Message.obtain(handler, 43);
		handler.sendMessage(msg);
		OUT = null;
		Server = null;
		handler = null;
		return;
	}
}
