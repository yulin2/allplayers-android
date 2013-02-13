package com.allplayers.android;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

public class MainScreen extends TabActivity {
    private Context context;
    
    private AccountManager accountManager;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Before anything, check if the user is logged in.
                
        RestApiV1 client = new RestApiV1();
        
        setContentView(R.layout.inapplayout);

        context = this.getBaseContext();

        Resources res = getResources(); // Resource object to get Drawables, this will be little icons for each one
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab


        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, GroupsActivity.class); //set as GroupsActivity.class, this will be changed to
        //whatever we end up calling that particular class

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("groups").setIndicator("Groups",
                res.getDrawable(R.drawable.ic_tab_groups)).setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, MessageActivity.class);
        spec = tabHost.newTabSpec("messages").setIndicator("Messages",
                res.getDrawable(R.drawable.ic_tab_messages)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, PhotosActivity.class);
        spec = tabHost.newTabSpec("photos").setIndicator("Photos",
                res.getDrawable(R.drawable.ic_tab_photos)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, EventsActivity.class);
        spec = tabHost.newTabSpec("events").setIndicator("Events",
                res.getDrawable(R.drawable.ic_tab_events)).setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.defaultmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.logOut: {
            LogOutTask helper = new LogOutTask();
            helper.execute();
            AccountManager manager = AccountManager.get(this.getBaseContext());
            Account[] accounts = manager.getAccountsByType("com.allplayers.android");
            if(accounts.length == 1) {
                manager.removeAccount(accounts[0], null, null);
            }
            startActivity(new Intent(MainScreen.this, Login.class));
            finish();
            return true;
        }
        case R.id.search: {
            startActivity(new Intent(MainScreen.this, FindGroupsActivity.class));
            return true;
        }
        case R.id.refresh: {
            TabHost tabHost = getTabHost();
            LocalActivityManager manager = getLocalActivityManager();
            String currentTag = tabHost.getCurrentTabTag();
            int currentIndex = tabHost.getCurrentTab();
            int swapIndex = (currentIndex % 3) + 1;
            Class <? extends Activity > currentClass = manager.getCurrentActivity().getClass();
            manager.destroyActivity(currentTag, true);
            manager.startActivity(currentTag, new Intent(this, currentClass));
            tabHost.setCurrentTab(swapIndex);
            tabHost.setCurrentTab(currentIndex);
            return true;
        }
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Logs the user out using a rest call.
     */
    public class LogOutTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... args) {
            RestApiV1.logOut();
            return null;
        }
    }
}