package com.allplayers.android;

import java.sql.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

public class EventData
{
	private String uuid = "";
	private String title = "";
	private String description = "";
	private String category = "";
	private Timestamp start = null;
	private Timestamp end = null;
	
	public EventData()
	{
		
	}
	
	public EventData(String jsonResult)
	{
		try
		{
			JSONObject eventObject = new JSONObject(jsonResult);
			uuid = eventObject.getString("uuid");
			title = eventObject.getString("title");
			description = eventObject.getString("description");
			category = eventObject.getString("category");
			//start = Timestamp.valueOf(eventObject.getString("start"));
			//end = Timestamp.valueOf(eventObject.getString("end"));
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
	
	public String getTitle()
	{
		return title;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getCategory()
	{
		return category;
	}
	
	public Timestamp getStart()
	{
		return start;
	}
	
	public Timestamp getEnd()
	{
		return end;
	}
}