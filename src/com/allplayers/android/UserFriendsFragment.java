package com.allplayers.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;
import com.google.gson.Gson;

/**
 * Fragment displaying the user's friends.
 */
public class UserFriendsFragment extends ListFragment {

    private Activity mParentActivity;
    private ArrayAdapter<GroupMemberData> mAdapter;
    private ArrayList<GroupMemberData> mMembersList;
    private Button mLoadMoreButton;
    private ListView mListView;
    private ProgressBar mLoadingIndicator;
    private ViewGroup mFooter;

    private final int LIMIT = 15;
    private boolean mDoneLoading = false;
    private boolean mEndOfData = false;
    private boolean mLoadedOnce = false;
    private int mOffset = 0;

    /**
     * Called to do initial creation of a fragment. This is called after onAttach(Activity) and
     * before onCreateView(LayoutInflater, ViewGroup, Bundle).
     * 
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the parent activity.
        mParentActivity = getActivity();

        // Variable initialization.
        mMembersList = new ArrayList<GroupMemberData>();

        // Get the first 15 friends.
        new GetUserFriendsTask().execute();
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but
     * before any saved state has been restored in to the view.
     * 
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     * saved state as given here.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Get a handle on the ListView.
        mListView = getListView();

        // Create our adapter for the ListView.
        mAdapter = new ArrayAdapter<GroupMemberData>(mParentActivity,
                android.R.layout.simple_list_item_1, mMembersList);

        // Inflate and get a handle on our loading button and indicator.
        mFooter = (ViewGroup) LayoutInflater.from(mParentActivity)
                  .inflate(R.layout.load_more, null);
        mLoadMoreButton = (Button) mFooter.findViewById(R.id.load_more_button);
        mLoadingIndicator = (ProgressBar) mFooter.findViewById(R.id.loading_indicator);

        // When the load more button is clicked, show the loading indicator and load more
        // group members.
        mLoadMoreButton.setOnClickListener(new OnClickListener() {

            /** 
             * Called when the button is clicked.
             * 
             * @param v: The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                mLoadMoreButton.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                new GetUserFriendsTask().execute();
            }
        });

        // Determine if we should add the loading indicator and load more button to the bottom of
        // the listview.
        if (!mDoneLoading && mLoadedOnce) {
            mListView.addFooterView(mFooter);
            mLoadMoreButton.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        } else if (!mDoneLoading) {
            mListView.addFooterView(mFooter);
        }

        // Set our ListView adapter.
        setListAdapter(mAdapter);
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
    public void onListItemClick(ListView l, View v, int position, long id) {

        // Check if the loading indicator is being clicked (its id is '-1'). If so, we don't want to
        // do anything.
        if (!(id == -1)) {
            final int selectedPosition = position;
            PopupMenu menu = new PopupMenu(mParentActivity, v);
            menu.inflate(R.menu.friend_menu);
            menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                /** 
                 * Called when a menu item has been invoked.
                 * 
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
                            Intent intent = new Intent(mParentActivity,
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
     * Gets a user's friends from the api.
     */
    public class GetUserFriendsTask extends AsyncTask<Void, Void, String> {

        /** 
         * Performs a computation on a background thread.
         * 
         * @param params The parameters of the task.
         * @return Result of computation.
         */
        @Override
        protected String doInBackground(Void... args) {
            return RestApiV1.getUserFriends(mOffset, LIMIT);
        }

        /** 
         * Runs on the UI thread after doInBackground(Params...).
         * 
         * @param result The result of the operation computed by doInBackground(Params...).
         */
        @Override
        protected void onPostExecute(String jsonResult) {
            mLoadedOnce = true;
            jsonResult = jsonResult.replaceAll("firstname", "fname");
            jsonResult = jsonResult.replaceAll("lastname", "lname");
            GroupMembersMap groupMembers = new GroupMembersMap(jsonResult);
            if (groupMembers.size() == 0) {

                // If the newly pulled group members is empty, indicate the end of data.
                mEndOfData = true;

                // If the members list is also empty, there are no group members, so add
                // a blank indicator showing so.
                if (mMembersList.size() == 0) {
                    GroupMemberData blank = new GroupMemberData();
                    blank.setName("No groupmates to display");
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

            if (!mEndOfData) {
                
                // If we are not at the end of data, show our load more button and increase our offset.
                mLoadMoreButton.setVisibility(View.VISIBLE);
                mLoadingIndicator.setVisibility(View.GONE);
                mOffset += groupMembers.size();
            } else {

                // If we are at the end of data, remove the load more button.
                mListView.removeFooterView(mFooter);
                mDoneLoading = true;
            }
        }
    }
}