package com.allplayers.android;

import com.allplayers.rest.RestApiV1;
import com.allplayers.objects.AlbumData;
import com.allplayers.objects.PhotoData;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class AlbumPhotosActivity extends ListActivity {
    private ArrayList<PhotoData> photoList;
    private ArrayAdapter adapter = null;
    private int currentPage;
    private int currentAmountShown;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlbumData album = (new Router(this)).getIntentAlbum();
        photoList = new ArrayList<PhotoData>();
        GetAlbumPhotosByAlbumIdTask helper = new GetAlbumPhotosByAlbumIdTask();
        helper.execute(album);
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

    public void setAdapter() {
        if (adapter == null) {
            if (photoList.isEmpty()) {
                String[] values = new String[] {"no photos to display"};

                adapter = new ArrayAdapter<String>(AlbumPhotosActivity.this,
                                                   android.R.layout.simple_list_item_1, values);
                setListAdapter(adapter);
            } else {
                //Create a customized ArrayAdapter
                adapter = new PhotoAdapter(getApplicationContext(), R.layout.photolistitem);
                setListAdapter(adapter);
            }
        }
    }

    public void updateAlbumPhotos(String jsonResult) {
        PhotosMap photos = new PhotosMap(jsonResult);
        photoList.addAll(photos.getPhotoData());
        setAdapter();
        for (int i = currentAmountShown; i < photoList.size(); i++, currentAmountShown++) {
            adapter.add(photoList.get(i));
        }
    }

    /*
     * Gets the photos from an album specified by its album ID using a rest call.
     */
    public class GetAlbumPhotosByAlbumIdTask extends AsyncTask<AlbumData, Void, String> {

        protected String doInBackground(AlbumData... album) {
            return RestApiV1.getAlbumPhotosByAlbumId(album[0].getUUID(), currentPage++ * 5, 5);
        }

        protected void onPostExecute(String jsonResult) {
            updateAlbumPhotos(jsonResult);
        }
    }
}
