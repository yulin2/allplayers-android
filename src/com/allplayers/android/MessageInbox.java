package com.allplayers.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageInbox extends Activity 
{
	
	private ArrayList<MessageData> messageList;
	private String jsonResult = "";
	private boolean hasMessages = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inboxlist);

        try
		{
			Bundle bundle = this.getIntent().getExtras();
			jsonResult = bundle.getString("inboxJSON");
		}
		catch(Throwable t)
		{
		}
		
		if(jsonResult.equals(""))
		{
			jsonResult = APCI_RestServices.getUserInbox();
		}
        
        MessagesMap messages = new MessagesMap(jsonResult);
		messageList = messages.getMessageData();

		Collections.reverse(messageList);
		
        ListView list = (ListView) findViewById(R.id.customListView);
        list.setClickable(true);
        
        if(!messageList.isEmpty())
		{
			hasMessages = true;
		}
        else
		{
			hasMessages = false;
		}

        final List<MessageData> messageList2 = messageList;
        MessageAdapter adapter = new MessageAdapter(this, messageList2);

        list.setOnItemClickListener(new OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> arg0, View view, int position, long index) 
            {
            	if(hasMessages)
        		{
        			Globals.currentMessage = messageList.get(position);

        			Intent intent = new Intent(MessageInbox.this, MessageThread.class);
        			startActivity(intent);
        		}
            }
        });

        list.setAdapter(adapter);
    }

}