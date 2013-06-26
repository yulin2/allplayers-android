package com.allplayers.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;
import com.google.gson.Gson;

/**
 * Fragment displaying the user's family members (children and guardians).
 */
public class UserFamilyFragment extends Fragment {

    private ArrayAdapter<GroupMemberData> mChildrenAdapter;
    private ArrayAdapter<GroupMemberData> mGuardiansAdapter;

    private ArrayList<GroupMemberData> mChildrenList;
    private ArrayList<GroupMemberData> mGuardiansList;

    private ListView mChildrenListView;
    private ListView mGuardiansListView;

    private ProgressBar mChildrenLoadingIndicator;
    private ProgressBar mGuardiansLoadingIndicator;

    private ViewGroup mChildrenFooter;
    private ViewGroup mGuardiansFooter;

    private boolean mChildrenNoData = false;
    private boolean mGuardiansNoData = false;
    private boolean mChildrenDoneLoading = false;
    private boolean mGuardiansDoneLoading = false;
    private boolean mHasBeenLoaded = false;

    private Activity mParentActivity;

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
        mChildrenList = new ArrayList<GroupMemberData>();
        mGuardiansList = new ArrayList<GroupMemberData>();
    }

    /** 
     * Called to have the fragment instantiate its user interface view.
     * 
     * @param inflater The LayoutInflater object that can be used to inflate any views in the
     * fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be
     * attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     * saved state as given here.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_family, null);
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

        // Get a handle on the ListViews.
        mChildrenListView = (ListView) getView().findViewById(R.id.children_listview);
        mGuardiansListView = (ListView) getView().findViewById(R.id.guardians_listview);

        // Create our adapter for the ListViews.
        mChildrenAdapter = new ArrayAdapter<GroupMemberData>(mParentActivity,
                android.R.layout.simple_list_item_1, mChildrenList);
        mGuardiansAdapter = new ArrayAdapter<GroupMemberData>(mParentActivity,
                android.R.layout.simple_list_item_1, mGuardiansList);

        // Set up the footer with the loading indicator.
        mChildrenFooter = (ViewGroup) LayoutInflater.from(mParentActivity)
                          .inflate(R.layout.loading_footer, null);
        mGuardiansFooter = (ViewGroup) LayoutInflater.from(mParentActivity)
                           .inflate(R.layout.loading_footer, null);
        mChildrenLoadingIndicator = (ProgressBar) mChildrenFooter
                                    .findViewById(R.id.loading_indicator);
        mGuardiansLoadingIndicator = (ProgressBar) mGuardiansFooter
                                     .findViewById(R.id.loading_indicator);

        // Add loading footers to the listviews.
        mChildrenListView.addFooterView(mChildrenFooter);
        mGuardiansListView.addFooterView(mGuardiansFooter);

        // Set the listviews' adapters.
        mChildrenListView.setAdapter(mChildrenAdapter);
        mGuardiansListView.setAdapter(mGuardiansAdapter);

        // Set up listeners for list item clicks.
        mChildrenListView.setOnItemClickListener(new OnItemClickListener() {

            /** 
             * Callback method to be invoked when an item in this AdapterView has been clicked.
             * 
             * @param parent The AdapterView where the click happened.
             * @param v The view within the AdapterView that was clicked.
             * @param position The position of the view in the adapter.
             * @param id The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // Check if the loading indicator is being clicked (its id is '-1'). If so, we don't
                // want to do anything.
                if (!(id == -1)) {
                    final int selectedPosition = position;
                    PopupMenu menu = new PopupMenu(mParentActivity, v);
                    menu.inflate(R.menu.friend_menu);
                    menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                        /** 
                         * Called when a menu item has been invoked.
                         * 
                         * @param item The menu item that was invoked.
                         * @return Return true to consume this click and prevent others from
                         * executing.
                         */
                        @Override
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            switch (item.getItemId()) {

                                // Go to SelectMessageContacts.class with the selected user
                                // autopopulated.
                                case R.id.send_message: {
                                    Gson gson = new Gson();
                                    ArrayList<GroupMemberData> selectedUser =
                                        new ArrayList<GroupMemberData>();
                                    selectedUser.add(mChildrenList.get(selectedPosition));
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
        });

        mGuardiansListView.setOnItemClickListener(new OnItemClickListener() {

            /** 
             * Callback method to be invoked when an item in this AdapterView has been clicked.
             * 
             * @param parent The AdapterView where the click happened.
             * @param v The view within the AdapterView that was clicked.
             * @param position The position of the view in the adapter.
             * @param id The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // Check if the loading indicator is being clicked (its id is '-1'). If so, we don't
                // want to do anything.
                if (!(id == -1)) {
                    final int selectedPosition = position;
                    PopupMenu menu = new PopupMenu(mParentActivity, v);
                    menu.inflate(R.menu.friend_menu);
                    menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                        /** 
                         * Called when a menu item has been invoked.
                         * 
                         * @param item The menu item that was invoked.
                         * @return Return true to consume this click and prevent others from
                         * executing.
                         */
                        @Override
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            switch (item.getItemId()) {

                                // Go to SelectMessageContacts.class with the selected user
                                // autopopulated.
                                case R.id.send_message: {
                                    Gson gson = new Gson();
                                    ArrayList<GroupMemberData> selectedUser =
                                        new ArrayList<GroupMemberData>();
                                    selectedUser.add(mGuardiansList.get(selectedPosition));
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
        });

        // Populate the listviews.
        if (!mHasBeenLoaded) {
            new GetUserChildrenTask().execute();
            new GetUserGuardiansTask().execute();
            mHasBeenLoaded = true;
        } else {
            if (mChildrenDoneLoading) {
                mChildrenLoadingIndicator.setVisibility(View.GONE);
            }
            if (mGuardiansDoneLoading) {
                mGuardiansLoadingIndicator.setVisibility(View.GONE);
            }
            if (mChildrenNoData) {
                mChildrenListView.setEnabled(false);
            }
            if (mGuardiansNoData) {
                mGuardiansListView.setEnabled(false);
            }
        }
    }

    /** 
     * Get's the currently logged in user's children from the API.
     */
    public class GetUserChildrenTask extends AsyncTask<Void, Void, String> {

        /** 
         * Performs a computation on a background thread.
         * 
         * @param params The parameters of the task.
         * @return Result of computation.
         */
        @Override
        protected String doInBackground(Void... params) {
            return RestApiV1.getUserChildren(0, 0);
        }

        /** 
         * Runs on the UI thread after doInBackground(Params...).
         * 
         * @param result The result of the operation computed by doInBackground(Params...).
         */
        @Override
        protected void onPostExecute(String result) {
            mChildrenDoneLoading = true;

            // Fixes API compatability issues.
            result = result.replaceAll("firstname", "fname");
            result = result.replaceAll("lastname", "lname");

            // Setup a map with the data returned from the API.
            GroupMembersMap childrenMap = new GroupMembersMap(result);

            // Check if there was any data returned, if not, we know that the user does not have any
            // children. If this is the case, display a message saying that there are no children
            // to display. If this is not the case, add all of the fetched children to the list.
            if (childrenMap.size() == 0) {
                GroupMemberData blank = new GroupMemberData();
                blank.setName("No children to display");
                mChildrenList.add(blank);
                mChildrenAdapter.notifyDataSetChanged();
                mChildrenListView.setEnabled(false);
                mChildrenLoadingIndicator.setVisibility(View.GONE);
                mChildrenNoData = true;
            } else {
                mChildrenList.addAll(childrenMap.getGroupMemberData());
                mChildrenAdapter.notifyDataSetChanged();
                mChildrenLoadingIndicator.setVisibility(View.GONE);
            }
        }
    }

    public class GetUserGuardiansTask extends AsyncTask<Void, Void, String> {

        /** 
         * Performs a computation on a background thread.
         * 
         * @param params The parameters of the task.
         * @return Result of computation.
         */
        @Override
        protected String doInBackground(Void... params) {

            // The number of guardians that a user has is always quite low so just pull all of them.
            return RestApiV1.getUserGuardians(0, 0);
        }

        /** 
         * Runs on the UI thread after doInBackground(Params...).
         * 
         * @param result The result of the operation computed by doInBackground(Params...).
         */
        @Override
        protected void onPostExecute(String result) {
            mGuardiansDoneLoading = true;

            // Fixes API compatability issues.
            result = result.replaceAll("firstname", "fname");
            result = result.replaceAll("lastname", "lname");

            // Setup a map with the data returned from the API.
            GroupMembersMap guardiansMap = new GroupMembersMap(result);

            // Check if there was any data returned, if not, we know that the user does not have any
            // guardians. If this is the case, display a message saying that there are no guardians
            // to display. If this is not the case, add all of the fetched guardians to the list.
            if (guardiansMap.size() == 0) {
                GroupMemberData blank = new GroupMemberData();
                blank.setName("No guardians to display");
                mGuardiansList.add(blank);
                mGuardiansAdapter.notifyDataSetChanged();
                mGuardiansListView.setEnabled(false);
                mGuardiansLoadingIndicator.setVisibility(View.GONE);
                mGuardiansNoData = true;
            } else {
                mGuardiansList.addAll(guardiansMap.getGroupMemberData());
                mGuardiansAdapter.notifyDataSetChanged();
                mGuardiansLoadingIndicator.setVisibility(View.GONE);
            }
        }
    }
}
