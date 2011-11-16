package com.allplayers.android;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AlbumPhotosActivity extends ListActivity
{
	private ArrayList<PhotoData> photoList;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		String jsonResult = APCI_RestServices.getAlbumPhotosByAlbumId(Globals.currentAlbum.getUUID());
		PhotosMap photos = new PhotosMap(jsonResult);
		photoList = photos.getPhotoData();
		
		String[] values;
		
		if(!photoList.isEmpty())
		{
			values = new String[photoList.size()];
			
			for(int i = 0; i < photoList.size(); i++)
			{
				values[i] = photoList.get(i).getTitle();
			}
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, values);
			setListAdapter(adapter);
		}
		else
		{
			TextView tv = new TextView(this);
			tv.setText("No photos to display");
			setContentView(tv);
		}
	}
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		
		Globals.currentPhoto = photoList.get(position);
		
		//Display the group page for the selected group
		Intent intent = new Intent(AlbumPhotosActivity.this, PhotoDisplayActivity.class);
		startActivity(intent);
		
	}
}