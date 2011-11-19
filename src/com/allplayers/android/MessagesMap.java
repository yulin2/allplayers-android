package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class MessagesMap
{
	private ArrayList<MessageData> mail = new ArrayList<MessageData>();
	
	public MessagesMap(String jsonResult)
	{
		try
		{
			JSONArray jsonArray = new JSONArray(jsonResult);
			//JSONObject jsonObject = new
			
			if(jsonArray.length() > 0)
			{
				for(int i = 0; i < jsonResult.length(); i++)
				{
					mail.add(new MessageData(jsonArray.getString(i)));
				}
			}
		}
		catch(JSONException ex)
		{
			System.out.println(ex);
		}
	}
	
	public ArrayList<MessageData> getMessageData()
	{
		return mail;
	}
}