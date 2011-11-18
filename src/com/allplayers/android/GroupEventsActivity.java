package com.allplayers.android;

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;

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
			values = new String[]{"No events to display"};
		}
		
		/*Cursor cursor = null;
		int[] to = new int[]{}; //Which fields to insert the data into
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, 
				android.R.layout.simple_list_item_2, cursor, values, to);
		setListAdapter(adapter);*/
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}
}