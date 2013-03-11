package com.allplayers.android;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

import android.content.Intent;
import android.os.Bundle;

public class MessageActivity extends SherlockFragmentActivity implements ISideNavigationCallback{

	private ActionBar actionbar;
	private SideNavigationView sideNavigationView;
	
	/**
	 * Called when the activity is first created, this creates the Action Bar
	 * and sets up the Side Navigation Menu.
	 * @param savedInstanceState: Saved data from the last instance of the
	 * activity.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.messages);
        
        actionbar = getSupportActionBar();
        actionbar.setIcon(R.drawable.menu_icon);
        actionbar.setTitle("Messages");

        sideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);
    }
	
	/**
	 * Creates the Action Bar Options Menu. 
	 * @param menu: The menu to be created.
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.defaultmenu, menu);
        
        return true;
    }

	/**
	 * Listener for the Action Bar Options Menu.
	 * @param item: The selected menu item.
	 * TODO: Add options for:
	 * 			Logout
	 * 			Search
	 * 			Refresh
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
}