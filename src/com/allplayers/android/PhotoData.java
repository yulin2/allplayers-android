package com.allplayers.android;

import org.json.JSONException;
import org.json.JSONObject;

public class PhotoData
{
	private String uuid = "";
	private String title = "";
	private String url = "";
	
	public PhotoData()
	{
		
	}
	
	public PhotoData(String jsonResult)
	{
		try
		{
			JSONObject photoObject = new JSONObject(jsonResult);
			uuid = photoObject.getString("uuid");
			title = photoObject.getString("title");
			url = photoObject.getString("logo");
		}
		catch(JSONException ex)
		{
			System.out.println(ex);
		}
	}
	
	public String getUUID()
	{
		return uuid;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getURL()
	{
		return url;
	}
}