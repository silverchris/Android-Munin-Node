package com.monitoring.munin_node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UploadURL {
	final static String charset = "UTF-8";
	boolean status = false;
	public UploadURL(String url, ByteArrayOutputStream bin){
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setDoOutput(true); // Triggers POST.
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", "application/octet-stream");
			connection.setRequestProperty("Content-Length", Integer.toString(bin.size()));
			OutputStream output = null;
			output = connection.getOutputStream();
		    bin.writeTo(output);
		    connection.connect();
		    if (output != null) try { output.close(); } catch (IOException e){}
		    if (((HttpURLConnection) connection).getResponseCode() == 200){
		    	status = true;
		    }
		    else{
		    	status = false;
		    }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean Status(){
		return status;
	}
}
