package com.allplayers.android;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageThreadData
{
	private String mid = "";
	private String subject = "";
	private String body = "";
	private String last_updated = "";
	private String is_new = "";
	private String sender = "";
	private String sender_uuid = "";
	private String uri = "";
	private Date updatedDate = null;
	
	public MessageThreadData()
	{
		
	}
	
	public MessageThreadData(String jsonResult)
	{
		JSONObject messageObject = null;
		
		try
		{
			messageObject = new JSONObject(jsonResult);
		}
		catch(JSONException ex)
		{
			System.err.println("MessageThreadData/messageObject/" + ex);
		}
		
		try
		{
			mid = messageObject.getString("mid");
		}
		catch(JSONException ex)
		{
			System.err.println("MessageThreadData/mid/" + ex);
		}
		
		try
		{
			subject = messageObject.getString("subject");
		}
		catch(JSONException ex)
		{
			System.err.println("MessageThreadData/subject/" + ex);
		}
		
		try
		{
			body = messageObject.getString("body");
		}
		catch(JSONException ex)
		{
			System.err.println("MessageThreadData/body/" + ex);
		}
		
		try
		{
			last_updated = messageObject.getString("timestamp") + "000"; //convert seconds to milliseconds
			updatedDate = parseTimestamp(last_updated);
			last_updated = Long.toString(updatedDate.getTime()); //update the string in case someone uses it
		}
		catch(JSONException ex)
		{
			System.err.println("MessageThreadData/last_updated/" + ex);
		}
		
		try
		{
			is_new = messageObject.getString("is_new");
		}
		catch(JSONException ex)
		{
			System.err.println("MessageThreadData/is_new/" + ex);
		}
		
		try
		{
			sender = messageObject.getString("sender");
		}
		catch(JSONException ex)
		{
			System.err.println("MessageThreadData/sender/" + ex);
		}
		
		try
		{
			sender_uuid = messageObject.getString("sender_uuid");
		}
		catch(JSONException ex)
		{
			System.err.println("MessageThreadData/sender_uuid/" + ex);
		}
		
		try
		{
			uri = messageObject.getString("uri");
		}
		catch(JSONException ex)
		{
			System.err.println("MessageThreadData/uri/" + ex);
		}
	}
	
	private Date parseTimestamp(String timestamp)
	{
		Date date = new Date(Long.parseLong(timestamp));
		
		TimeZone timezone = TimeZone.getDefault();
		int offset = timezone.getOffset(date.getTime());
		date = new Date(date.getTime() + offset);
		return date;
	}
	
	public String getTimestampString()
	{
		return last_updated;
	}
	
	public Date getDate()
	{
		return updatedDate;
	}
	
	public String getDateString()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(updatedDate);
		
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
	
	public String getMId()
	{
		return mid;
	}
	
	public String getMessageBody()
	{
		return body;
	}
	
	public String getSenderName()
	{
		return sender;
	}
	
	public String getSenderId()
	{
		return sender_uuid;
	}
	
	public String getSubject()
	{
		return subject;
	}
	
	public String getNew()
	{
		return is_new;
	}
	
	public String getURI()
	{
		return uri;
	}
}