package com.allplayers.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.EventData;
import com.allplayers.objects.GroupData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class GroupEventsActivity extends AllplayersSherlockListActivity {
    private ArrayList<EventData> mEventsList = new ArrayList<EventData>();
    private ArrayList<HashMap<String, String>> mAdapterList = new ArrayList<HashMap<String, String>>();
    private SimpleAdapter mAdapter;
    private int mOffset = 0;
    private int mLimit = 10;
    private boolean mHasEvents = false;
    private ProgressBar mLoadingIndicator;
    private ViewGroup mFooter;
    private Button mLoadMoreButton;
    private GroupData mGroup;
    private boolean mCanRemoveFooter = false;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the UI.
        setContentView(R.layout.events_list);

        // Pull the group information from the current intent.
        mGroup = (new Router(this)).getIntentGroup();

        // Set up the ActionBar.
        mActionBar.setTitle(mGroup.getTitle());
        mActionBar.setSubtitle("Events");

        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Create the adapter for the ListView.
        String[] from = {"line1", "line2"};
        int[] to = {android.R.id.text1, android.R.id.text2};
        mAdapter = new SimpleAdapter(GroupEventsActivity.this, mAdapterList, android.R.layout.simple_list_item_2, from, to);

        // Inflate and get a handle on our load more and loading indicator footer for our list.
        mFooter = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.load_more, null);
        mLoadMoreButton = (Button) mFooter.findViewById(R.id.load_more_button);
        mLoadingIndicator = (ProgressBar) mFooter.findViewById(R.id.loading_indicator);

        // When the load button is clicked, show the loading indicator and load more events.
        mLoadMoreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadMoreButton.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                new GetGroupEventsTask().execute(mGroup);
            }
        });

        // Add our footer to the bottom of the list and set our adapter.
        getListView().addFooterView(mFooter);
        setListAdapter(mAdapter);

        // Load the group's first set of events.
        new GetGroupEventsTask().execute(mGroup);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent;
        if (mHasEvents) {
            // If the event has a location and the API is correct, load the map event display.
            if ((!(mEventsList.get(position).getLatitude().equals("")
                    && mEventsList.get(position).getLongitude().equals(""))) && (!(Build.VERSION.SDK_INT < 11))) {
                intent = (new Router(this)).getEventDisplayActivityIntent(mEventsList.get(position));
            } else { // If these conditions are not met, load the textual event information display.
                intent = (new Router(this)).getEventDetailActivityIntent(mEventsList.get(position));
            }
            startActivity(intent);
        }
    }

    /**
     * Gets a group's events using a rest call and places the data into a hash map.
     */
    public class GetGroupEventsTask extends AsyncTask<GroupData, Void, String> {

        protected String doInBackground(GroupData... groups) {
            // Call the API to return the group's events.
            return RestApiV1.getGroupEventsByGroupId(groups[0].getUUID(), mOffset, mLimit);
        }

        protected void onPostExecute(String jsonResult) {
            EventsMap events = new EventsMap(jsonResult);
            HashMap<String, String> map;

            if (!events.isEmpty()) {
                // Add our new set of events into our list.
                mEventsList.addAll(events.getEventData());

                // If we did not load a full set of groups, we can remove our load more button.
                if (events.size() < mLimit) {
                    mCanRemoveFooter  = true;
                }

                // If the group has events, put the information into list for our adapter.
                if (!mEventsList.isEmpty()) {
                    for (int i = mOffset; i < mEventsList.size(); i++) {
                        map = new HashMap<String, String>();
                        map.put("line1", mEventsList.get(i).getTitle());

                        String start = mEventsList.get(i).getStartDateString();
                        map.put("line2", start);
                        mAdapterList.add(map);
                    }
                    mOffset = mEventsList.size();
                    mHasEvents = true;
                }
            } else { // If the group has no events make a blank list item.
                if (mEventsList.isEmpty()) {
                    map = new HashMap<String, String>();
                    map.put("line1", "No events to display.");
                    map.put("line2", "");
                    mAdapterList.add(map);
                    mAdapter.notifyDataSetChanged();
                    mHasEvents = false;
                    // Remove our footer.
                    getListView().removeFooterView(mFooter);
                }
            }

            // Update our ListView.
            mAdapter.notifyDataSetChanged();

            // Remove our load more button if we can.
            if (mCanRemoveFooter) {
                getListView().removeFooterView(mFooter);
            } else { // Reset the load more button.
                mLoadMoreButton.setVisibility(View.VISIBLE);
                mLoadingIndicator.setVisibility(View.GONE);
            }
        }
    }
}