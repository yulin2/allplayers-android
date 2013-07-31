
package com.allplayers.android;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.GroupData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

/**
 * Displays the results from FindGroupsActivity.
 */
public class SearchGroupsListActivity extends AllplayersSherlockListActivity {

    private ArrayList<GroupData> mGroupList;
    private ProgressBar mProgressBar;
    private SearchGroupsTask mSearchGroupsTask;
    
    private boolean hasGroups = false;

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
        setContentView(R.layout.search_groups_list);

        // If you user is not logged in, we want to make sure that they cannot access the side
        // navigation menu.
        if (RestApiV1.getCurrentUserUUID().equals("")) {
            mActionBar.setHomeButtonEnabled(false);
        }

        // Set up the ActionBar.
        mActionBar.setTitle("Search");

        // Set up the side navigation menu.
        mSideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Set up the ProgressBar.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        // Grab the search parameters from the intent.
        Router router = new Router(this);
        String query = router.getIntentSearchQuery();
        int zipcode = router.getIntentSearchZipcode();
        int distance = router.getIntentSearchDistance();

        // Search for the groups using the passed in parameters.
        mSearchGroupsTask = new SearchGroupsTask();
        mSearchGroupsTask.execute(query, zipcode, distance);
    }
    
    /**
     * Called when you are no longer visible to the user. You will next receive either onRestart(),
     * onDestroy(), or nothing, depending on later user activity.
     */
    @Override 
    public void onStop() {
        super.onStop();
        
        if (mSearchGroupsTask != null) {
            mSearchGroupsTask.cancel(true);
        }
    }

    /**
     * This method will be called when an item in the list is selected.
     * 
     * @param l The ListView where the click happened.
     * @param v The view that was clicked within the ListView.
     * @param position The position of the view in the list.
     * @param id The row id of the item that was clicked.
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // This is how we disable the user from clicking the empty list item. There are probably
        // better ways to do this, but this is effective for now.
        if (hasGroups) {
            
            // Display the group page for the selected group
            Intent intent = (new Router(this)).getGroupPageActivityIntent(mGroupList.get(position));
            startActivity(intent);
        }
    }

    /**
     * Called when a key was released and not handled by any of the views inside of the activity. 
     * 
     * @param keyCode The value in event.getKeyCode().
     * @param event Description of the key event.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_MENU) {
            startActivity(new Intent(SearchGroupsListActivity.this, FindGroupsActivity.class));
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Searches for a group.
     */
    public class SearchGroupsTask extends AsyncTask<Object, Void, String> {
        
        /**
         * Performs a computation on the background thread. Searches for groups using the passed
         * parameters.
         * 
         * @param args
         *      [0] The search query.
         *      [1] The search zipcode.
         *      [2] The search distance.
         * @return The result of the API call.
         */
        @Override
        protected String doInBackground(Object... args) {
            return RestApiV1.searchGroups((String) args[0], (Integer) args[1], (Integer) args[2], 0, 0, getApplicationContext());
        }

        /**
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         * 
         * @param jsonResult The result of the API call.
         */
        @Override
        protected void onPostExecute(String jsonResult) {
            GroupsMap groups = new GroupsMap(jsonResult);
            mGroupList = groups.getGroupData();

            if (mGroupList.size() == 1) {
                mActionBar.setSubtitle("1 Result");
            } else {
                mActionBar.setSubtitle(mGroupList.size() + " Results");
            }

            String[] values;

            if (!mGroupList.isEmpty()) {
                values = new String[mGroupList.size()];

                for (int i = 0; i < mGroupList.size(); i++) {
                    values[i] = mGroupList.get(i).getTitle();
                }

                hasGroups = true;
            } else {
                values = new String[] {
                    "no groups to display"
                };
                hasGroups = false;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchGroupsListActivity.this,
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
