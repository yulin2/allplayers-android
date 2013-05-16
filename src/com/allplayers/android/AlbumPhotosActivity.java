package com.allplayers.android;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    private ArrayList<PhotoData> mPhotoList;
    private PhotoAdapter mPhotoAdapter;
    private GridView mGridView;
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

        final AlbumData album = (new Router(this)).getIntentAlbum();
        mPhotoList = new ArrayList<PhotoData>();
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mPhotoList.get(position) != null) {
                    // Display the group page for the selected group
                    Intent intent = (new Router(AlbumPhotosActivity.this)).getPhotoPagerActivityIntent(mPhotoList.get(position));
                    intent.putExtra("album title", album.getTitle());
                    startActivity(intent);
                }
            }
        });

        mActionBar.setTitle(album.getTitle());

        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        new GetAlbumPhotosByAlbumIdTask().execute(album);
    }

    /**
     * Creates an array adapter to store the album's photos.
     */
    public void setAdapter() {

        if (mPhotoList.isEmpty()) {
            String[] values = new String[] {"no photos to display"};

            mGridView.setAdapter(new ArrayAdapter<String>(AlbumPhotosActivity.this,
                                 android.R.layout.simple_list_item_1, values));
        } else {
            mPhotoAdapter = new PhotoAdapter(getApplicationContext());
            mGridView.setAdapter(mPhotoAdapter);
        }
    }

    /**
     * Aggregates the albums photos specified by a json string into a map.
     * @param jsonResult The album's photos.
     */
    public void updateAlbumPhotos(String jsonResult) {

        PhotosMap photos = new PhotosMap(jsonResult);
        mPhotoList.addAll(photos.getPhotoData());

        setAdapter();

        if (mPhotoList.size() != 0) {
            mPhotoAdapter.addAll(mPhotoList);
        }
    }

    /**
     * Gets the photos from an album specified by its album ID using a rest call.
     */
    public class GetAlbumPhotosByAlbumIdTask extends AsyncTask<AlbumData, Void, String> {

        /**
         * Gets the photos in a specified album using a rest call.
         */
        protected String doInBackground(AlbumData... album) {
            return RestApiV1.getAlbumPhotosByAlbumId(album[0].getUUID(), 0, 0);
        }

        /**
         * Calls a method to organize the fetched photos into a map.
         */
        protected void onPostExecute(String jsonResult) {
            updateAlbumPhotos(jsonResult);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
