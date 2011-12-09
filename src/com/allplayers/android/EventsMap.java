package com.allplayers.android;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

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

					if(Globals.isUnique(event, events))
					{
						events.add(event);
					}
				}
			}
		}
		catch(JSONException ex)
		{
			System.err.println("EventsMap/" + ex);
		}
	}
	
	public ArrayList<EventData> getEventData()
	{
		return events;
	}
}