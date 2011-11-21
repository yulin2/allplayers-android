package com.allplayers.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageViewSingle extends Activity
{
	/** called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.viewsinglemessage);
		
		
		final TextView subjectText = (TextView)findViewById(R.id.subjectText);
		subjectText.setText("This is the Subject.");
		
		final TextView bodyText = (TextView)findViewById(R.id.bodyText);
		bodyText.setText("This is the body text.");
		
		final Button replyButton = (Button)findViewById(R.id.replyButton);
		
        replyButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	//startActivity(new Intent(Login.this, FindGroupsActivity.class));
            }
        });
	}
}