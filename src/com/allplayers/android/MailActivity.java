package com.allplayers.android;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MailActivity extends ListActivity
{
	
	//final Intent mailInbox = new Intent(this, MailInbox.class);
	//final Intent mailCreate = new Intent(this, MailCreate.class);
	//final Intent mailSent = new Intent(this, MailSent.class);
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String[] values = new String[] { "Create New", "Inbox", "Sent"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if(position == 0)
		{
			Toast.makeText(this, "You clicked \"Create New\".", Toast.LENGTH_SHORT).show(); //this is where we would send them to the CreateNewMail.class activity
		}
		else if(position == 1)
		{
			//startActivity(mailInbox);
			//Toast.makeText(this, "You clicked \"Inbox\".", Toast.LENGTH_SHORT).show(); //this is where we would send them to the MailInbox.class activity
		}
		else if(position == 2)
		{
			Toast.makeText(this, "You clicked \"Sent\".", Toast.LENGTH_SHORT).show(); //this is where we would send them to the MailSent.class activity
		}
	}
}