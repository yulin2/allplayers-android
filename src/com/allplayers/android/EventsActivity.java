package com.allplayers.android;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class EventsActivity extends SherlockFragmentActivity implements ISideNavigationCallback {

	private SideNavigationView sideNavigationView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events);
        
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.title4);
        
        sideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);
        	
        actionbar.setDisplayHomeAsUpEnabled(true);
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
}