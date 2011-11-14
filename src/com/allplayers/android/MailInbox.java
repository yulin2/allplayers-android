package com.allplayers.android;

//import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MailInbox extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String jsonResult = APCI_RestServices.getUserMessages();
		
		//GroupsMap groups = new GroupsMap(jsonResult);
		//ArrayList<GroupData> groupList = groups.getGroupData();
		
		//String result = "";
		
		//if(!groupList.isEmpty())
		//{
		//	for(int i = 0; i < groupList.size(); i++)
		//	{
		//		result += groupList.get(i).getTitle();
		//	}
		//}
		
		TextView tv = new TextView(this);
		tv.setText("Messages: \n\n" + jsonResult);
		setContentView(tv);
	}
}