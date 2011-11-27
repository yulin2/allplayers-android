package com.allplayers.android;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MessageViewSingle extends Activity
{
	/** called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.viewsinglemessage);
		
		MessageData message = Globals.currentMessage;
		
		String subject = message.getSubject();
		String sender = message.getLastSender();
		Date date = message.getDate();
		String body = message.getMessageBody();
		int threadID = Integer.parseInt(message.getThreadID());
		int isNew = Integer.parseInt(message.getNew());
		
		if(isNew == 1)
		{
			APCI_RestServices.putMessage(threadID, 0, "");
		}
		
		final TextView subjectText = (TextView)findViewById(R.id.subjectText);
		subjectText.setText("This is the Subject.");
		subjectText.setText(subject);
		
		final TextView senderText = (TextView)findViewById(R.id.senderText);
		senderText.setText("This is the Sender's Name.");
		senderText.setText("From: " + sender);
		
		final TextView dateText = (TextView)findViewById(R.id.dateText);
		dateText.setText("This is the Date last sent.");
		dateText.setText("Last Message: " + date);
		
		final TextView bodyText = (TextView)findViewById(R.id.bodyText);
		bodyText.setText("This is the body text.");
		bodyText.setText(body);
		bodyText.setMovementMethod(LinkMovementMethod.getInstance());
		
		final Button replyButton = (Button)findViewById(R.id.replyButton);
		
        replyButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	startActivity(new Intent(MessageViewSingle.this, MessageReply.class));
            }
        });
	}
}