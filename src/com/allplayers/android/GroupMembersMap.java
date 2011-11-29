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
					GroupMemberData member = new GroupMemberData(jsonArray.getString(i));
					
					if(Globals.isUnique(member, members))
					{
						members.add(member);
					}
				}
			}
		}
		catch(JSONException ex)
		{
			System.err.println("GroupMembersMap/" + ex);
		}
	}
	
	public ArrayList<GroupMemberData> getGroupMemberData()
	{
		return members;
	}
}