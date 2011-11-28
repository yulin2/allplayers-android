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
						PhotoData pd = new PhotoData(jsonArray.getString(i));
						
						if(pd.getPhotoFull() != null && !pd.getPhotoFull().trim().equals(""))
						{
							photos.add(pd);
						}
					}
				}
			}
			catch(JSONException ex)
			{
				System.err.println("PhotosMap/" + ex);
			}
		}
	}
	
	public ArrayList<PhotoData> getPhotoData()
	{
		return photos;
	}
}