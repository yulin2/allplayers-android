package com.allplayers.android;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupEventsActivity extends ListActivity
{
	private ArrayList<EventData> eventsList;
	private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(2);
	private boolean hasEvents = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String jsonResult = APCI_RestServices.getGroupEventsByGroupId(Globals.currentGroup.getUUID());
		
		EventsMap events = new EventsMap(jsonResult);
		eventsList = events.getEventData();
		HashMap<String, String> map;
		
		if(!eventsList.isEmpty())
		{
			for(int i = 0; i < eventsList.size(); i++)
			{
				map = new HashMap<String, String>();
				map.put("line1", eventsList.get(i).getTitle());
				
				String start = eventsList.get(i).getStartDateString();
				map.put("line2", start);
				list.add(map);
			}
			
			hasEvents = true;
		}
		else
		{
			map = new HashMap<String, String>();
			map.put("line1", "No events to display.");
			map.put("line2", "");
			list.add(map);
			hasEvents = false;
		}
		
		String[] from = {"line1", "line2"};

		int[] to = {android.R.id.text1, android.R.id.text2};

		SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, from, to);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		
		if(hasEvents)
		{
			Globals.currentEvent = eventsList.get(position);
			Intent intent = new Intent(GroupEventsActivity.this, EventDisplayActivity.class); //Can be used to display map or full details
			startActivity(intent);
		}
	}
}