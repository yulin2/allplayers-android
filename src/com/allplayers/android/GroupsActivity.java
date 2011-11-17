package com.allplayers.android;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class GroupsActivity extends ListActivity
{
	private ArrayList<GroupData> groupList;
	private boolean hasGroups = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String jsonResult = APCI_RestServices.getUserGroups();
		
		GroupsMap groups = new GroupsMap(jsonResult);
		groupList = groups.getGroupData();
		
		String[] values;
		
		if(!groupList.isEmpty())
		{
			values = new String[groupList.size()];
			
			for(int i = 0; i < groupList.size(); i++)
			{
				values[i] = groupList.get(i).getTitle();
			}
			
			hasGroups = true;
		}
		else
		{
			values = new String[]{"no groups to display"};
			hasGroups = false;
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		
		if(hasGroups)
		{
			Globals.currentGroup = groupList.get(position);
			
			//Display the group page for the selected group
			Intent intent = new Intent(GroupsActivity.this, GroupPageActivity.class);
			startActivity(intent);
		}
	}
}