package com.allplayers.android;

import org.json.JSONException;
import org.json.JSONObject;

public class PhotoData
{
	private String uuid = "";
	private String groupUUID = "";
	private String title = "";
	private String description = "";
	private String photoFull = "";
	private String photoThumb = "";
	
	public PhotoData()
	{
		
	}
	
	public PhotoData(String jsonResult)
	{
		try
		{
			JSONObject photoObject = new JSONObject(jsonResult);
			uuid = photoObject.getString("uuid");
			groupUUID = photoObject.getString("group_uuid");
			title = photoObject.getString("title");
			description = photoObject.getString("description");
			photoFull = photoObject.getString("photo_full");
			photoThumb = photoObject.getString("photo_thumb");
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
	
	public String getGroupUUID()
	{
		return groupUUID;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getPhotoFull()
	{
		return photoFull;
	}
	
	public String getPhotoThumb()
	{
		return photoThumb;
	}
}