package com.allplayers.android;

import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageData
{
/*  ["thread_id",
	 "subject",
	 "last_updated",
	 "is_new" -> 0 or 1,
	 "message_count",
	 "last_message_sender",
	 "last_message_sender_uuid",
	 "last_message_body",
	 "uri"]*/
	
	private String thread_id = "";
	private String subject = "";
	private String is_new = "";
	private String last_message_sender = "";
	private String last_message_body = "";
	private String last_updated = "";
	private Date updatedDate = null;
	
	public MessageData()
	{
		
	}
	
	public MessageData(String jsonResult)
	{
		try
		{
			JSONObject messageObject = new JSONObject(jsonResult);
			thread_id = messageObject.getString("thread_id");
			subject = messageObject.getString("subject");
			is_new = messageObject.getString("is_new");
			last_message_sender = messageObject.getString("last_message_sender");
			last_message_body = messageObject.getString("last_message_body");
			last_updated = messageObject.getString("last_updated") + "000"; //convert seconds to milliseconds
			
			updatedDate = parseTimestamp(last_updated);
			last_updated = Long.toString(updatedDate.getTime()); //update the string in case someone uses it
		}
		catch(JSONException ex)
		{
			System.out.println(ex);
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
	
	public String getDateString()
	{
		return last_updated;
	}
	
	public Date getDate()
	{
		return updatedDate;
	}
	
	public String getThreadID()
	{
		return thread_id;
	}
	
	public String getMessageBody()
	{
		return last_message_body;
	}
	
	public String getLastSender()
	{
		return last_message_sender;
	}
	
	public String getSubject()
	{
		return subject;
	}
	
	public String getNew()
	{
		return is_new;
	}
}