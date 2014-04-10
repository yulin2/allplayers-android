package com.allplayers.android;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.allplayers.android.activities.AllplayersSherlockActivity;
import com.allplayers.objects.AlbumData;
import com.allplayers.objects.GroupData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

/**
 * Interface for the user to view a list of a group's photo albums.
 */
public class GroupAlbumsActivity  extends AllplayersSherlockActivity {

    private final int LIMIT = 15;
    
    private AlbumAdapter mAlbumListAdapter;
    private ArrayList<AlbumData> mAlbumList;
    private Button mLoadMoreButton;
    private GetGroupAlbumsByGroupIdTask mGetGroupAlbumsByGroupIdTask;
    private GroupData mGroup;
    private ListView mListView;
    private ProgressBar mLoadingIndicator;
    private ViewGroup mFooter;

    private boolean mEndOfData;
    private int mOffset;

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
        setContentView(R.layout.albums_list);

        // Grab the group from the intent.
        mGroup = (new Router(this)).getIntentGroup();

        // Set up the action bar.
        mActionBar = getSupportActionBar();
        mActionBar.setIcon(R.drawable.menu_icon);
        mActionBar.setTitle(mGroup.getTitle());
        mActionBar.setSubtitle("Photo Albums");

        // Set up the side navigation interface.
        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Get the group's albums.
        mGetGroupAlbumsByGroupIdTask = new GetGroupAlbumsByGroupIdTask();
        mGetGroupAlbumsByGroupIdTask.execute(mGroup);

        // Variable initialization.
        mEndOfData = false;
        mOffset = 0;
        mAlbumList = new ArrayList<AlbumData>();
        mListView = (ListView) findViewById(R.id.customListView);
        mAlbumListAdapter = new AlbumAdapter(GroupAlbumsActivity.this, R.layout.albumlistitem, mAlbumList);
        mFooter = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.load_more, null);
        mLoadMoreButton = (Button) mFooter.findViewById(R.id.load_more_button);
        mLoadingIndicator = (ProgressBar) mFooter.findViewById(R.id.loading_indicator);

        // Set up the "load more" button.
        mLoadMoreButton.setOnClickListener(new OnClickListener() {
            
            /**
             * Called when a view has been clicked.
             * 
             * @param v The view that was cliced.
             */
            @Override
            public void onClick(View v) {
                mLoadMoreButton.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                mGetGroupAlbumsByGroupIdTask = new GetGroupAlbumsByGroupIdTask();
                mGetGroupAlbumsByGroupIdTask.execute(mGroup);
            }
        });

        // Set up the list view that will show all of the data.
        mListView.addFooterView(mFooter);
        mListView.setAdapter(mAlbumListAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            
            /**
             * Callback method to be invoked when an item in this AdapterView has been clicked.
             * 
             * @param parent The AdapterView where the click happened.
             * @param view The view within the AdapterView that was clicked (this will be a view
             * provided by the adapter).
             * @param position The position of the view in the adapter.
             * @param id The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long index) {

                Intent intent = (new Router(GroupAlbumsActivity.this)).getAlbumPhotosActivityIntent(mAlbumList.get(position));
                startActivity(intent);
            }
        });
    }
    
    /**
     * Called when you are no longer visible to the user. You will next receive either onRestart(),
     * onDestroy(), or nothing, depending on later user activity.
     */
    @Override
    public void onStop() {
        super.onStop();
        
        // Cancel any running asynchronous tasks.
        if (mGetGroupAlbumsByGroupIdTask != null) {
            mGetGroupAlbumsByGroupIdTask.cancel(true);
        }
    }

    /**
     * Fetches the passed group's photo albums asynchronously.
     */
    public class GetGroupAlbumsByGroupIdTask extends AsyncTask<GroupData, Void, String> {

        /**
         * Fetch the passed in groups albums.
         *
         * @param groups: An array of groups (that only contains one group) of whose albums we need
         * to fetch. (doInBackground()'s super requires an array as the parameter)
         */
        @Override
        protected String doInBackground(GroupData... groups) {
            return RestApiV1.getGroupAlbumsByGroupId(groups[0].getUUID(), mOffset, LIMIT);
        }

        /**
         * Take the JSON result from fetching the groups albums and turn it into useful data.
         *
         * @param jsonResult: Result of fetching the group's albums.
         */
        @Override
        protected void onPostExecute(String jsonResult) {
            AlbumsMap albums = new AlbumsMap(jsonResult);

            // Check if there was any data returned. If there wasn't, either all of the data has
            // been fetched already or there wasn't any to fetch in the beginning.
            //if(albums.size() == 0) {
            if (jsonResult.equals("error")) {
                mEndOfData = true;
                mListView.removeFooterView(mFooter);

                // Check if the list of messages is empty. If so, there was never any data to be
                // fetched.
                if (mAlbumList.size() == 0) {

                    // We need to display to the user that there aren't any messages. We do this
                    // by making a blank AlbumData object.
                    AlbumData blank = new AlbumData();
                    blank.setTitle("No albums to display");
                    mAlbumList.add(blank);
                    mListView.setEnabled(false);
                    mAlbumListAdapter.notifyDataSetChanged();
                }
            }

            // If we made it here we know that there was at least part of a set of data fetched.
            else {

                // If the size of the returned data is less than the maximum size we specified, we
                // know that we have reached the end of the data to be fetched.
                if (albums.size() < LIMIT) {
                    mEndOfData = true;
                }

                mAlbumList.addAll(albums.getAlbumData());
                mAlbumListAdapter.notifyDataSetChanged();
                if (!mEndOfData) {
                    mLoadMoreButton.setVisibility(View.VISIBLE);
                    mLoadingIndicator.setVisibility(View.GONE);
                    mOffset += LIMIT;
                } else {
                    mListView.removeFooterView(mFooter);
                }
            }
        }
    }
}