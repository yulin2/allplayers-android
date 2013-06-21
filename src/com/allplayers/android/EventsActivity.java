
package com.allplayers.android;

import android.os.Bundle;

import com.allplayers.android.activities.AllplayersSherlockFragmentActivity;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

/**
 * Activity made to display EventFragment.java.
 */
public class EventsActivity extends AllplayersSherlockFragmentActivity {

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
        setContentView(R.layout.events);

        // Set up the ActionBar.
        mActionBar.setTitle("Events");

        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);
    }
    
    
    /**
     * Called when the activity has detected the user's press of the back key. The default
     * implementation simply finishes the current activity, but you can override this to do whatever
     * you want.
     */
    @Override
    public void onBackPressed() {
        
        // This is used to fix a problem with the activity stack on API 10.
        moveTaskToBack(true);
    }
}
