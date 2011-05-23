package com.monitoring.munin_node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class Test_Settings {
	public static final int OK = 0;
	public static final int PASSCODE_WRONG = 1;
	public static final int FAILURE = -1;
	public Integer Run_Test(String Server){
		HttpClient client = new DefaultHttpClient();
		String server = Server+"?test=true";
		HttpGet get = new HttpGet(server);
		HttpResponse response = null;
		try {
			response = client.execute(get);
		} catch (ClientProtocolException e) {
			return -1;
		} catch (IOException e) {
			return -1;
		} catch(IllegalStateException e){
			return -1;
		}
		if (response.getStatusLine().getStatusCode() == 200){
			HttpEntity entity = response.getEntity();
			InputStream input = null;
			BufferedReader in = null;
			try {
				input = entity.getContent();
				in = new BufferedReader(new InputStreamReader(input));
				String Response = in.readLine();
				if(Response != null){
					if(Response.contains("munin ok")){
						return 0;
					}
					else if(Response.contains("Password Wrong")){
						return 1;
					}
				}
			} catch (IllegalStateException e) {
				return -1;
			} catch (IOException e) {
				return -1;
			}
		}
		else{
			return -1;
		}
		return -1;
	}
}
