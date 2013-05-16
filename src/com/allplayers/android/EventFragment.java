package com.allplayers.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.allplayers.objects.EventData;
import com.allplayers.rest.RestApiV1;

public class EventFragment extends ListFragment {
    private ArrayList<EventData> mEventsList;
    private ArrayList<HashMap<String, String>> mTimeList = new ArrayList<HashMap<String, String>>(2);
    private boolean hasEvents = false;
    private String mJsonResult;
    private Activity mParentActivity;
    private static final String LINE_ONE_KEY = "line1";
    private static final String LINE_TWO_KEY = "line2";
    private SimpleAdapter mAdapter;
    private ProgressBar mLoadingIndicator;

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
        mLoadingIndicator = new ProgressBar(mParentActivity);
        getListView().addFooterView(mLoadingIndicator);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent;
        if (hasEvents) {
            if ((!(mEventsList.get(position).getLatitude().equals("")
                    && mEventsList.get(position).getLatitude().equals(""))) && (!(Build.VERSION.SDK_INT < 11))) {
                intent = (new Router(mParentActivity)).getEventDisplayActivityIntent(mEventsList.get(position));
            } else {
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
        mEventsList = events.getEventData();
        HashMap<String, String> map;

        if (!mEventsList.isEmpty()) {
            for (int i = 0; i < mEventsList.size(); i++) {
                map = new HashMap<String, String>();
                map.put(LINE_ONE_KEY, mEventsList.get(i).getTitle());

                String start = mEventsList.get(i).getStartDateString();
                map.put(LINE_TWO_KEY, start);
                mTimeList.add(map);
            }
            mAdapter.notifyDataSetChanged();
            hasEvents = true;
        } else {
            map = new HashMap<String, String>();
            map.put(LINE_ONE_KEY, "No events to display.");
            map.put(LINE_TWO_KEY, "");
            mTimeList.add(map);
            mAdapter.notifyDataSetChanged();
            hasEvents = false;
        }
        getListView().removeFooterView(mLoadingIndicator);
    }

    /*
     * Gets event information for a user using a rest call.
     */
    public class GetUserEventsTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... args) {
            return RestApiV1.getUserEvents(0);
        }

        protected void onPostExecute(String jsonResult) {
            mJsonResult = jsonResult;
            setEventsMap();
        }
    }
}
