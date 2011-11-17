package com.allplayers.android;

//import java.sql.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

public class AlbumData
{
	private String uuid = "";
	private String title = "";
	private String description = "";
	private int photoCount = 0;
	//private Timestamp modifiedDate = null;
	
	public AlbumData()
	{
		
	}
	
	public AlbumData(String jsonResult)
	{
		try
		{
			JSONObject albumObject = new JSONObject(jsonResult);
			uuid = albumObject.getString("uuid");
			title = albumObject.getString("title");
			description = albumObject.getString("description");
			photoCount = albumObject.getInt("photo_count");
			//modifiedDate = Timestamp.valueOf(albumObject.getString("modified_date"));
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
	
	public String getDescription()
	{
		return description;
	}
	
	public int getPhotoCount()
	{
		return photoCount;
	}
	
	/*public Timestamp getModifedDate()
	{
		return modifiedDate;
	}*/
}