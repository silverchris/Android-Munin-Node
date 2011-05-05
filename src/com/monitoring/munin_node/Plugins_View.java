
package com.monitoring.munin_node;

import android.app.ExpandableListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.monitoring.munin_node.plugin_api.LoadPlugins;

//import com.monitoring.munin_node.android.apis.R;

/**
 * Demonstrates expandable lists using a custom {@link ExpandableListAdapter}
 * from {@link BaseExpandableListAdapter}.
 */
public class Plugins_View extends ExpandableListActivity {

    ExpandableListAdapter mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up our adapter
        LoadPlugins loadplugins = new LoadPlugins(this);
        //loadplugins.plugins();
        loadplugins.genCats();
        mAdapter = new MyExpandableListAdapter(loadplugins.getCats(),loadplugins.getchildCats());
        setListAdapter(mAdapter);
        registerForContextMenu(getExpandableListView());

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Sample menu");
        menu.add(0, 0, 0, "Sample Action");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();

        String title = ((TextView) info.targetView).getText().toString();

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); 
            Toast.makeText(this, title + ": Child " + childPos + " clicked in group " + groupPos,
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            Toast.makeText(this, title + ": Group " + groupPos + " clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    /**
     * A simple adapter which maintains an ArrayList of photo resource Ids. 
     * Each photo is displayed as an image. This adapter supports clearing the
     * list of photos and adding a new photo.
     *
     */
    public class MyExpandableListAdapter extends BaseExpandableListAdapter {
        // Sample data set.  children[i] contains the children (String[]) for groups[i].
        private String[] groups = {};
        private String[][] children = {{}};
        	
        public MyExpandableListAdapter(String[] Groups, String[][] Children){
        	groups = Groups;
        	children = Children;
        }
        public Object getChild(int groupPosition, int childPosition) {
        	return children[groupPosition][childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        public TextView getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, 64);

            TextView textView = new TextView(Plugins_View.this);
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
            // Set the text starting position
            textView.setPadding(36, 0, 0, 0);
            return textView;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, 64);

            CheckBox checkBox = new CheckBox(Plugins_View.this);
            checkBox.setLayoutParams(lp);
            // Center the text vertically
            checkBox.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
            // Set the text starting position
            checkBox.setOnClickListener(new OnClickListener(){
            	public void onClick(View v){
                    if (((CheckBox) v).isChecked()) {
                    	SharedPreferences settings = getSharedPreferences("Munin_Node", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(((CheckBox) v).getText().toString(), true);
                        editor.commit();
                    } else {
                    	SharedPreferences settings = getSharedPreferences("Munin_Node", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(((CheckBox) v).getText().toString(), false);
                        editor.commit();
                    }
            	}
            });
            checkBox.setPadding(36, 0, 0, 0);
            checkBox.setText(getChild(groupPosition, childPosition).toString());
        	SharedPreferences settings = getSharedPreferences("Munin_Node", 0);
        	checkBox.setChecked(settings.getBoolean(getChild(groupPosition, childPosition).toString(), true));
            return checkBox;
        }

        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        public int getGroupCount() {
            return groups.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getGroup(groupPosition).toString());
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }
}