package com.allplayers.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class GroupsActivity extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String result = APCI_RestServices.isLoggedIn();
		
		TextView tv = new TextView(this);
		tv.setText("Groups Activity\n\n" + result);
		setContentView(tv);
	}
}