package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class GroupsMap
{
	private ArrayList<GroupData> groups = new ArrayList<GroupData>();
	
	public GroupsMap(String jsonResult)
	{
		try
		{
			JSONArray jsonArray = new JSONArray(jsonResult);
			
			if(jsonArray.length() > 0)
			{
				for(int i = 0; i < jsonResult.length(); i++)
				{
					GroupData group = new GroupData(jsonArray.getString(i));
					
					if(Globals.isUnique(group, groups))
					{
						groups.add(group);
					}
				}
			}
		}
		catch(JSONException ex)
		{
			System.err.println("GroupsMap/" + ex);
		}
	}
	
	public ArrayList<GroupData> getGroupData()
	{
		return groups;
	}
}