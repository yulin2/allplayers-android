
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
    
    // Tabs used for navigation between the contacts fragments.
    private Tab mFriendsTab;
    private Tab mGroupmatesTab;
    private Tab mFamilyTab;
    
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
        setContentView(R.layout.activity_contacts);

        // Set up the ActionBar.
        mActionBar.setTitle("Contacts");
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the Side Navigation Menu.
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
        mGroupmatesTab.setText("Groupmates");
        mGroupmatesTab.setTabListener(new ContactsTabListener<UserGroupmatesFragment>(this, "Groupmates", UserGroupmatesFragment.class));
        mActionBar.addTab(mGroupmatesTab);

        mFamilyTab = mActionBar.newTab();
        mFamilyTab.setText("Family");
        mFamilyTab.setTabListener(new ContactsTabListener<UserFamilyFragment>(this, "Family", UserFamilyFragment.class));
        mActionBar.addTab(mFamilyTab);
    }

    /**
     * This hook is called whenever an item in your options menu is selected. The default
     * implementation simply returns false to have the normal processing happen (calling the item's
     * Runnable or sending a message to its Handler as appropriate). You can use this method for any
     * items for which you would like to do processing without those other facilities.
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
     * Called when the activity has detected the user's press of the back key. The default
     * implementation simply finishes the current activity, but you can override this to do whatever
     * you want.
     */
    @Override
    public void onBackPressed() {
        
        // This is used to fix a problem with the activity stack on API 10.
        moveTaskToBack(true);
    }

    /**
     * Custom tab listener used for navigation between the contacts fragments.
     */
    public static class ContactsTabListener<T extends Fragment> implements TabListener {
        private final Activity mHostingActivity;
        private final Class<T> mFragmentClass;
        private Fragment mFragment;
        private final String mFragmentTag;
        
        /** 
         * Constructor used each time a new tab is created.
         * 
         * @param activity  The host Activity, used to instantiate the fragment.
         * @param tag  The identifier tag for the fragment.
         * @param fragmentClass  The fragment's Class, used to instantiate the fragment.
         */
        public ContactsTabListener(Activity activity, String tag, Class<T> fragmentClass) {
            mHostingActivity = activity;
            mFragmentTag = tag;
            mFragmentClass = fragmentClass;
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
                mFragment = Fragment.instantiate(mHostingActivity, mFragmentClass.getName());
                ft.add(R.id.container, mFragment, mFragmentTag);
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
                
                // Detach the fragment, because another one is being attached.
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
            // Not being used, but it has to be here.
        }
    }
}
