package com.monitoring.munin_node.plugin_api;
import com.monitoring.munin_node.plugins.*;

public class PluginFactory {
	public static Plugin_API getPlugin(String classname){
		Class newClass = null;
		Plugin_API newInstance = null;
		try{
			newClass = Class.forName(classname);
		}
		catch(ClassNotFoundException e){
			newClass = null;
		}
		try{
		newInstance = (Plugin_API) newClass.newInstance();
		}
		catch(InstantiationException e){
			// asg
		}
		catch(IllegalAccessException e){
			//asd
		}
		return newInstance;
	}
}
