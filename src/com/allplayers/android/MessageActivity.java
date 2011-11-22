package com.allplayers.android;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MessageActivity extends ListActivity
{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String[] values = new String[] { "Inbox", "Sent"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		
		if(position == 0)
		{
			Intent intent = new Intent(MessageActivity.this, MessageInbox.class);
			startActivity(intent);
		}
		else if(position == 1)
		{
			Intent intent = new Intent(MessageActivity.this, MessageSent.class);
			startActivity(intent);
		}
	}
	
}