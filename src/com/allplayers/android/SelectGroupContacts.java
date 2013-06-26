package com.allplayers.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.GroupData;
import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.google.gson.Gson;

/**
 * Interface to allow a user to select group recipients for a message.
 */
public class SelectGroupContacts extends AllplayersSherlockListActivity {

    private ArrayAdapter<GroupData> mAdapter;
    private ArrayList<GroupData> mGroupsList;
    private ArrayList<GroupData> mSelectedGroups;
    private ArrayList<GroupMemberData> mSelectedMembers;
    private Button mLoadMoreButton;
    private ListView mListView;
    private ProgressBar mLoadingIndicator;
    private ViewGroup mFooter;

    private final int LIMIT = 15;
    private boolean mEndOfData = false;
    private int mOffset = 0;

    /**
     * Called when the activity is starting. Handles variable initialization, and sets up the
     * interface.
     * @param savedInstanceState: If the activity is being re-initialized after previously being
     * shut down then this Bundle contains the data it most recently supplied in
     * onSaveInstanceState(Bundle). Otherwise it is null.
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectgroupcontacts);

        // Set up the ActionBar.
        mActionBar.setTitle("Compose Message");
        mActionBar.setSubtitle("Select Group Recipients");

        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Variable initialization.
        mGroupsList = new ArrayList<GroupData>();
        mSelectedGroups = new ArrayList<GroupData>();
        mSelectedMembers = new ArrayList<GroupMemberData>();

        // Get a handle on the ListView.
        mListView = getListView();

        // Create our adapter for the ListView.
        mAdapter = new ArrayAdapter<GroupData>(this, android.R.layout.simple_list_item_multiple_choice, mGroupsList);

        // Inflate and get a handle on our loading button and indicator.
        mFooter = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.load_more, null);
        mLoadMoreButton = (Button) mFooter.findViewById(R.id.load_more_button);
        mLoadingIndicator = (ProgressBar) mFooter.findViewById(R.id.loading_indicator);

        // When the load more button is clicked, show the loading indicator and load more
        // groups.
        mLoadMoreButton.setOnClickListener(new OnClickListener() {

            /**
             * Called when a view has been clicked.
             * 
             * @param v: The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                mLoadMoreButton.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                new GetUserGroupsTask().execute();
            }
        });

        // Add our loading button and indicator to the ListView.
        mListView.addFooterView(mFooter);

        // Set our ListView adapter.
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        setListAdapter(mAdapter);

        final Button doneButton = (Button)findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             * 
             * @param v: The view that was clicked.
             */
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                mLoadMoreButton.setVisibility(View.INVISIBLE);
                if (mSelectedGroups.size() == 0) {
                    finish();
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    // The serialization and finishing of this activity is handled in the async
                    // thread.
                    new GetGroupMembersByGroupIdTask().execute(mSelectedGroups);
                }
            }
        });

        // Get the first 15 groups.
        new GetUserGroupsTask().execute();
    }

    /**
     * This method will be called when an item in the list is selected.
     * 
     * @param l: The ListView where the click happened.
     * @param v: The view that was clicked within the ListView.
     * @param position: The position of the view in the list.
     * @param id: The row id of the item that was clicked.
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (!mSelectedGroups.contains(mGroupsList.get(position))) {
            mSelectedGroups.add(mGroupsList.get(position));
        } else {
            mSelectedGroups.remove(mGroupsList.get(position));
        }
    }

    /**
     * Gets a user's groups.
     */
    public class GetUserGroupsTask extends AsyncTask<Void, Void, String> {

        /**
         * Performs a computation on a background thread.
         * 
         * @return Result of the API call. 
         */
        @Override
        protected String doInBackground(Void... args) {
            return RestApiV1.getUserGroups(mOffset, LIMIT, "alphabetical_ascending");
        }

        /**
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         * 
         * @param jsonResult Result of the API call.
         */
        @Override
        protected void onPostExecute(String jsonResult) {
            GroupsMap groups = new GroupsMap(jsonResult);

            if (groups.size() == 0) {
                
                // If the newly pulled group members is empty, indicate the end of data.
                mEndOfData = true;
                
                // If the members list is also empty, there are no group members, so add
                // a blank indicator showing so.
                if (mGroupsList.size() == 0) {
                    GroupData blank = new GroupData();
                    blank.setTitle("No members to display");
                    mGroupsList.add(blank);
                    mAdapter.notifyDataSetChanged();
                    mListView.setEnabled(false);
                }
            } else {
                
                // If we pulled less than 10 new members, indicate we are at the end of data.
                if (groups.size() < LIMIT) {
                    mEndOfData = true;
                }

                // Add all the new members to our list and update our ListView.
                mGroupsList.addAll(groups.getGroupData());
                mAdapter.notifyDataSetChanged();
            }
            
            // If we are not at the end of data, show our load more button and increase our offset.
            if (!mEndOfData) {
                mLoadMoreButton.setVisibility(View.VISIBLE);
                mLoadingIndicator.setVisibility(View.GONE);
                mOffset += groups.size();
                
            // If we are at the end of data, remove the load more button.
            } else {
                mListView.removeFooterView(mFooter);
            }
        }
    }

    /**
     * Gets a group's members.
     */
    public class GetGroupMembersByGroupIdTask extends AsyncTask<ArrayList<GroupData>, Void, String> {

        /**
         * Performs a computation on a background thread.
         * 
         * @return Result of the API call. 
         */
        @Override
        protected String doInBackground(ArrayList<GroupData>... groups) {
            String jsonResult = new String();
            for (int i = 0; i < groups[0].size(); i++) {
                jsonResult += (RestApiV1.getGroupMembersByGroupId(groups[0].get(i).getUUID(), 0, 0));
            }
            return jsonResult;
        }

        /**
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         * 
         * @param jsonResult Result of the API call.
         */
        @Override
        protected void onPostExecute(String jsonResult) {
            GroupMembersMap groupMembers = new GroupMembersMap(jsonResult);
            mSelectedMembers = groupMembers.getGroupMemberData();
            Gson gson = new Gson();
            String userData = gson.toJson(mSelectedMembers);
            Intent intent = new Intent();
            intent.putExtra("userData", userData);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}