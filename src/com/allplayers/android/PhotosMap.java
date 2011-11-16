package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class PhotosMap
{
	private ArrayList<PhotoData> photos = new ArrayList<PhotoData>();
	
	public PhotosMap(String jsonResult)
	{
		if(!jsonResult.equals("false"))
		{
			try
			{
				JSONArray jsonArray = new JSONArray(jsonResult);
				
				if(jsonArray.length() > 0)
				{
					for(int i = 0; i < jsonResult.length(); i++)
					{
						photos.add(new PhotoData(jsonArray.getString(i)));
					}
				}
			}
			catch(JSONException ex)
			{
				System.out.println(ex);
			}
		}
	}
	
	public ArrayList<PhotoData> getPhotoData()
	{
		return photos;
	}
}