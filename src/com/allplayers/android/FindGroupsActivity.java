package com.allplayers.android;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FindGroupsActivity extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findgroups);
		
		final Button logOnButton = (Button) findViewById(R.id.searchGroupsButton);
        logOnButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	EditText searchEditText = (EditText)findViewById(R.id.searchGroupsField);
            	String search = searchEditText.getText().toString();
            	
            	String jsonResult = APCI_RestServices.searchGroups(search);
            	
            	GroupsMap groups = new GroupsMap(jsonResult);
        		ArrayList<GroupData> groupList = groups.getGroupData();
        		
        		String result = "";
        		
        		if(!groupList.isEmpty())
        		{
        			for(int i = 0; i < groupList.size(); i++)
        			{
        				result += groupList.get(i).getTitle();
        			}
        		}
				
				TextView resultView = (TextView)findViewById(R.id.searchGroupsResults);
				resultView.setText(result);
            }
        });
	}
}