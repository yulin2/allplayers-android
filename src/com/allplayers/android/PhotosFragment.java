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

public class PhotosFragment extends ListFragment {
    private ArrayList<AlbumData> mAlbumList = new ArrayList<AlbumData>();
    private static Activity mParentActivity;
    private int mGroupCount = 0;
    private int mNumGroupsLoaded = 0;
    private AlbumAdapter mAdapter;
    private ProgressBar mLoadingIndicator;
    private ViewGroup mFooter;
    private Button mLoadMoreButton;
    private boolean mCanRemoveFooter = false;
    private ListView mListView;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity = this.getActivity();
        mAdapter = new AlbumAdapter(mParentActivity, R.layout.albumlistitem, mAlbumList);
        new GetUserGroupsTask().execute();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mFooter = (ViewGroup) LayoutInflater.from(mParentActivity).inflate(R.layout.load_more, null);
        mLoadMoreButton = (Button) mFooter.findViewById(R.id.load_more_button);
        mLoadingIndicator = (ProgressBar) mFooter.findViewById(R.id.loading_indicator);

        mLoadMoreButton.setOnClickListener(new OnClickListener() {
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
     * Uses the json result passed in, and populates the UI with a list of albums from the
     * user's groups.
     * @param jsonResult
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

    /*
     * Gets a user's groups using a rest call.
     */
    public class GetUserGroupsTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... args) {
            return RestApiV1.getUserGroups(mGroupCount, 15, null);
        }

        protected void onPostExecute(String jsonResult) {
            populateGroupAlbums(jsonResult);
        }
    }

    /*
     * Gets a group's photo albums using a rest call.
     */
    public class GetGroupAlbumsByGroupIdTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... group_uuid) {
            return RestApiV1.getGroupAlbumsByGroupId(group_uuid[0], 0, 100);
        }

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
