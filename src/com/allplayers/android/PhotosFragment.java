package com.allplayers.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.allplayers.objects.AlbumData;
import com.allplayers.objects.GroupData;
import com.allplayers.rest.RestApiV1;

public class PhotosFragment extends ListFragment {
    private ArrayList<AlbumData> mAlbumList = new ArrayList<AlbumData>();
    private static Activity mParentActivity;
    private int mGroupCount, mNumGroupsLoaded;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity = this.getActivity();

        String jsonResult;

        if (LocalStorage.getTimeSinceLastModification("UserAlbums") / 1000 / 60 < 60) { //more recent than 60 minutes
            AlbumsMap albums;
            ArrayList<AlbumData> newAlbumList;

            String storedAlbums = LocalStorage.readUserAlbums(mParentActivity.getBaseContext());

            String[] storedAlbumsList = storedAlbums.split("\n");

            for (int i = 0; i < storedAlbumsList.length; i++) {
                albums = new AlbumsMap(storedAlbumsList[i]);
                newAlbumList = albums.getAlbumData();

                if (!newAlbumList.isEmpty()) {
                    for (int j = 0; j < newAlbumList.size(); j++) {
                        mAlbumList.add(newAlbumList.get(j));
                    }
                }
            }
        } else {
            //check local storage
            if (LocalStorage.getTimeSinceLastModification("UserGroups") / 1000 / 60 < 60) { //more recent than 60 minutes
                jsonResult = LocalStorage.readUserGroups(mParentActivity.getBaseContext());
                populateGroupAlbums(jsonResult);
            } else {
                new GetUserGroupsTask().execute();
            }
        }

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
            mGroupCount = groupList.size();
            for (int i = 0; i < mGroupCount; i++) {
                group_uuid = groupList.get(i).getUUID();
                new GetGroupAlbumsByGroupIdTask().execute(group_uuid);
            }
        }
    }

    /*
     * Gets a user's groups using a rest call.
     */
    public class GetUserGroupsTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... args) {
            return RestApiV1.getUserGroups(0, 0, null);
        }

        protected void onPostExecute(String jsonResult) {
            mNumGroupsLoaded++;
            populateGroupAlbums(jsonResult);
        }
    }

    /*
     * Gets a group's photo albums using a rest call.
     */
    public class GetGroupAlbumsByGroupIdTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... group_uuid) {
            // @TODO: Implement asynchronous loading.
            return RestApiV1.getGroupAlbumsByGroupId(group_uuid[0], 100);
        }

        protected void onPostExecute(String jsonResult) {
            AlbumsMap albums;
            ArrayList<AlbumData> newAlbumList;
            albums = new AlbumsMap(jsonResult);
            newAlbumList = albums.getAlbumData();

            if (!newAlbumList.isEmpty()) {
                for (int j = 0; j < newAlbumList.size(); j++) {
                    mAlbumList.add(newAlbumList.get(j));
                }
            }
            if (mAlbumList.isEmpty() && mNumGroupsLoaded == mGroupCount) {
                String[] values = new String[] {"no albums to display"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(PhotosFragment.mParentActivity,
                        android.R.layout.simple_list_item_1, values);
                setListAdapter(adapter);
            } else {
                //Create a customized ArrayAdapter
                // TODO Fix to use only one adapter and push new items into it.
                AlbumAdapter adapter = new AlbumAdapter(PhotosFragment.mParentActivity.getApplicationContext(), R.layout.albumlistitem, mAlbumList);
                setListAdapter(adapter);
            }
        }
    }
}
