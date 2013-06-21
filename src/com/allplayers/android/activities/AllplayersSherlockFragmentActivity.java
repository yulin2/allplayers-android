package com.allplayers.android.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.allplayers.android.ContactsActivity;
import com.allplayers.android.EventsActivity;
import com.allplayers.android.FindGroupsActivity;
import com.allplayers.android.GroupsActivity;
import com.allplayers.android.Login;
import com.allplayers.android.MessageActivity;
import com.allplayers.android.PhotosActivity;
import com.allplayers.android.R;
import com.allplayers.android.activities.AllplayersSherlockListActivity.LogOutTask;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;

/**
 * Custom version of SherlockFragmentActivity that sets up the action bar and side navigation menu.
 */
public class AllplayersSherlockFragmentActivity extends SherlockFragmentActivity implements ISideNavigationCallback {
    protected SideNavigationView mSideNavigationView;
    protected ActionBar mActionBar;
    protected int mCurrentTheme;
    
    /**
     * Called when the activity is starting.
     * 
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in
     * onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        // Check which theme is selected by the user and apply it.
        SharedPreferences sharedPreferences = getSharedPreferences("Theme Choice", 0);
        setTheme(sharedPreferences.getInt("Theme", android.R.style.Theme_Holo));
        mCurrentTheme = sharedPreferences.getInt("Theme", android.R.style.Theme_Holo);
        
        super.onCreate(savedInstanceState);
        
        // Set up the action bar.
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        mActionBar = getSupportActionBar();
        mActionBar.setIcon(R.drawable.menu_icon);
        mActionBar.setHomeButtonEnabled(true);
    }

    /**
     * Called when you are no longer visible to the user. You will next receive either onRestart(),
     * onDestroy(), or nothing, depending on later user activity.
     */
    @Override
    public void onStop() {
        super.onStop();

        // Save the user's UUID to local storage
        SharedPreferences sharedPreferences = getSharedPreferences("Critical_Data", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String UUID = RestApiV1.getCurrentUserUUID();
        editor.putString("UUID", UUID);
        editor.commit();
    }

    /**
     * Called after onStop() when the current activity is being re-displayed to the user (the user
     * has navigated back to it). It will be followed by onStart() and then onResume().
     */
    @Override
    public void onRestart() {
        super.onRestart();
        
        // Check what theme is currently seleted by the user.
        SharedPreferences sharedPreferences = getSharedPreferences("Theme Choice", 0);
        
        // If we are alerady using the user selected theme then don't worry about doign anything
        // else. If we arent using the user selected theme though, we need to switch the theme and
        // then restart the activity to update it.
        if (!(sharedPreferences.getInt("Theme", android.R.style.Theme_Holo) == mCurrentTheme)) {
            setTheme(sharedPreferences.getInt("Theme", android.R.style.Theme_Holo));
            mCurrentTheme = sharedPreferences.getInt("Theme", android.R.style.Theme_Holo);
            refresh();
        }

        // Make sure that the user's UUID is up to date.
        sharedPreferences = getSharedPreferences("Critical_Data", 0);
        RestApiV1.setCurrentUserUUID(sharedPreferences.getString("UUID", "ERROR: The string UUID could not be fetched from sharedPreferences"));
    }

    /**
     * This hook is called whenever an item in your options menu is selected. 
     *
     * @param item The menu item that was selected.
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // The only thing we are using the action bar menu for is the side navigation. This will
        // detect that selection and show us the side navigation manu.
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
     * Listener for the Side Navigation Menu.
     *
     * @param itemId: The ID of the list item that was selected.
     */
    @Override
    public void onSideNavigationItemClick(int itemId) {

        switch (itemId) {

            case R.id.groups: {
                invokeActivity(GroupsActivity.class);
                break;
            }
    
            case R.id.contacts: {
                invokeActivity(ContactsActivity.class);
                break;
            }
    
            case R.id.messages: {
                invokeActivity(MessageActivity.class);
                break;
            }
    
            case R.id.photos: {
                invokeActivity(PhotosActivity.class);
                break;
            }
    
            case R.id.events: {
                invokeActivity(EventsActivity.class);
                break;
            }
    
            case R.id.search: {
                search();
                break;
            }
    
            case R.id.log_out: {
                logOut();
                break;
            }
    
            case R.id.refresh: {
                refresh();
                break;
            }
            
            case R.id.change_theme: {
                
                // Switch the theme preference and then refresh the activity. We are not directly
                // setting the theme here. When we call refresh(), the activity's onCreate() method
                // is being called, which will set the theme.
                
                SharedPreferences sharedPreferences = getSharedPreferences("Theme Choice", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (sharedPreferences.getInt("Theme", 0) == android.R.style.Theme_Holo_Light_DarkActionBar) {
                    
                    // If we currently are using the light theme, switch to dark.
                    editor.putInt("Theme", android.R.style.Theme_Holo);
                } else if (sharedPreferences.getInt("Theme", 0) == android.R.style.Theme_Holo) {
                    
                    // If we currently are using the dark theme, switch to light.
                    editor.putInt("Theme", android.R.style.Theme_Holo_Light_DarkActionBar);
                } else {
                    
                    // We should never get here but just in case we do, set the theme to the default
                    // (dark).
                    editor.putInt("Theme", android.R.style.Theme_Holo);
                }
                
                // Commit the changes and refresh the activity to show the new theme.
                editor.commit();
                refresh();
                break;
            }
        }
        finish();
    }

    /**
     * Helper method for onSideNavigationItemClick. Starts the passed in activity.
     *
     * @param activity: The activity to be started.
     */
    @SuppressWarnings("rawtypes")
    protected void invokeActivity(Class activity) {

        Intent intent = new Intent(this, activity);
        intent.addFlags(32768 | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        //overridePendingTransition(0, 0); // Disables new activity animation.
    }

    /**
     * Opens the search screen.
     */
    protected void search() {

        startActivity(new Intent(this, FindGroupsActivity.class));
    }

    /**
     * Logs the user out of the application.
     */
    protected void logOut() {

        new LogOutTask().execute();

        AccountManager manager = AccountManager.get(this.getBaseContext());
        Account[] accounts = manager.getAccountsByType("com.allplayers.android");

        for (int i = 0; i < accounts.length; i++) {
            manager.removeAccount(accounts[i], null, null);
        }

        invokeActivity(Login.class);
        finish();
    }

    /**
     * Refreshes the current activity to update information.
     */
    protected void refresh() {

        finish();
        startActivity(getIntent());
    }

    /**
     * Helper class to handle the network call needed to log out asynchronously.
     */
    protected class LogOutTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {

            RestApiV1.logOut();
            return null;
        }
    }
}
