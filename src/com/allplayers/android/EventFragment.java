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

public class EventFragment extends ListFragment {
    private ArrayList<EventData> mEventsList = new ArrayList<EventData>();
    private ArrayList<HashMap<String, String>> mTimeList = new ArrayList<HashMap<String, String>>();
    private boolean hasEvents = false;
    private String mJsonResult;
    private Activity mParentActivity;
    private static final String LINE_ONE_KEY = "line1";
    private static final String LINE_TWO_KEY = "line2";
    private SimpleAdapter mAdapter;
    private ProgressBar mLoadingIndicator;
    private ViewGroup mFooter;
    private Button mLoadMoreButton;
    private int mOffset = 0;
    private int mLimit = 10;
    private boolean mCanRemoveFooter = false;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity = this.getActivity();

        String[] from = {LINE_ONE_KEY, LINE_TWO_KEY};
        int[] to = {android.R.id.text1, android.R.id.text2};
        mAdapter = new SimpleAdapter(mParentActivity, mTimeList, android.R.layout.simple_list_item_2, from, to);

        new GetUserEventsTask().execute();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Inflate and get a handle on our load more and loading indicator footer for our list.
        mFooter = (ViewGroup) LayoutInflater.from(mParentActivity).inflate(R.layout.load_more, null);
        mLoadMoreButton = (Button) mFooter.findViewById(R.id.load_more_button);
        mLoadingIndicator = (ProgressBar) mFooter.findViewById(R.id.loading_indicator);

        // When the load button is clicked, show the loading indicator and load more events.
        mLoadMoreButton.setOnClickListener(new OnClickListener() {
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent;
        if (hasEvents) {
            // If the event has a loaction and we are on the correct API level, show a map.
            if ((!(mEventsList.get(position).getLatitude().equals("")
                    && mEventsList.get(position).getLatitude().equals(""))) && (!(Build.VERSION.SDK_INT < 11))) {
                intent = (new Router(mParentActivity)).getEventDisplayActivityIntent(mEventsList.get(position));
            } else {// If we do not meet those conditions, launch only the more detailed event page.
                intent = (new Router(mParentActivity)).getEventDetailActivityIntent(mEventsList.get(position));
            }
            startActivity(intent);
        }
    }

    /*
     * Populates a hash map with event information.
     */
    protected void setEventsMap() {
        EventsMap events = new EventsMap(mJsonResult);
        HashMap<String, String> map;
        if (!events.isEmpty()) {
            // Add new events to our list.
            mEventsList.addAll(events.getEventData());
            // If we did not load a full set of groups, we can remove our load more button.
            if (events.size() < mLimit) {
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
            } else { // Reset the load more button.
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

    /*
     * Gets event information for a user using a rest call.
     */
    public class GetUserEventsTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... args) {
            return RestApiV1.getUserEvents(mOffset, mLimit);
        }

        protected void onPostExecute(String jsonResult) {
            mJsonResult = jsonResult;
            setEventsMap();
        }
    }
}
