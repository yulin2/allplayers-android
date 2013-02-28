package com.allplayers.android;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

import com.allplayers.rest.RestApiV1;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.accounts.AccountManager;
import android.accounts.Account;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.TabHost;
import android.widget.Toast;

public class MainScreen extends SherlockFragmentActivity {

	/**
	 * Called when the activity is first created. Sets up the action bar tab
	 * navigation interface.
	 * 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the action bar and set it to use tab navigation.
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                        
        // Create a tab for the groups page.
        actionbar.addTab(actionbar
        		.newTab()
        		.setCustomView(R.layout.groups_tab_text)
        		.setTabListener(
        				new TabListener<GroupsFragment>(this, "Groups",
        				GroupsFragment.class, null)));
        
        // Create a tab for the messages page.
        actionbar.addTab(actionbar
        		.newTab()
        		.setCustomView(R.layout.messages_tab_text)
        		.setTabListener(
        				new TabListener<MessageFragment>(this, "Messages",
        				MessageFragment.class, null)));
        
        // Create a tab for the photos page.
        actionbar.addTab(actionbar
        		.newTab()
        		.setCustomView(R.layout.photos_tab_text)
        		.setTabListener(
        				new TabListener<PhotosFragment>(this, "Photos",
        				PhotosFragment.class, null)));
        
        // Create a tab for the Events page.
        actionbar.addTab(actionbar
        		.newTab()
        		.setCustomView(R.layout.events_tab_text)
        		.setTabListener(
        				new TabListener<EventFragment>(this, "Events",
        				EventFragment.class, null)));
    }
    
    /**
     * When the activity is created, populate the options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.defaultmenu, menu);
        return true;
    }

    /**
     * When an option in the options menu is selected, perform the associated
     * task.
     * 
     * @TODO Fix the refresh functionality to work with the action bar tab 
     * navigation.
     * 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.logOut: {
            LogOutTask helper = new LogOutTask();
            helper.execute();
            AccountManager manager = AccountManager.get(this.getBaseContext());
            Account[] accounts = manager.getAccountsByType(
            		"com.allplayers.android");
            if (accounts.length == 1) {
                manager.removeAccount(accounts[0], null, null);
            }
            startActivity(new Intent(MainScreen.this, Login.class));
            finish();
            return true;
        }
        case R.id.search: {
            startActivity(new Intent(MainScreen.this,
            		FindGroupsActivity.class));
            return true;
        }
//        case R.id.refresh: {
//            TabHost tabHost = getTabHost();
//            LocalActivityManager manager = getLocalActivityManager();
//            String currentTag = tabHost.getCurrentTabTag();
//            int currentIndex = tabHost.getCurrentTab();
//            int swapIndex = (currentIndex % 3) + 1;
//            Class <? extends Activity > currentClass = 
//            		manager.getCurrentActivity().getClass();
//            manager.destroyActivity(currentTag, true);
//            manager.startActivity(currentTag, new Intent(this, currentClass));
//            tabHost.setCurrentTab(swapIndex);
//            tabHost.setCurrentTab(currentIndex);
//            return true;
//        }
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Will log the current user out using a REST call.
     *
     */
    public class LogOutTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... args) {
            RestApiV1.logOut();
            return null;
        }
    }
}

/**
 * Handles switching between fragments when a tab is selected.
 *
 */
class TabListener<T extends Fragment> implements ActionBar.TabListener {
	private final FragmentActivity fragmentActivity;
	private final String fragmentTag;
	private final Class<T> fragmentClass;
	private final Bundle fragmentArgs;
	private Fragment fragment;
	
	/**
	 * Constructs the listener.
	 * 
	 * @param activity The activity the fragment was created in.
	 * @param tag A tag describing the fragment.
	 * @param clz The class the fragment is defined by.
	 * @param args Additional arguments.
	 */
	public TabListener(FragmentActivity activity, String tag, Class<T> clz,
			Bundle args) {
		fragmentActivity = activity;
		fragmentTag = tag;
		fragmentClass = clz;
		fragmentArgs = args;
		FragmentTransaction ft = fragmentActivity.getSupportFragmentManager()
		        .beginTransaction();
	}
	
	/**
	 * Switches the active fragment to the one corresponding with the selected
	 * tab.
	 * 
	 */
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
	ft = fragmentActivity.getSupportFragmentManager()
	        .beginTransaction();
	
		if (fragment == null) {
		    fragment = Fragment.instantiate(fragmentActivity, 
		    		fragmentClass.getName(), fragmentArgs);
		    ft.add(android.R.id.content, fragment, fragmentTag);
		    ft.commit();
		} else {
		    ft.attach(fragment);
		    ft.commit();
		}
	}
	
	/**
	 * When a tab is switched from, closes the previous fragment.
	 * 
	 */
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
	
		if (fragment != null) {
		    ft.detach(fragment);
		    ft.commitAllowingStateLoss();
		}
	}
	
	/**
	 * When a tab that has been previously selected is reselected, does some 
	 * "special behavior".
	 * 
	 */
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		//Do Nothing.
	}
}



