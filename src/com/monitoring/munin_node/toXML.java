package com.monitoring.munin_node;

import org.xmlpull.v1.XmlSerializer;
import android.util.Xml;

import java.io.IOException;
import java.io.StringWriter;


public class toXML {
	XmlSerializer  serializer = Xml.newSerializer();		
	StringWriter output = new StringWriter();
	
	public toXML(){
		try {
			serializer.setOutput(output);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			serializer.startTag(null, "munin");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void addPlugin(String name, String config, String update){
	
		try {
			serializer.startTag(null, "plugin");
			serializer.attribute(null, "name", name);
			serializer.startTag(null, "config");
			serializer.text(config);
			serializer.endTag(null, "config");
			serializer.startTag(null, "update");
			serializer.text(update);
			serializer.endTag(null, "update");
			serializer.endTag(null, "plugin");
		} catch (IllegalArgumentException e) {
			// Fuck
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// You
			e.printStackTrace();
		} catch (IOException e) {
			// Eclipse
			e.printStackTrace();
		}
	}
	public String toString(){
        try {
			serializer.endTag(null, "munin");
            serializer.endDocument();
            serializer.flush();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}
}
