package com.allplayers.android;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GroupAlbumsActivity  extends ListActivity
{
	private ArrayList<AlbumData> albumList;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		String jsonResult = APCI_RestServices.getGroupAlbumsByGroupId(Globals.currentGroup.getUUID());
		AlbumsMap albums = new AlbumsMap(jsonResult);
		albumList = albums.getAlbumData();
		
		String[] values;
		
		if(!albumList.isEmpty())
		{
			values = new String[albumList.size()];
			
			for(int i = 0; i < albumList.size(); i++)
			{
				values[i] = albumList.get(i).getTitle();
			}
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, values);
			setListAdapter(adapter);
		}
		else
		{
			TextView tv = new TextView(this);
			tv.setText("No albums to display");
			setContentView(tv);
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		
		Globals.currentAlbum = albumList.get(position);
		
		//Display the group page for the selected group
		Intent intent = new Intent(GroupAlbumsActivity.this, AlbumPhotosActivity.class);
		startActivity(intent);
		
	}
}