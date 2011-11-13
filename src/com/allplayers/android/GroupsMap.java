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
					groups.add(new GroupData(jsonArray.getString(i)));
				}
			}
		}
		catch(JSONException ex)
		{
			System.out.println(ex);
		}
	}
	
	public ArrayList<GroupData> getGroupData()
	{
		return groups;
	}
}