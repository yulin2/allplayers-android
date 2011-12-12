package com.allplayers.android;

import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class GroupMembersActivity extends ListActivity
{
	private ArrayList<GroupMemberData> membersList;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String jsonResult = RestApiV1.getGroupMembersByGroupId(Globals.currentGroup.getUUID());
		
		GroupMembersMap groupMembers = new GroupMembersMap(jsonResult);
		membersList = groupMembers.getGroupMemberData();
		
		String[] values;
		
		if(!membersList.isEmpty())
		{
			values = new String[membersList.size()];
			
			for(int i = 0; i < membersList.size(); i++)
			{
				values[i] = membersList.get(i).getName();
			}
		}
		else
		{
			values = new String[]{"No members to display"};
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}
}