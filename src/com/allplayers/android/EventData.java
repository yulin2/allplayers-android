package com.allplayers.android;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

public class EventData
{
	private String uuid = "";
	private String title = "";
	private String description = "";
	private String category = "";
	private String start = "";
	private String end = "";
	private String longitude = "";
	private String latitude = "";
	
	private Date startDate = null;
	private Date endDate = null;
	
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
			
			startDate = parseDatetime(start);
			endDate = parseDatetime(end);
			
			//Get rid of the 'T' in start and end
			int separator = start.indexOf("T");
			start = start.substring(0, separator) + " " + start.substring(separator + 1);
			
			separator = end.indexOf("T");
			end = end.substring(0, separator) + " " + end.substring(separator + 1);
			
			latitude = eventObject.getJSONObject("resource").getJSONObject("location").getString("latitude");
			longitude = eventObject.getJSONObject("resource").getJSONObject("location").getString("longitude");
		}
		catch(JSONException ex)
		{
			System.out.println("EventData/" + ex);
		}
	}
	
	private Date parseDatetime(String datetime)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = dateFormat.parse(datetime, new ParsePosition(0));
		
		TimeZone timezone = TimeZone.getDefault();
		int offset = timezone.getOffset(date.getTime());
		date = new Date(date.getTime() + offset);
		return date;
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
	
	public Date getStartDate()
	{
		return startDate;
	}
	
	public String getEnd()
	{
		return end;
	}
	
	public Date getEndDate()
	{
		return endDate;
	}
	
	public String getLatitude()
	{
		return latitude;
	}
	
	public String getLongitude()
	{
		return longitude;
	}
}