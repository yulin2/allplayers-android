package com.allplayers.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstScreen extends Activity
{
	/** called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		
		final Button logOnButton = (Button)findViewById(R.id.logOnButton);
        logOnButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	startActivity(new Intent(FirstScreen.this, Login.class));
            }
        });
        
        final Button findGroupsButton = (Button)findViewById(R.id.findGroupsButton);
        findGroupsButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	startActivity(new Intent(FirstScreen.this, FindGroups.class));
            }
        });
	}
}