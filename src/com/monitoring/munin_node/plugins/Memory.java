package com.monitoring.munin_node.plugins;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.monitoring.munin_node.plugin_api.Plugin_API;

//TODO cleanup new lines so that there are no extras throught the output. 
public class Memory implements Plugin_API{
	public String getName(){
		return "Memory";
	}
	public String getCat(){
		return "System";
	}
	
	@Override
	public Boolean needsContext() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Void setContext(Context context) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Void run(Handler handler) {
		Map<String, String> meminfo = new HashMap<String, String>();
		Pattern meminfo_regex = Pattern.compile("([:\\s]+)");
		try {
			BufferedReader in = new BufferedReader(new FileReader("/proc/meminfo"));
			String str;
			while ((str = in.readLine()) != null) {
				String[] items = meminfo_regex.split(str);
				Long data = Long.parseLong(items[1])*1024;
				meminfo.put(items[0], data.toString());
			}
			in.close();
		}
		catch (IOException e) {}
		StringBuffer output = new StringBuffer();

		output.append("graph_args --base 1024 -l 0 --upper-limit "+meminfo.get("MemTotal")+"\n");
		output.append("graph_vlabel Bytes\ngraph_title Memory usage\ngraph_category system\ngraph_info This graph shows what the machine uses memory for.\n");
		output.append("graph_order apps ");
		if (meminfo.containsKey("PageTables")){
			output.append("page_tables ");
		}
		if (meminfo.containsKey("SwapCached")){

			output.append("swap_cache ");
		}
		if (meminfo.containsKey("Slab")){
			output.append("slab ");
		}
		output.append("cached buffers free swap\n");
		output.append("apps.label apps\napps.draw AREA\napps.info Memory used by user-space applications.\nbuffers.label buffers\nbuffers.draw STACK\n");
		output.append("buffers.info Block device (e.g. harddisk) cache. Also where \"dirty\" blocks are stored until written.\nswap.label swap\nswap.draw STACK\n");
		output.append("swap.info Swap space used.\ncached.label cache\ncached.draw STACK\ncached.info Parked file data (file content) cache.\nfree.label unused\n");
		output.append("free.draw STACK\nfree.info Wasted memory. Memory that is not used for anything at all.\n");
		if (meminfo.containsKey("Slab")){
			output.append("slab.label slab_cache\nslab.draw STACK\nslab.info Memory used by the kernel (major users are caches like inode, dentry, etc).\n");
		}
		if (meminfo.containsKey("SwapCached")){

			output.append("swap_cache.label swap_cache\nswap_cache.draw STACK\nswap_cache.info A piece of memory that keeps track of pages that have been fetched from swap but not yet been modified.\n");
		}
		{
			output.append("page_tables.label page_tables\npage_tables.draw STACK\npage_tables.info Memory used to map between virtual and physical memory addresses.\n");
		}
		if (meminfo.containsKey("VmallocUsed")){
			output.append("vmalloc_used.label vmalloc_used\nvmalloc_used.draw LINE2\nvmalloc_used.info 'VMalloc' (kernel) memory used\n");
		}
		if (meminfo.containsKey("Committed_AS")) {
			output.append("committed.label committed\ncommitted.draw LINE2\ncommitted.info The amount of memory allocated to programs. Overcommitting is normal, but may indicate memory leaks.\n");
		}
		if (meminfo.containsKey("Mapped")) {
			output.append("mapped.label mapped\nmapped.draw LINE2\nmapped.info All mmap()ed pages.\n");
		}
		if (meminfo.containsKey("Active")) {
			output.append("active.label active\nactive.draw LINE2\nactive.info Memory recently used. Not reclaimed unless absolutely necessary.\n");
		}
		if (meminfo.containsKey("ActiveAnon")) {
			output.append("active_anon.label active_anon\nactive_anon.draw LINE1\n");
		}
		if (meminfo.containsKey("ActiveCache")){
			output.append("active_cache.label active_cache\nactive_cache.draw LINE1\n");
		}
		if (meminfo.containsKey("Inactive")) {
			output.append("inactive.label inactive\ninactive.draw LINE2\ninactive.info Memory not currently used.\n");
		}
		if (meminfo.containsKey("Inact_dirty")) {
			output.append("inact_dirty.label inactive_dirty\ninact_dirty.draw LINE1\ninact_dirty.info Memory not currently used, but in need of being written to disk.\n");
		}
		if (meminfo.containsKey("Inact_laundry")) {
			output.append("inact_laundry.label inactive_laundry\ninact_laundry.draw LINE1\n");
		}
		if (meminfo.containsKey("Inact_clean")) {
			output.append("inact_clean.label inactive_clean\ninact_clean.draw LINE1\ninact_clean.info Memory not currently used.\n");
		}
		StringBuffer output2 = new StringBuffer();

		if (meminfo.containsKey("Slab")){
			output2.append("slab.value "+meminfo.get("Slab")+"\n");
		}
		else {
			output2.append("slab.value 0\n");
		}
		if (meminfo.containsKey("SwapCached")) {
			output2.append("swap_cache.value "+meminfo.get("SwapCached")+"\n");
		}
		else {
			output2.append("swap_cache.value 0\n");
		}
		if (meminfo.containsKey("PageTables")) {
			output2.append("page_tables.value "+meminfo.get("PageTables")+"\n");
		}
		else {
			output2.append("page_tables.value 0\n");
		}
		if (meminfo.containsKey("VmallocUsed")){
			output2.append("vmalloc_used.value "+meminfo.get("VmallocUsed")+"\n");
		}
		else{
			output2.append("vmalloc_used.value 0\n");
		}
		Long appvalue = Long.parseLong(meminfo.get("MemTotal"))
		-Long.parseLong(meminfo.get("MemFree"))
		-Long.parseLong(meminfo.get("Buffers"))
		-Long.parseLong(meminfo.get("Cached"))
		-Long.parseLong(meminfo.get("Slab"))
		-Long.parseLong(meminfo.get("PageTables"))
		-Long.parseLong(meminfo.get("SwapCached"));
		output2.append("apps.value "+appvalue+"\n");
		output2.append("free.value "+meminfo.get("MemFree")+"\n");
		output2.append("buffers.value "+meminfo.get("Buffers")+"\n");
		output2.append("cached.value "+meminfo.get("Cached")+"\n");
		output2.append("swap.value "+((Long.parseLong(meminfo.get("SwapTotal")))-(Long.parseLong(meminfo.get("SwapFree")))));
		if (meminfo.containsKey("Committed_AS")) {
			output2.append("\ncommitted.value "+meminfo.get("Committed_AS"));
		}
		if (meminfo.containsKey("Mapped")) {
			output2.append("\nmapped.value "+meminfo.get("Mapped"));
		}
		if (meminfo.containsKey("Active")) {
			output2.append("\nactive.value "+meminfo.get("Active"));
		}
		if (meminfo.containsKey("ActiveAnon")) {
			output2.append("\nactive_anon.value "+meminfo.get("ActiveAnon"));
		}
		if (meminfo.containsKey("ActiveCache")) {
			output2.append("\nactive_cache.value "+meminfo.get("ActiveCache"));
		}
		if (meminfo.containsKey("Inactive")) {
			output2.append("\ninactive.value "+meminfo.get("Inactive"));
		}
		if (meminfo.containsKey("Inact_dirty")) {
			output2.append("\ninact_dirty.value "+meminfo.get("Inact_dirty"));
		}
		if (meminfo.containsKey("Inact_laundry")) {
			output2.append("\ninact_laundry.value "+meminfo.get("Inact_laundry"));
		}
		if (meminfo.containsKey("Inact_clean")) {
			output2.append("\ninact_clean.value "+meminfo.get("Inact_clean"));
		}
		Bundle bundle = new Bundle();
		bundle.putString("name", this.getName());
		bundle.putString("config", output.toString());
		bundle.putString("update", output2.toString());
		Message msg = Message.obtain(handler, 42, bundle);
		handler.sendMessage(msg);
		return null;
	}
}