package com.allplayers.android;

import com.allplayers.objects.GroupData;
import com.allplayers.rest.APCI_RestServices;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchGroupsListActivity extends ListActivity
{
	private ArrayList<GroupData> groupList;
	private boolean hasGroups = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String search = Globals.search;
		int zipcode = Globals.zipcode;
		int distance = Globals.distance;
		
		String jsonResult = APCI_RestServices.searchGroups(search, zipcode, distance);
		
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
			Intent intent = new Intent(SearchGroupsListActivity.this, GroupPageActivity.class);
			startActivity(intent);
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_MENU)
		{
			startActivity(new Intent(SearchGroupsListActivity.this, FindGroupsActivity.class));
		}
		
		return super.onKeyUp(keyCode, event);
	}
}