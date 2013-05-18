package com.allplayers.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
    private ArrayList<EventData> eventsList;
    private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(2);
    private boolean hasEvents = false;
    private ProgressBar loading;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.events_list);
        loading = (ProgressBar) findViewById(R.id.progress_indicator);

        GroupData group = (new Router(this)).getIntentGroup();

        mActionBar.setTitle(group.getTitle());
        mActionBar.setSubtitle("Events");

        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        new GetIntentGroupTask().execute(group);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent;
        if (hasEvents) {
            if ((!(eventsList.get(position).getLatitude().equals("")
                    && eventsList.get(position).getLatitude().equals(""))) && (!(Build.VERSION.SDK_INT < 11))) {
                intent = (new Router(this)).getEventDisplayActivityIntent(eventsList.get(position));
            } else {
                intent = (new Router(this)).getEventDetailActivityIntent(eventsList.get(position));
            }
            startActivity(intent);
        }
    }

    /*
     * Gets a group's events using a rest call and places the data into a hash map.
     */
    public class GetIntentGroupTask extends AsyncTask<GroupData, Void, String> {

        protected String doInBackground(GroupData... groups) {
            return RestApiV1.getGroupEventsByGroupId(groups[0].getUUID(), 0, 0);
        }

        protected void onPostExecute(String jsonResult) {
            EventsMap events = new EventsMap(jsonResult);
            eventsList = events.getEventData();

            HashMap<String, String> map;
            if (!eventsList.isEmpty()) {
                for (int i = 0; i < eventsList.size(); i++) {
                    map = new HashMap<String, String>();
                    map.put("line1", eventsList.get(i).getTitle());

                    String start = eventsList.get(i).getStartDateString();
                    map.put("line2", start);
                    list.add(map);
                }
                hasEvents = true;
            } else {
                map = new HashMap<String, String>();
                map.put("line1", "No events to display.");
                map.put("line2", "");
                list.add(map);
                hasEvents = false;
            }
            String[] from = {"line1", "line2"};
            int[] to = {android.R.id.text1, android.R.id.text2};
            SimpleAdapter adapter = new SimpleAdapter(GroupEventsActivity.this, list, android.R.layout.simple_list_item_2, from, to);
            setListAdapter(adapter);
            loading.setVisibility(View.GONE);
        }
    }
}