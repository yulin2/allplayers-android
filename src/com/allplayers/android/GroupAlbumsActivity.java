package com.allplayers.android;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.allplayers.objects.AlbumData;
import com.allplayers.objects.GroupData;

import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class GroupAlbumsActivity  extends SherlockListActivity implements ISideNavigationCallback{
    private ArrayList<AlbumData> albumList;
    private SideNavigationView sideNavigationView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.albums_list);
        GroupData group = (new Router(this)).getIntentGroup();

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(group.getTitle());
        actionbar.setSubtitle("Photo Albums");
        
        sideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);
        	
        actionbar.setDisplayHomeAsUpEnabled(true);
        
        GetGroupAlbumsByGroupIdTask helper = new GetGroupAlbumsByGroupIdTask();
        helper.execute(group);
    }

    @Override
    public void onSideNavigationItemClick(int itemId) {
        switch (itemId) {
            case R.id.side_navigation_menu_item1:
                invokeActivity(GroupsActivity.class);
                break;

            case R.id.side_navigation_menu_item2:
                invokeActivity(MessageActivity.class);
                break;

            case R.id.side_navigation_menu_item3:
                invokeActivity(PhotosActivity.class);
                break;

            case R.id.side_navigation_menu_item4:
                invokeActivity(EventsActivity.class);
                break;
                
            default:
                return;
        }
        finish();
    }
	
	private void invokeActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case android.R.id.home:
    			sideNavigationView.toggleMenu();
    		default:
                return super.onOptionsItemSelected(item);
    	}
    }
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (!albumList.isEmpty()) {
            //Display the photos for the selected album
            Intent intent = (new Router(this)).getAlbumPhotosActivityIntent(albumList.get(position));
            startActivity(intent);
        }
    }

    /*
     * Gets the photo albums for a group by using the groups ID with a rest call.
     */
    public class GetGroupAlbumsByGroupIdTask extends AsyncTask<GroupData, Void, String> {

        protected String doInBackground(GroupData... groups) {
            return RestApiV1.getGroupAlbumsByGroupId(groups[0].getUUID());
        }

        protected void onPostExecute(String jsonResult) {
            AlbumsMap albums = new AlbumsMap(jsonResult);
            albumList = albums.getAlbumData();
            if (albumList.isEmpty()) {
                String[] values = new String[] {"no albums to display"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(GroupAlbumsActivity.this,
                        android.R.layout.simple_list_item_1, values);
                setListAdapter(adapter);
            } else {
                //Create a customized ArrayAdapter
                AlbumAdapter adapter = new AlbumAdapter(getApplicationContext(), R.layout.albumlistitem, albumList);
                setListAdapter(adapter);
            }
        }
    }
}