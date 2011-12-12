package com.allplayers.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupMemberData extends DataObject
{
	private String uuid = "";
	private String name = "";
	private String picture = "";
	
	public GroupMemberData()
	{
		
	}
	
	public GroupMemberData(String jsonResult)
	{
		JSONObject memberObject = null;
		
		try
		{
			memberObject = new JSONObject(jsonResult);
		}
		catch(JSONException ex)
		{
			System.err.println("GroupMemberData/memberObject/" + ex);
		}
		
		try
		{
			uuid = memberObject.getString("uuid");
		}
		catch(JSONException ex)
		{
			System.err.println("GroupMemberData/uuid/" + ex);
		}
		
		String fname = null;
		String lname = null;
		
		try
		{
			fname = memberObject.getString("fname");
		}
		catch(JSONException ex)
		{
			System.err.println("GroupMemberData/fname/" + ex);
		}
			
		try
		{
			lname = memberObject.getString("lname");
		}
		catch(JSONException ex)
		{
			System.err.println("GroupMemberData/lname/" + ex);
		}

		//To fix names that are all lowercase
		if(fname != null && !fname.equals("null"))
		{
			name = fname.substring(0,1).toUpperCase() + fname.substring(1).toLowerCase();
		}
		if(lname != null && !lname.equals("null"))
		{
			//If a first name has been added already
			if(!name.equals(""))
			{
				name += " ";
			}
			
			name += lname.substring(0,1).toUpperCase() + lname.substring(1).toLowerCase();
		}
		
		try
		{
			picture = memberObject.getString("picture");
		}
		catch(JSONException ex)
		{
			System.err.println("GroupMemberData/picture/" + ex);
		}
	}
	
	public String getUUID()
	{
		return uuid;
	}
	
	public String getId()
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