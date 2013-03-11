package com.allplayers.android;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.allplayers.objects.GroupData;
import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupPageActivity extends SherlockActivity implements ISideNavigationCallback {
    private GroupData group;
    private ArrayList<GroupMemberData> membersList;
    
	// Used for the Side Navigation Menu.
    private SideNavigationView sideNavigationView;
    
    /**
	 * Called when the activity is first created, this creates the Action Bar
	 * and sets up the Side Navigation Menu as well as assigning some variables.
	 * @param savedInstanceState: Saved data from the last instance of the
	 * activity.
	 */  
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

        group = (new Router(this)).getIntentGroup();

        setButtonState(group.getUUID());

        setContentView(R.layout.grouppage);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(group.getTitle());
        actionbar.setDisplayHomeAsUpEnabled(true);

        sideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);
        	        
        GetRemoteImageTask helper = new GetRemoteImageTask();
        helper.execute(group.getLogo());
    }

    /**
	 * Listener for the Action Bar Options Menu.
	 * @param item: The selected menu item.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
    	switch(item.getItemId()) {
    	
    		case android.R.id.home:
    			sideNavigationView.toggleMenu();
    			
    		default:
                return super.onOptionsItemSelected(item);
    	}
    }
	
	/**
	 * Listener for the Side Navigation Menu.
	 * @param itemId: The ID of the list item that was selected.
	 */
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
	
	/**
	 * Helper method for onSideNavigationItemClick. Starts the passed in
	 * activity.
	 * @param activity: The activity to be started.
	 */
	private void invokeActivity(Class activity) {
		
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        
        overridePendingTransition(0, 0); // Disables new activity animation.
    }
    
    /**
     * Checks if the user is a member of the group in order to determine if they should have
     * access to the buttons provided to view members, events, and photos.
     *
     * @param group_uuid: The uuid of the group to be checked.
     */
    private void setButtonState(String group_uuid) {
    	
        GetGroupMembersByGroupIdTask helper = new GetGroupMembersByGroupIdTask();
        helper.execute(group_uuid);
    }

    /**
     * Gets a remote image using a REST call.
     */
    public class GetRemoteImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... logoURL) {
        	
            return RestApiV1.getRemoteImage(logoURL[0]);
        }

        protected void onPostExecute(Bitmap logo) {
        	
            ImageView imView = (ImageView)findViewById(R.id.groupLogo);
            imView.setImageBitmap(logo);
        }
    }

    /*
     * Checks if the user is a group member. If the user is a group member the group page interface is set up.
     */
    public class GetGroupMembersByGroupIdTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... group_uuid) {
            return RestApiV1.getGroupMembersByGroupId(group_uuid[0]);
        }

        protected void onPostExecute(String jsonResult) {
            boolean isMember = false;
            boolean isLoggedIn = RestApiV1.isLoggedIn();
            GroupMembersMap groupMembers = new GroupMembersMap(jsonResult);
            membersList = groupMembers.getGroupMemberData();
            String currentUUID = RestApiV1.getCurrentUserUUID();
            for (int i = 0; i < membersList.size(); i++) {
                if (membersList.get(i).getUUID().equals(currentUUID)) {
                    isMember = true;
                    break;
                }
            }

            final Button groupMembersButton = (Button)findViewById(R.id.groupMembersButton);
            if (isMember && isLoggedIn) groupMembersButton.setVisibility(View.VISIBLE);
            groupMembersButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = (new Router(GroupPageActivity.this)).getGroupMembersActivityIntent(group);
                    startActivity(intent);
                }
            });

            final Button groupEventsButton = (Button)findViewById(R.id.groupEventsButton);
            if (isLoggedIn) groupEventsButton.setVisibility(View.VISIBLE);
            groupEventsButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = (new Router(GroupPageActivity.this)).getGroupEventsActivityIntent(group);
                    startActivity(intent);
                }
            });

            final Button groupPhotosButton = (Button)findViewById(R.id.groupPhotosButton);
            if (isLoggedIn) groupPhotosButton.setVisibility(View.VISIBLE);
            groupPhotosButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = (new Router(GroupPageActivity.this)).getGroupAlbumsActivityIntent(group);
                    startActivity(intent);
                }
            });

            TextView groupInfo = (TextView)findViewById(R.id.groupDetails);
            groupInfo.setText("Title: " + group.getTitle() + "\n\nDescription: " + group.getDescription());
        }
    }
}