package com.allplayers.android;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class PhotosActivity  extends ListActivity
{
	private ArrayList<AlbumData> albumList = new ArrayList<AlbumData>();
	
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
		
		//Create a customized ArrayAdapter
		AlbumAdapter adapter = new AlbumAdapter(getApplicationContext(), R.layout.albumlistitem, albumList);
		setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		
		if(!albumList.isEmpty())
		{
			Globals.currentAlbum = albumList.get(position);
			
			//Display the photos for the selected album
			Intent intent = new Intent(PhotosActivity.this, AlbumPhotosActivity.class);
			startActivity(intent);
		}
	}
}