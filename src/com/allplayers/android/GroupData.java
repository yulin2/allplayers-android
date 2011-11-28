package com.allplayers.android;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupData
{
	private String uuid = "";
	private String title = "";
	private String description = "";
	private String logo = "";
	
	public GroupData()
	{
		
	}
	
	public GroupData(String jsonResult)
	{
		JSONObject groupObject = null;
		
		try
		{
			groupObject = new JSONObject(jsonResult);
		}
		catch(JSONException ex)
		{
			System.err.println("GroupData/groupObject/" + ex);
		}
		
		try
		{
			uuid = groupObject.getString("uuid");
		}
		catch(JSONException ex)
		{
			System.err.println("GroupData/uuid/" + ex);
		}
		
		try
		{
			title = groupObject.getString("title");
		}
		catch(JSONException ex)
		{
			System.err.println("GroupData/title/" + ex);
		}
		
		try
		{
			description = groupObject.getString("description");
		}
		catch(JSONException ex)
		{
			System.err.println("GroupData/description/" + ex);
		}
		
		try
		{
			logo = groupObject.getString("logo");
		}
		catch(JSONException ex)
		{
			System.err.println("GroupData/logo/" + ex);
		}
	}
	
	public String getUUID()
	{
		return uuid;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getLogo()
	{
		return logo;
	}
}