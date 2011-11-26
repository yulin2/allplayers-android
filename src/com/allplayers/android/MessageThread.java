package com.allplayers.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MessageThread extends ListActivity
{
	
	private ArrayList<MessageData> messageList;
	private boolean hasMessages = false;
	private String jsonResult = "";
	
	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(2);
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//jsonResult = APCI_RestServices.getUserInbox();
		
		MessageData message = Globals.currentMessage;
		String threadID = message.getThreadID();
		
		jsonResult = APCI_RestServices.getUserMessagesByThreadId(threadID);
		
		System.out.println(jsonResult);

		HashMap<String, String> map;
		
		
		
		map = new HashMap<String, String>();
		map.put("line1", "This is here for testing purpose");
		map.put("line2", "It will be taken away soon");
		list.add(map);
		//MessagesMap messages = new MessagesMap(jsonResult);
		//messageList = messages.getMessageData();
/*
		if(!messageList.isEmpty())
		{
			hasMessages = true;
			
			for(int i = 0; i < messageList.size(); i++)
			{
				map = new HashMap<String, String>();
				map.put("line1", messageList.get(i).getSubject());
				
				if(Integer.parseInt(messageList.get(i).getNew()) == 1)
					map.put("line2", "Unread");
				else
					map.put("line2", "Read");
					
				list.add(map);
			}
		}
		else
		{
			hasMessages = false;
			
			map = new HashMap<String, String>();
			map.put("line1", "You have no new messages.");
			map.put("line2", "");
			list.add(map);
		}
		
*/
		String[] from = { "line1", "line2" };

		int[] to = { android.R.id.text1, android.R.id.text2 };

		SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, from, to);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		/*
		if(hasMessages)
		{
			//Globals.currentMessage = jsonResult;
			//Globals.currentMessageLoc = position;
			//Globals.currentMessage = messageList.get(position);

			Intent intent = new Intent(MessageThread.this, MessageViewSingle.class);
			startActivity(intent);
		}*/
	}
}