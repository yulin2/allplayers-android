package com.allplayers.android;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.AlbumData;
import com.allplayers.objects.GroupData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class GroupAlbumsActivity  extends AllplayersSherlockListActivity {
    private ProgressBar mProgressBar;
    private ArrayList<AlbumData> mAlbumList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.albums_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_indicator);

        GroupData group = (new Router(this)).getIntentGroup();

        actionbar.setTitle(group.getTitle());
        actionbar.setSubtitle("Photo Albums");

        sideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);

        GetGroupAlbumsByGroupIdTask helper = new GetGroupAlbumsByGroupIdTask();
        helper.execute(group);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (!mAlbumList.isEmpty()) {
            //Display the photos for the selected album
            Intent intent = (new Router(this)).getAlbumPhotosActivityIntent(mAlbumList.get(position));
            startActivity(intent);
        }
    }

    /*
     * Gets the photo albums for a group by using the groups ID with a rest call.
     */
    public class GetGroupAlbumsByGroupIdTask extends AsyncTask<GroupData, Void, String> {

        protected String doInBackground(GroupData... groups) {
            // @TODO: Move to asynchronous loading.
            return RestApiV1.getGroupAlbumsByGroupId(groups[0].getUUID(), 0);
        }

        protected void onPostExecute(String jsonResult) {
            AlbumsMap albums = new AlbumsMap(jsonResult);
            mAlbumList = albums.getAlbumData();

            if (mAlbumList.isEmpty()) {
                String[] values = new String[] {"no albums to display"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(GroupAlbumsActivity.this,
                        android.R.layout.simple_list_item_1, values);
                setListAdapter(adapter);
            } else {
                //Create a customized ArrayAdapter
                AlbumAdapter adapter = new AlbumAdapter(getApplicationContext(), R.layout.albumlistitem, mAlbumList);
                setListAdapter(adapter);
            }
            mProgressBar.setVisibility(View.GONE);
        }
    }
}