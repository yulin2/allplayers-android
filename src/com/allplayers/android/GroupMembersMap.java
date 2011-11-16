package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class GroupMembersMap
{
private ArrayList<GroupMemberData> members = new ArrayList<GroupMemberData>();
	
	public GroupMembersMap(String jsonResult)
	{
		try
		{
			JSONArray jsonArray = new JSONArray(jsonResult);
			
			if(jsonArray.length() > 0)
			{
				for(int i = 0; i < jsonResult.length(); i++)
				{
					members.add(new GroupMemberData(jsonArray.getString(i)));
				}
			}
		}
		catch(JSONException ex)
		{
			System.out.println(ex);
		}
	}
	
	public ArrayList<GroupMemberData> getGroupMemberData()
	{
		return members;
	}
}