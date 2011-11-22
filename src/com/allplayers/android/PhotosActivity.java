package com.allplayers.android;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PhotosActivity  extends ListActivity
{
	private ArrayList<AlbumData> albumList = new ArrayList<AlbumData>();
	private boolean hasAlbums = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if(Globals.groupList.isEmpty())
		{
			String jsonResult = APCI_RestServices.getUserGroups();
			
			GroupsMap groups = new GroupsMap(jsonResult);
			Globals.groupList = groups.getGroupData();
		}
		
		ArrayList<GroupData> groupList = Globals.groupList;
		
		if(!groupList.isEmpty())
		{
			String group_uuid;
			String jsonResult;
			AlbumsMap albums;
			ArrayList<AlbumData> newAlbumList;
			
			for(int i = 0; i < groupList.size(); i++)
			{
				group_uuid = groupList.get(i).getUUID();
				jsonResult = APCI_RestServices.getGroupAlbumsByGroupId(group_uuid);
				albums = new AlbumsMap(jsonResult);
				newAlbumList = albums.getAlbumData();
				
				if(!newAlbumList.isEmpty())
				{
					for(int j = 0; j < newAlbumList.size(); j++)
					{
						albumList.add(newAlbumList.get(j));
					}
				}
			}
		}
		
		String[] values;
		
		if(!albumList.isEmpty())
		{
			values = new String[albumList.size()];
			
			for(int i = 0; i < albumList.size(); i++)
			{
				values[i] = albumList.get(i).getTitle();
			}
			
			hasAlbums = true;
		}
		else
		{
			values = new String[]{"No albums to display"};
			hasAlbums = false;
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		
		if(hasAlbums)
		{
			Globals.currentAlbum = albumList.get(position);
			
			//Display the photos for the selected album
			Intent intent = new Intent(PhotosActivity.this, AlbumPhotosActivity.class);
			startActivity(intent);
		}
	}
}