package com.allplayers.android;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupMemberData
{
	private String uuid = "";
	private String name = "";
	private String picture = "";
	
	public GroupMemberData()
	{
		
	}
	
	public GroupMemberData(String jsonResult)
	{
		try
		{
			JSONObject memberObject = new JSONObject(jsonResult);
			uuid = memberObject.getString("uuid");
			String fname = memberObject.getString("fname");
			String lname = memberObject.getString("lname");
			//To fix names that are all lowercase
			name = fname.substring(0,1).toUpperCase() + fname.substring(1).toLowerCase() + " " + 
					lname.substring(0,1).toUpperCase() + lname.substring(1).toLowerCase();
			
			picture = memberObject.getString("picture");
		}
		catch(JSONException ex)
		{
			System.out.println(ex);
		}
	}
	
	public String getUUID()
	{
		return uuid;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getPicture()
	{
		return picture;
	}
}