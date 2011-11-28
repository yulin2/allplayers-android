package com.allplayers.android;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
	
	public String getDateString(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1; //because calendar uses 0-11 instead of 1-12
		int year = calendar.get(Calendar.YEAR);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		String AmPm = "AM";
		
		if(hour >= 12)
		{
			AmPm = "PM";
			
			if(hour > 12)
			{
				hour = hour - 12;
			}
		}
		else if(hour == 0)
		{
			hour = 12;
		}
		
		DecimalFormat df = new DecimalFormat("00");
		
		return "" + df.format(month) + "/" + df.format(day) + "/" + year + " " + df.format(hour) + ":" + df.format(minute) + AmPm;
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
	
	public String getStartDateString()
	{
		return getDateString(startDate);
	}
	
	public String getEnd()
	{
		return end;
	}
	
	public Date getEndDate()
	{
		return endDate;
	}
	
	public String getEndDateString()
	{
		return getDateString(endDate);
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