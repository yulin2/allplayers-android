package com.allplayers.android;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class GroupEventsActivity extends ListActivity
{
	private ArrayList<EventData> eventsList;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String jsonResult = APCI_RestServices.getGroupEventsByGroupId(Globals.currentGroup.getUUID());
		
		EventsMap events = new EventsMap(jsonResult);
		eventsList = events.getEventData();
		
		String[] values;
		
		if(!eventsList.isEmpty())
		{
			values = new String[eventsList.size()];
			
			for(int i = 0; i < eventsList.size(); i++)
			{
				values[i] = eventsList.get(i).getTitle();
			}
			
		}
		else
		{
			values = new String[]{"No members to display"};
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}
}