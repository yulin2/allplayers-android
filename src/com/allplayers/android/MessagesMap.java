package com.allplayers.android;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessagesMap
{
	private ArrayList<MessageData> mail = new ArrayList<MessageData>();
	private String[] names;
	
	public MessagesMap(String jsonResult)
	{
		try
		{
			JSONObject jsonObject = new JSONObject(jsonResult);
			
			names = getNames(jsonObject);
			
			if(names.length > 0)
			{
				for(int i = 0; i < names.length; i++)
				{
					mail.add(new MessageData(jsonObject.getString(names[i])));
				}
			}
		}
		catch(JSONException ex)
		{
			System.out.println(ex);
		}
	}
	
	private static String[] getNames( JSONObject jo ) {
		int length = jo.length();
		if (length == 0) return null;
		Iterator iterator = jo.keys();
		String[] names = new String[length];
		int i = 0;
		while (iterator.hasNext()) {
			names[i] = (String)iterator.next();
			i += 1;
		}
		return names;
	}
	
	public ArrayList<MessageData> getMessageData()
	{
		return mail;
	}
}