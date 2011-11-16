package com.allplayers.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class GroupPhotosActivity extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		String jsonResult = APCI_RestServices.getGroupAlbumsByGroupId(Globals.currentGroup.getUUID());
		
		TextView tv = new TextView(this);
		tv.setText("Group Photos Activity\n\n" + jsonResult);
		setContentView(tv);
	}
}