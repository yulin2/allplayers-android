package com.allplayers.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class GroupPageActivity extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		GroupData group = Globals.currentGroup;
		String title = group.getTitle();
		String desc = group.getDescription();
		
		TextView tv = new TextView(this);
		tv.setText("Title: " + title + "\n\nDescription: " + desc);
		setContentView(tv);
	}
}