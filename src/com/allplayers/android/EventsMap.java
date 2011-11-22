package com.allplayers.android;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;

public class EventsMap
{
private ArrayList<EventData> events = new ArrayList<EventData>();
	
	public EventsMap(String jsonResult)
	{
		try
		{
			JSONArray jsonArray = new JSONArray(jsonResult);
			
			if(jsonArray.length() > 0)
			{
				for(int i = 0; i < jsonResult.length(); i++)
				{
					EventData event = new EventData(jsonArray.getString(i));
					
					String end = event.getEnd();
					
					int separator = end.indexOf("-");
					int year = Integer.parseInt(end.substring(0, separator));
					end = end.substring(separator + 1);
					
					separator = end.indexOf("-");
					int month = Integer.parseInt(end.substring(0, separator)) - 1; //Calendar month ranges from 0-11
					end = end.substring(separator + 1);
					
					separator = end.indexOf(" ");
					int day = Integer.parseInt(end.substring(0, separator));
					end = end.substring(separator + 1);
					
					separator = end.indexOf(":");
					int hour = Integer.parseInt(end.substring(0, separator));
					end = end.substring(separator + 1);
					
					separator = end.indexOf(":");
					int minute = Integer.parseInt(end.substring(0, separator));
					end = end.substring(separator + 1);
					
					int second = Integer.parseInt(end);
					
					Calendar eventEnd = Calendar.getInstance();
					eventEnd.set(year, month, day, hour, minute, second);
					
					Calendar currentDate = Calendar.getInstance();
					
					//If you want to also include events that have already happened for the current day, uncomment the below code
					//currentDate.set(Calendar.HOUR_OF_DAY, 0);
					//currentDate.set(Calendar.MINUTE, 0);
					//currentDate.set(Calendar.SECOND, 0);
					
					if(currentDate.before(eventEnd))
					{
						events.add(event);
					}
				}
			}
		}
		catch(JSONException ex)
		{
			System.out.println(ex);
		}
	}
	
	public ArrayList<EventData> getEventData()
	{
		return events;
	}
}