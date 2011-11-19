package com.allplayers.android;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MailInbox extends ListActivity
{
	
	private ArrayList<MessageData> messageList;
	private boolean hasMessages;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String jsonResult = APCI_RestServices.getUserInbox();
		
		MessagesMap messages = new MessagesMap(jsonResult);
		messageList = messages.getMessageData();
		
		String[] values;
		
		if(messageList.isEmpty())
		{
			hasMessages = false;
		}else{
			hasMessages = true;
		}
		
		if(hasMessages)
		{
			values = new String[messageList.size()];
			
			for(int i = 0; i < messageList.size(); i++)
			{
				values[i] = messageList.get(i).getSubject();
			}
		}else{
			values = new String[]{"You have no messages."};
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		
		//if(hasMessages)
		//{
		//	Globals.currentGroup = groupList.get(position);
			
			//Display the group page for the selected group
		//	Intent intent = new Intent(GroupsActivity.this, GroupPageActivity.class);
		//	startActivity(intent);
		//}
	}
}