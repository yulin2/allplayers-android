package com.allplayers.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
//import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MessageInbox extends ListActivity
{
	
	private ArrayList<MessageData> messageList;
	private boolean hasMessages;
	
	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(2);
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String jsonResult = APCI_RestServices.getUserInbox();
		
		HashMap<String, String> map;
		
		MessagesMap messages = new MessagesMap(jsonResult);
		ArrayList<MessageData> messageList = messages.getMessageData();
		
		if(messageList.isEmpty())
		{
			hasMessages = false;
		}else{
			hasMessages = true;
		}

		if(hasMessages)
		{
			//values = new String[messageList.size()];
			
			for(int i = 0; i < messageList.size(); i++)
			{
				map = new HashMap<String, String>();
				map.put("line1", messageList.get(i).getSubject());
				map.put("line2", "Last sent from: " + messageList.get(i).getLastSender());
				list.add(map);
				//values[i] = messageList.get(i).getSubject();
			}
		}else{
			map = new HashMap<String, String>();
			map.put("line1", "You have no new messages.");
			map.put("line2", "");
			list.add(map);
		}
		

		String[] from = { "line1", "line2" };

		int[] to = { android.R.id.text1, android.R.id.text2 };

		SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, from, to);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		
		Intent intent = new Intent(MessageInbox.this, MessageViewSingle.class);
		startActivity(intent);
		
		//if(hasMessages)
		//{
		//	Globals.currentGroup = groupList.get(position);
			
			//Display the group page for the selected group
		//	Intent intent = new Intent(GroupsActivity.this, GroupPageActivity.class);
		//	startActivity(intent);
		//}
	}
}