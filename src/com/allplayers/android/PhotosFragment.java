package com.allplayers.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.allplayers.objects.AlbumData;
import com.allplayers.objects.GroupData;
import com.allplayers.rest.RestApiV1;

/**
 * Fragment displaying the user's photo albums.
 */
public class PhotosFragment extends ListFragment {
    
    private static Activity mParentActivity;
    private AlbumAdapter mAdapter;
    private ArrayList<AlbumData> mAlbumList = new ArrayList<AlbumData>();
    private Button mLoadMoreButton;
    private ListView mListView;
    private ProgressBar mLoadingIndicator;
    private ViewGroup mFooter;

    private boolean mCanRemoveFooter = false;
    private int mGroupCount = 0;
    private int mNumGroupsLoaded = 0;

    /**
     * Called to do initial creation of a fragment. This is called after onAttach(Activity) and
     * before onCreateView(LayoutInflater, ViewGroup, Bundle).
     * 
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity = this.getActivity();
        
        // Set up the adapter.
        mAdapter = new AlbumAdapter(mParentActivity, R.layout.albumlistitem, mAlbumList);
        
        // Grab the user's groups, we need this to get their albums. 
        new GetUserGroupsTask().execute();
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but
     * before any saved state has been restored in to the view.
     * 
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     * saved state as given here.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mFooter = (ViewGroup) LayoutInflater.from(mParentActivity).inflate(R.layout.load_more, null);
        mLoadMoreButton = (Button) mFooter.findViewById(R.id.load_more_button);
        mLoadingIndicator = (ProgressBar) mFooter.findViewById(R.id.loading_indicator);

        mLoadMoreButton.setOnClickListener(new OnClickListener() {
            
            /**
             * Called when a view has been clicked.
             * 
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                mLoadMoreButton.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                new GetUserGroupsTask().execute();
            }
        });
        mListView = getListView();
        mListView.addFooterView(mFooter);
        setListAdapter(mAdapter);
    }

    /**
     * This method will be called when an item in the list is selected.
     * 
     * @param l The ListView where the click happened.
     * @param v The view that was clicked within the ListView.
     * @param position The position of the view in the list.
     * @param id The row id of the item that was clicked.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (!mAlbumList.isEmpty()) {
            
            // Display the photos for the selected album
            Intent intent = (new Router(mParentActivity)).getAlbumPhotosActivityIntent(mAlbumList.get(position));
            startActivity(intent);
        }
    }

    /**
     * Uses the API call result passed in, and populates the UI with a list of albums from the
     * user's groups.
     * 
     * @param jsonResult The result of the API call to get the user's groups.
     */
    protected void populateGroupAlbums(String jsonResult) {
        GroupsMap groups = new GroupsMap(jsonResult);
        ArrayList<GroupData> groupList = groups.getGroupData();

        if (!groupList.isEmpty()) {
            String group_uuid;

            LocalStorage.writeUserAlbums(mParentActivity.getBaseContext(), "", true);
            mGroupCount += groupList.size();
            if (groupList.size() < 15) {
                mCanRemoveFooter = true;
            }
            for (int i = 0; i < groupList.size(); i++) {
                group_uuid = groupList.get(i).getUUID();
                new GetGroupAlbumsByGroupIdTask().execute(group_uuid);
            }
        } else if (mGroupCount == 0) {
            String[] blankMessage = {"No photos to display"};
            ArrayAdapter<String> blank = new ArrayAdapter<String>(mParentActivity, android.R.layout.simple_list_item_1, blankMessage);
            mListView.setAdapter(blank);
            mListView.setEnabled(false);
            mListView.removeFooterView(mFooter);
        }
    }

    /**
     * Fetches the user's groups from the API.
     */
    public class GetUserGroupsTask extends AsyncTask<Void, Void, String> {

        /**
         * Performs a calculation on a background thread. Fetches the user's groups from the API.
         * 
         * @return The result of the API call to fetch the user's groups.
         */
        @Override
        protected String doInBackground(Void... args) {
            return RestApiV1.getUserGroups(mGroupCount, 15, null, getActivity().getApplicationContext());
        }

        /**
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         * 
         * @param jsonResult The result of the API call.
         */
        @Override
        protected void onPostExecute(String jsonResult) {
            populateGroupAlbums(jsonResult);
        }
    }

    /**
     * Fetches a groups' photo albums from the API.
     */
    public class GetGroupAlbumsByGroupIdTask extends AsyncTask<String, Void, String> {

        /**
         * Performs a calculation on a background thread. Fetches a groups' photo albums from the
         * API.
         * 
         * @param group_uuid The UUID of the group whos photo albums are being fetched.
         * @return The result of the API call.
         */
        @Override 
        protected String doInBackground(String... groupUuid) {
            return RestApiV1.getGroupAlbumsByGroupId(groupUuid[0], 0, 100, getActivity().getApplicationContext());
        }

        /**
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         * 
         * @param jsonResult The result of the API call.
         */
        protected void onPostExecute(String jsonResult) {
            mNumGroupsLoaded++;

            AlbumsMap albums;
            ArrayList<AlbumData> newAlbumList;
            albums = new AlbumsMap(jsonResult);
            newAlbumList = albums.getAlbumData();

            if (!newAlbumList.isEmpty()) {
                mAlbumList.addAll(newAlbumList);
                mAdapter.notifyDataSetChanged();
            }
            if (mAlbumList.isEmpty() && mNumGroupsLoaded == mGroupCount) {
                String[] values = new String[] {"No albums to display"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(PhotosFragment.mParentActivity,
                        android.R.layout.simple_list_item_1, values);
                setListAdapter(adapter);
            }
            if (mNumGroupsLoaded == mGroupCount) {
                if (mCanRemoveFooter) {
                    mListView.removeFooterView(mFooter);
                } else {
                    mLoadMoreButton.setVisibility(View.VISIBLE);
                    mLoadingIndicator.setVisibility(View.GONE);
                }
            }
        }
    }
}
