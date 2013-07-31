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

/**
 * Displays the photos in an album in a grid.
 */
public class AlbumPhotosActivity extends AllplayersSherlockActivity {
    
    private AlbumData mAlbum;
    private ArrayList<PhotoData> mPhotoList;
    private GetAlbumPhotosByAlbumIdTask mGetAlbumPhotosByAlbumIdTask;
    private GridView mGridView;
    private PhotoAdapter mPhotoAdapter;
    private ProgressBar mProgressBar;

    /**
     * Called when the activity is starting.
     * 
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in
     * onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albumdisplay);
        
        // Link up with the XML file.
        mGridView = (GridView) findViewById(R.id.gridview);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_indicator);

        // Pull the album info from the current intent.
        mAlbum = (new Router(this)).getIntentAlbum();
        
        // Variable declaration.
        mPhotoList = new ArrayList<PhotoData>();

        // Setup the listener for the grid view.
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            
            /**
             * Callback method to be invoked when an item in this AdapterView has been clicked.
             * 
             * @param parent The AdapterView where the click happened.
             * @param v The view within the AdapterView that was clicked.
             * @param position The position of the view in the adapter.
             * @param id The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                
                // If there is a photo in the selected position that is clicked on, open the pager
                // for photo viewing.
                if (mPhotoList.get(position) != null) {
                    
                    // Display the album's photos.
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
        mGetAlbumPhotosByAlbumIdTask = new GetAlbumPhotosByAlbumIdTask();
        mGetAlbumPhotosByAlbumIdTask.execute(mAlbum);
    }
    
    /**
     * Called when you are no longer visible to the user. You will next receive either onRestart(),
     * onDestroy(), or nothing, depending on later user activity.
     */
    @Override
    public void onStop() {
        super.onStop();
        
        // Stop any asynchronous tasks that we have running.
        if (mGetAlbumPhotosByAlbumIdTask != null) {
            mGetAlbumPhotosByAlbumIdTask.cancel(true);
        }
    }

    /**
     * Aggregates the albums photos specified by a json string into a map.
     * @param jsonResult The album's photos.
     */
    public void updateAlbumPhotos(String jsonResult) {
        
        // Create the photo list from the result of the API call.
        PhotosMap photos = new PhotosMap(jsonResult);
        mPhotoList.addAll(photos.getPhotoData());

        // If there are no photos, create a blank adapter showing so. If there are photos, create a
        // custom adapter for the GridView.
        if (mPhotoList.isEmpty()) {
            
            String[] values = new String[] {"No photos to display"};
            mGridView.setAdapter(new ArrayAdapter<String>(AlbumPhotosActivity.this,
                                 android.R.layout.simple_list_item_1, values));
        } else {
            
            mPhotoAdapter = new PhotoAdapter(getApplicationContext(), mPhotoList);
            mGridView.setAdapter(mPhotoAdapter);
        }
    }

    /**
     * Gets the photos from an album specified by its album ID.
     */
    public class GetAlbumPhotosByAlbumIdTask extends AsyncTask<AlbumData, Void, String> {

        /**
         * Gets the photos in a specified album using a rest call.
         */
        @Override
        protected String doInBackground(AlbumData... album) {
            return RestApiV1.getAlbumPhotosByAlbumId(album[0].getUUID(), 0, 0, getApplicationContext());
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
