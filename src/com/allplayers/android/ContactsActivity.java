
package com.allplayers.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.MenuItem;
import com.allplayers.android.activities.AllplayersSherlockFragmentActivity;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

/**
 * Base activity to hold the contacts fragments (UserGroupmatesFragment, UserFriendsFragment, and
 * UserFamilyFragment).
 */
public class ContactsActivity extends AllplayersSherlockFragmentActivity {
    
    private Tab mFriendsTab;
    private Tab mGroupmatesTab;
    private Tab mFamilyTab;
    
    /** 
     * Called when the activity is first created.
     * 
     * @param savedInstanceState: Saved data from the last instance of the activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Set up the action bar (inherited from AllplayersSherlockFragmentActivity).
        mActionBar.setTitle("Contacts");
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the side navigation menu (inherited from AllplayersSherlockFragmentActivity).
        mSideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Set up the tabs for navigation
        mFriendsTab = mActionBar.newTab();
        mFriendsTab.setText("Friends");
        mFriendsTab.setTabListener(new ContactsTabListener<UserFriendsFragment>(this, "Friends", UserFriendsFragment.class));
        mActionBar.addTab(mFriendsTab);

        mGroupmatesTab = mActionBar.newTab();
        mGroupmatesTab.setText("Groupates");
        mGroupmatesTab.setTabListener(new ContactsTabListener<UserGroupmatesFragment>(this, "Groupmates", UserGroupmatesFragment.class));
        mActionBar.addTab(mGroupmatesTab);

        mFamilyTab = mActionBar.newTab();
        mFamilyTab.setText("Family");
        mFamilyTab.setTabListener(new ContactsTabListener<UserFamilyFragment>(this, "Family", UserFamilyFragment.class));
        mActionBar.addTab(mFamilyTab);
    }

    /**
     * Listener for the Action Bar Options Menu.
     *
     * @param item: The selected menu item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

        case android.R.id.home: {
            mSideNavigationView.toggleMenu();
            return true;
        }

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /** 
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        
        // This is used to fix a problem with the activity stack on API 10.
        moveTaskToBack(true);
    }

    public static class ContactsTabListener<T extends Fragment> implements TabListener {
        private final Activity mActivity;
        private final Class<T> mClass;
        private final String mTag;
        
        private Fragment mFragment;

        /** 
         * Constructor used each time a new tab is created.
         * 
         * @param activity  The host Activity, used to instantiate the fragment
         * @param tag  The identifier tag for the fragment
         * @param clz  The fragment's Class, used to instantiate the fragment
         */
        public ContactsTabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

        /**
         * Called when a tab enters the selected state.
         * 
         * @param tab The tab that was selected.
         * @param ft A FragmentTransaction for queuing fragment operations to execute during a tab
         * switch. The previous tab's unselect and this tab's select will be executed in a single
         * transaction. This FragmentTransaction does not support being added to the back stack.
         */
        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            
            // Check if the fragment is already initialized. If not, instantiate and add it to the
            // activity. If it exists, simply attach it in order to show it.
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(R.id.container, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        /**
         * Called when a tab exits the selected state.
         * 
         * @param tab The tab that was unselected.
         * @param ft A FragmentTransaction for queuing fragment operations to execute during a tab
         * switch. This tab's unselect and the newly selected tab's select will be executed in a
         * single transaction. This FragmentTransaction does not support being added to the back
         * stack.
         */
        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                
                // Detach the fragment, because another one is being attached
                ft.detach(mFragment);
            }
        }

        /**
         * Called when a tab that is already selected is chosen again by the user.
         * 
         * @param tab The tab that was reselected.
         * @param ft A FragmentTransaction for queuing fragment operations to execute once this
         * method returns. This FragmentTransaction does not support being added to the back stack.
         */
        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // UNUSED
        }
    }
}
