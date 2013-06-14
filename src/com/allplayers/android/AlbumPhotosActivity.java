package com.allplayers.android;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.allplayers.android.activities.AllplayersSherlockActivity;
import com.allplayers.objects.AlbumData;
import com.allplayers.objects.PhotoData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class AlbumPhotosActivity extends AllplayersSherlockActivity {
    private ArrayList<PhotoData> mPhotoList = new ArrayList<PhotoData>();
    private PhotoAdapter mPhotoAdapter;
    private GridView mGridView;
    private AlbumData mAlbum;
    private ProgressBar mProgressBar;

    /**
     * Called when the activity is first created, this sets up some variables,
     * creates the Action Bar, and sets up the Side Navigation Menu.
     * @param savedInstanceState: Saved data from the last instance of the
     * activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albumdisplay);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_indicator);

        // Pull the album info from the current intent.
        mAlbum = (new Router(this)).getIntentAlbum();

        // Create our GridView and set its click listener.
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mPhotoList.get(position) != null) {
                    // Display the group page for the selected group
                    Intent intent = (new Router(AlbumPhotosActivity.this)).getPhotoPagerActivityIntent(mPhotoList.get(position));
                    intent.putExtra("album title", mAlbum.getTitle());
                    startActivity(intent);
                }
            }
        });

        // Set up the ActionBar.
        mActionBar.setTitle(mAlbum.getTitle());

        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Load the photos for the album.
        new GetAlbumPhotosByAlbumIdTask().execute(mAlbum);
    }

    /**
     * Aggregates the albums photos specified by a json string into a map.
     * @param jsonResult The album's photos.
     */
    public void updateAlbumPhotos(String jsonResult) {
        // Create the photo list from the json.
        PhotosMap photos = new PhotosMap(jsonResult);
        mPhotoList.addAll(photos.getPhotoData());

        // If there are no photos, create a blank adapter showing so.
        if (mPhotoList.isEmpty()) {
            String[] values = new String[] {"No photos to display"};
            mGridView.setAdapter(new ArrayAdapter<String>(AlbumPhotosActivity.this,
                                 android.R.layout.simple_list_item_1, values));
        } else { // If there are photos, create a custom adapter for the GridView.
            mPhotoAdapter = new PhotoAdapter(getApplicationContext(), mPhotoList);
            mGridView.setAdapter(mPhotoAdapter);
        }
    }

    /**
     * Gets the photos from an album specified by its album ID using a rest call.
     */
    public class GetAlbumPhotosByAlbumIdTask extends AsyncTask<AlbumData, Void, String> {

        /**
         * Gets the photos in a specified album using a rest call.
         */
        @Override
        protected String doInBackground(AlbumData... album) {
            return RestApiV1.getAlbumPhotosByAlbumId(album[0].getUUID(), 0, 0);
        }

        /**
         * Calls a method to organize the fetched photos into a map.
         */
        @Override
        protected void onPostExecute(String jsonResult) {
            updateAlbumPhotos(jsonResult);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
