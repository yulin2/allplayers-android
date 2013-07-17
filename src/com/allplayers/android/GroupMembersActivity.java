package com.allplayers.android;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.GroupData;
import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.google.gson.Gson;

/**
 * Displays a list of the group's members.
 */
public class GroupMembersActivity extends AllplayersSherlockListActivity {
    private ProgressBar mLoadingIndicator;
    private ArrayList<GroupMemberData> mMembersList = new ArrayList<GroupMemberData>();
    private Button mLoadMoreButton;
    private GroupData mGroup;
    private int mOffset = 0;
    private boolean mEndOfData = false;
    private ArrayAdapter<GroupMemberData> mAdapter;
    private ListView mListView;
    private ViewGroup mFooter;
    private final int LIMIT = 15;
    
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
        setContentView(R.layout.members_list);

        // Get a handle on the ListView.
        mListView = getListView();
        
        // Create our adapter for the ListView.
        mAdapter = new ArrayAdapter<GroupMemberData>(this, android.R.layout.simple_list_item_1, mMembersList);

        // Inflate and get a handle on our loading button and indicator.
        mFooter = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.load_more, null);
        mLoadMoreButton = (Button) mFooter.findViewById(R.id.load_more_button);
        mLoadingIndicator = (ProgressBar) mFooter.findViewById(R.id.loading_indicator);

        // When the load more button is clicked, show the loading indicator and load more
        // group members.
        mLoadMoreButton.setOnClickListener(new OnClickListener() {
            
            /**
             * Called when a view has been clicked.
             * 
             * @param v The view that has been clicked.
             */
            @Override
            public void onClick(View v) {
                mLoadMoreButton.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                new GetGroupMembersByGroupIdTask().execute(mGroup);
            }
        });

        // Add our loading button and indicator to the ListView.
        mListView.addFooterView(mFooter);
        // Set our ListView adapter.
        setListAdapter(mAdapter);

        // Pull the current group out of the current intent.
        mGroup = (new Router(this)).getIntentGroup();

        // Set up the ActionBar.
        mActionBar.setTitle(mGroup.getTitle());
        mActionBar.setSubtitle("Members");

        // Set up the Side Navigation List.
        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Populate the list with the first 8 members.
        new GetGroupMembersByGroupIdTask().execute(mGroup);
    }

    /** This method will be called when an item in the list is selected.
     * @param l The ListView where the click happened.
     * @param v The view that was clicked within the ListView.
     * @param position The position of the view in the list.
     * @param id The row id of the item that was clicked.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        // Check if the loading indicator is being clicked (its id is '-1'). If so, we don't want to
        // do anything.
        if (!(id == -1)) {
            final int selectedPosition = position;
            PopupMenu menu = new PopupMenu(GroupMembersActivity.this, v);
            menu.inflate(R.menu.friend_menu);
            menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                /** Called when a menu item has been invoked.
                 * @param item The menu item that was invoked.
                 * @return Return true to consume this click and prevent others from executing.
                 */
                @Override
                public boolean onMenuItemClick(android.view.MenuItem item) {
                    switch (item.getItemId()) {

                        // Go to SelectMessageContacts.class with the selected user autopopulated.
                        case R.id.send_message: {
                            Gson gson = new Gson();
                            ArrayList<GroupMemberData> selectedUser =
                                new ArrayList<GroupMemberData>();
                            selectedUser.add(mMembersList.get(selectedPosition));
                            String broadcastRecipients = gson.toJson(selectedUser);
                            Intent intent = new Intent(GroupMembersActivity.this,
                                                       SelectMessageContacts.class);
                            intent.putExtra("broadcastRecipients", broadcastRecipients);
                            startActivity(intent);
                        }
                    }
                    return true;
                }
            });
            menu.show();
        }
    }

    /**
     * Gets a group's members..
     */
    public class GetGroupMembersByGroupIdTask extends AsyncTask<GroupData, Void, String> {

        /**
         * Performs a computation on a background thread.
         * 
         * @param groups The group whose users need to be fetched.
         */
        @Override
        protected String doInBackground(GroupData... groups) {
            return RestApiV1.getGroupMembersByGroupId(groups[0].getUUID(), mOffset, LIMIT, getApplicationContext());
        }

        /**
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         * 
         * @param jsonResult The result from fetching the group's users.
         */
        @Override
        protected void onPostExecute(String jsonResult) {
            GroupMembersMap groupMembers = new GroupMembersMap(jsonResult);

            if (groupMembers.size() == 0) {
                
                // If the newly pulled group members is empty, indicate the end of data.
                mEndOfData = true;
                
                // If the members list is also empty, there are no group members, so add
                // a blank indicator showing so.
                if (mMembersList.size() == 0) {
                    GroupMemberData blank = new GroupMemberData();
                    blank.setName("No members to display");
                    mMembersList.add(blank);
                    mAdapter.notifyDataSetChanged();
                    mListView.setEnabled(false);
                }
            } else {
                
                // If we pulled less than 10 new members, indicate we are at the end of data.
                if (groupMembers.size() < LIMIT) {
                    mEndOfData = true;
                }

                // Add all the new members to our list and update our ListView.
                mMembersList.addAll(groupMembers.getGroupMemberData());
                mAdapter.notifyDataSetChanged();
            }
            
            // If we are not at the end of data, show our load more button and increase our offset.
            // If we are at the end of data, remove the load more button.
            if (!mEndOfData) {
                mLoadMoreButton.setVisibility(View.VISIBLE);
                mLoadingIndicator.setVisibility(View.GONE);
                mOffset += groupMembers.size();
            } else {
                mListView.removeFooterView(mFooter);
            }
        }
    }
}