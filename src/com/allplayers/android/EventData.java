package com.allplayers.android;

import org.json.JSONException;
import org.json.JSONObject;

public class EventData
{
	private String uuid = "";
	private String title = "";
	private String description = "";
	private String category = "";
	private String start = null;
	private String end = null;
	
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
			start = eventObject.getString("start");
			end = eventObject.getString("end");
			
			//Get rid of the 'T' in start and end
			int separator = start.indexOf("T");
			start = start.substring(0, separator) + " " + start.substring(separator + 1);
			
			separator = end.indexOf("T");
			end = end.substring(0, separator) + " " + end.substring(separator + 1);
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
	
	public String getStart()
	{
		return start;
	}
	
	public String getEnd()
	{
		return end;
	}
}