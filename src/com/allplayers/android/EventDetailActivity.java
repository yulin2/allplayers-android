package com.allplayers.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.widget.SimpleAdapter;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.EventData;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

/**
 * Shows the details of an event.
 */
public class EventDetailActivity extends AllplayersSherlockListActivity {
    
    private ArrayList<HashMap<String, String>> mList;
    private EventData mEvent;
    private HashMap<String, String> mMap;

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
        setContentView(R.layout.event_detail_activity_layout);

        // Create a list for the event detail items.
        mList = new ArrayList<HashMap<String, String>>();

        // Pull the event data from the current intent.
        mEvent = (new Router(this)).getIntentEvent();

        // Add the title of the event.
        mMap = new HashMap<String, String>();
        mMap.put("line1", "Title");
        mMap.put("line2", mEvent.getTitle());
        mList.add(mMap);

        // Add the description of the event if it exists.
        if (mEvent.getDescription() != "") {
            mMap = new HashMap<String, String>();
            mMap.put("line1", "Description");
            mMap.put("line2", mEvent.getDescription());
            mList.add(mMap);
        }

        // Adds the category of the event if it exists.
        if (mEvent.getCategory() != "") {
            mMap = new HashMap<String, String>();
            mMap.put("line1", "Category");
            mMap.put("line2", mEvent.getCategory());
            mList.add(mMap);
        }

        // Adds the start date of the event.
        mMap = new HashMap<String, String>();
        mMap.put("line1", "Start");
        mMap.put("line2", mEvent.getStartDateString());
        mList.add(mMap);

        // Adds the end date of the event.
        mMap = new HashMap<String, String>();
        mMap.put("line1", "End");
        mMap.put("line2", mEvent.getEndDateString());
        mList.add(mMap);

        // Create an adapter and the ListView.
        String[] from = { "line1", "line2" };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        SimpleAdapter adapter = new SimpleAdapter(this, mList, android.R.layout.simple_list_item_2, from, to);
        setListAdapter(adapter);
        getListView().setEnabled(false);

        // Set up the ActionBar.
        mActionBar.setTitle(mEvent.getTitle());

        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);
    }
}
