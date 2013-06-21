package com.allplayers.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.allplayers.objects.EventData;
import com.allplayers.rest.RestApiV1;

/**
 * Fragment displayed by EventsActivity. Lists out all of the user's events.
 */
public class EventFragment extends ListFragment {
    
    private static final int LIMIT = 10;
    private static final String LINE_ONE_KEY = "line1";
    private static final String LINE_TWO_KEY = "line2";
    
    private Activity mParentActivity;
    private ArrayList<EventData> mEventsList = new ArrayList<EventData>();
    private ArrayList<HashMap<String, String>> mTimeList = new ArrayList<HashMap<String, String>>();
    private Button mLoadMoreButton;
    private ProgressBar mLoadingIndicator;
    private SimpleAdapter mAdapter;
    private ViewGroup mFooter;
    
    private boolean mCanRemoveFooter = false;
    private boolean hasEvents = false;
    private int mOffset = 0;
    private String mJsonResult;

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
        
        // Initialize variables.
        mParentActivity = this.getActivity();

        // Set up the adapter.
        String[] from = {LINE_ONE_KEY, LINE_TWO_KEY};
        int[] to = {android.R.id.text1, android.R.id.text2};
        mAdapter = new SimpleAdapter(mParentActivity, mTimeList, android.R.layout.simple_list_item_2, from, to);

        // Fetch the user's events.
        new GetUserEventsTask().execute();
    }

    /**
     * Attach to list view once the view hierarchy has been created.
     * 
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     * saved state as given here.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        
        // Inflate and get a handle on our load more and loading indicator footer for our list.
        mFooter = (ViewGroup) LayoutInflater.from(mParentActivity).inflate(R.layout.load_more, null);
        mLoadMoreButton = (Button) mFooter.findViewById(R.id.load_more_button);
        mLoadingIndicator = (ProgressBar) mFooter.findViewById(R.id.loading_indicator);

        // When the load button is clicked, show the loading indicator and load more events.
        mLoadMoreButton.setOnClickListener(new OnClickListener() {
            
            /**
             * Called when a view has been clicked.
             * 
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                mLoadMoreButton.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                new GetUserEventsTask().execute();
            }
        });

        // Add our footer to the bottom of the list and set our adapter.
        getListView().addFooterView(mFooter);
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
        super.onListItemClick(l, v, position, id);

        Intent intent;
        if (hasEvents) {
            
            // If the event has a loaction and we are on the correct API level, show a map. If we do
            // not meet those conditions, launch only the more detailed event page.
            if ((!(mEventsList.get(position).getLatitude().equals("")
                    && mEventsList.get(position).getLatitude().equals(""))) && (!(Build.VERSION.SDK_INT < 11))) {
                intent = (new Router(mParentActivity)).getEventDisplayActivityIntent(mEventsList.get(position));
            } else {
                intent = (new Router(mParentActivity)).getEventDetailActivityIntent(mEventsList.get(position));
            }
            startActivity(intent);
        }
    }

    /**
     * Populate a map with the event's information.
     */
    protected void setEventsMap() {
        EventsMap events = new EventsMap(mJsonResult);
        HashMap<String, String> map;
        if (!events.isEmpty()) {
            
            // Add new events to our list.
            mEventsList.addAll(events.getEventData());
            
            // If we did not load a full set of groups, we can remove our load more button.
            if (events.size() < LIMIT) {
                mCanRemoveFooter  = true;
            }
            
            // Create the list items for each event.
            for (int i = mOffset; i < mEventsList.size(); i++) {
                map = new HashMap<String, String>();
                map.put(LINE_ONE_KEY, mEventsList.get(i).getTitle());

                String start = mEventsList.get(i).getStartDateString();
                map.put(LINE_TWO_KEY, start);
                mTimeList.add(map);
            }
            
            // Increment our count of how many events we have.
            mOffset += events.size();
            
            // Update the ListView.
            mAdapter.notifyDataSetChanged();
            
            // Remove our load more button if we can.
            if (mCanRemoveFooter) {
                getListView().removeFooterView(mFooter);
            } else {
                mLoadMoreButton.setVisibility(View.VISIBLE);
                mLoadingIndicator.setVisibility(View.GONE);
            }
            hasEvents = true;
        } else {
            
            // If we did not load any new events and no previous events were there,
            // we create a blank list item and indicate no events loaded.
            if (mEventsList.isEmpty()) {
                map = new HashMap<String, String>();
                map.put(LINE_ONE_KEY, "No events to display");
                map.put(LINE_TWO_KEY, "");
                mTimeList.add(map);
                mAdapter.notifyDataSetChanged();
                hasEvents = false;
                
                // Remove our footer.
                getListView().removeFooterView(mFooter);
                getListView().setEnabled(false);
            }
        }
    }

    /**
     * Fetches the user's event information.
     */
    public class GetUserEventsTask extends AsyncTask<Void, Void, String> {

        /**
         * Performs a computation on a background thread. Requests the user's event information from
         * the API.
         * 
         * @return The result of the API call.
         */
        @Override
        protected String doInBackground(Void... args) {
            return RestApiV1.getUserEvents(mOffset, LIMIT);
        }

        /**
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         * 
         * @param jsonResult The result of the API call.
         */
        @Override
        protected void onPostExecute(String jsonResult) {
            mJsonResult = jsonResult;
            setEventsMap();
        }
    }
}
