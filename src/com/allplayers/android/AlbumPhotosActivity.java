package com.allplayers.android;

import com.allplayers.rest.RestApiV1;
import com.allplayers.objects.AlbumData;
import com.allplayers.objects.PhotoData;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class AlbumPhotosActivity extends ListActivity {
    private ArrayList<PhotoData> photoList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlbumData album = (new Router(this)).getIntentAlbum();
        String jsonResult = RestApiV1.getAlbumPhotosByAlbumId(album.getUUID());
        PhotosMap photos = new PhotosMap(jsonResult);
        photoList = photos.getPhotoData();

        if (photoList.isEmpty()) {
            String[] values = new String[] {"no photos to display"};

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
        } else {
            //Create a customized ArrayAdapter
            PhotoAdapter adapter = new PhotoAdapter(getApplicationContext(), R.layout.photolistitem, photoList);
            setListAdapter(adapter);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (!photoList.isEmpty()) {
            // Display the group page for the selected group
            Intent intent = (new Router(this)).getPhotoDisplayActivityIntent(photoList.get(position));
            startActivity(intent);
        }
    }
}