
package com.allplayers.android;

import android.os.Bundle;

import com.allplayers.android.activities.AllplayersSherlockFragmentActivity;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class PhotosActivity extends AllplayersSherlockFragmentActivity {

    /**
     * Called when the activity is first created, this creates the Action Bar
     * and sets up the Side Navigation Menu.
     *
     * @param savedInstanceState: Saved data from the last instance of the
     *            activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.photos);

        actionbar = getSupportActionBar();
        actionbar.setIcon(R.drawable.menu_icon);
        actionbar.setTitle("Photo Albums");

        sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);
    }
}
