package com.allplayers.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.allplayers.objects.GroupData;
import com.allplayers.rest.RestApiV1;

/**
 * Fragment displaying the user's groups.
 */
public class GroupsFragment extends ListFragment {
    
    private Activity mParentActivity;
    private ArrayAdapter<GroupData> mAdapter;
    private ArrayList<GroupData> mGroupList = new ArrayList<GroupData>();
    private GetUserGroupsTask mGetUserGroupsTask;
    private ListView mListView;
    private ProgressBar mProgressBar;
    
    private final int LIMIT = 15;
    private boolean mHasGroups = false, mLoadMore = true;
    private int mCurrentAmountShown = 0; 
    private int mPageNumber = 0;
    private String mSortType = "radioactive";

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

        // Get a handle on our containing activity.
        mParentActivity = this.getActivity();

        // Create our adapter.
        mAdapter = new ArrayAdapter<GroupData>(mParentActivity, android.R.layout.simple_list_item_1, mGroupList);

        // Load our sort type from the arguments.
        if (getArguments() != null && getArguments().containsKey("sort")) {
            mSortType = getArguments().getString("sort");
        }

        // Load the first set of groups.
        mGetUserGroupsTask = new GetUserGroupsTask();
        mGetUserGroupsTask.execute();
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
        
        // Set up our loading indicator at the foot of the ListView.
        mProgressBar = new ProgressBar(mParentActivity);
        mListView = getListView();
        mListView.addFooterView(mProgressBar, null, false);
        
        // Set our adapter.
        setListAdapter(mAdapter);

        // Set a scroll listener to load more if we are at the end of our list
        // and less than two items are offscreen.
        mListView.setOnScrollListener(new OnScrollListener() {
            private int visibleThreshold = 2;
            private int previousTotal = 1;
            private boolean loading = true;
            
            /**
             * Callback method to be invoked when the list or grid has been scrolled. This will be
             * called after the scroll has completed
             * 
             * @param view The view whose scroll state is being reported.
             * @param firstVisibleItem The index of the first visible cell (ignore if
             * visibleItemCount == 0).
             * @param visibleItemCount The number of visible cells.
             * @param totalItemCount The number of items in the list adaptor.
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (mLoadMore && !loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    mGetUserGroupsTask = new GetUserGroupsTask();
                    mGetUserGroupsTask.execute();
                    loading = true;
                }
            }
            
            /**
             * Callback method to be invoked while the list view or grid view is being scrolled. If
             * the view is being scrolled, this method will be called before the next frame of the
             * scroll is rendered. In particular, it will be called before any calls to getView(int,
             * View, ViewGroup).
             * 
             * @param view The view whose scroll state is being reported.
             * @param scrollState The current scroll state. One of SCROLL_STATE_IDLE,
             * SCROLL_STATE_TOUCH_SCROLL or SCROLL_STATE_IDLE.
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // We don't need this.
            }
        });
    }
    
    /**
     * Called when the Fragment is no longer started. This is generally tied to Activity.onStop of
     * the containing Activity's lifecycle.
     */
    @Override
    public void onStop() {
        super.onStop();
        
        // Stop any asynchronous tasks that we have running.
        if (mGetUserGroupsTask != null) {
            mGetUserGroupsTask.cancel(true);
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (mHasGroups && position < mGroupList.size()) {
            
            //Display the group page for the selected group
            Intent intent = (new Router(mParentActivity)).getGroupPageActivityIntent(mGroupList.get(position));
            startActivity(intent);
        }
    }

    /**
     * Populates the list of groups to display. 
     */
    protected void updateGroupData() {
        if (!mGroupList.isEmpty()) {
            mAdapter.notifyDataSetChanged();
            
            // If we did not load 15 groups, we are at the end of the list, so signal
            // not to try to load more groups.
            if (mGroupList.size() - mCurrentAmountShown < LIMIT) {
                mLoadMore = false;
                if (mListView != null) {
                    mListView.removeFooterView(mProgressBar);
                }
            }
            mHasGroups = true;
        } else {
            
            // If we do not have any groups, create a blank list item and remove our loading footer.
            ArrayAdapter<String> blankAdapter = new ArrayAdapter<String>(mParentActivity, android.R.layout.simple_list_item_1);
            blankAdapter.add("No groups to display");
            if (mListView != null) {
                mListView.setAdapter(blankAdapter);
                mListView.setEnabled(false);
                mListView.removeFooterView(mProgressBar);
            }
        }
    }

    /**
     * Fetches the groups a user belongs to.
     */
    public class GetUserGroupsTask extends AsyncTask<Void, Void, String> {
        
        /**
         * Performs a calculation on a background thread. Gets a user's groups from the API.
         * 
         * @return The result of the call to get the user's groups.
         */
        @Override
        protected String doInBackground(Void... args) {
            return RestApiV1.getUserGroups(mPageNumber++ * LIMIT, LIMIT, mSortType, getActivity().getApplicationContext());
        }

        /**
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         * 
         * @param jsonResult The result of the call to get the user's groups.
         */
        @Override
        protected void onPostExecute(String jsonResult) {
            
            if (!jsonResult.equals("error")) {
                
                // Create the new group data objects and add them to our list.
                GroupsMap groups = new GroupsMap(jsonResult);
                mCurrentAmountShown = mGroupList.size();
                mGroupList.addAll(groups.getGroupData());
            }
            
            // Update the ListView.
            updateGroupData();
        }
    }
}
