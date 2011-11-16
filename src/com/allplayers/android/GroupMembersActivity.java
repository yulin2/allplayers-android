package com.allplayers.android;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GroupMembersActivity extends ListActivity
{
	private ArrayList<GroupMemberData> membersList;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String jsonResult = APCI_RestServices.getGroupMembersByGroupId(Globals.currentGroup.getUUID());
		
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
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, values);
			setListAdapter(adapter);
		}
		else
		{
			TextView tv = new TextView(this);
			tv.setText("No members to display");
			setContentView(tv);
		}
	}
}