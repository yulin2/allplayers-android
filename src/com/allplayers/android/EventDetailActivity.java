package com.allplayers.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.widget.SimpleAdapter;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.EventData;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class EventDetailActivity extends AllplayersSherlockListActivity {
    private EventData mEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_activity_layout);

        // Create a list for the event detail items.
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        // Pull the event data from the current intent.
        mEvent = (new Router(this)).getIntentEvent();

        // Add the title of the event.
        HashMap<String, String> map;
        map = new HashMap<String, String>();
        map.put("line1", "Title");
        map.put("line2", mEvent.getTitle());
        list.add(map);

        // Add the description of the event if it exists.
        if (mEvent.getDescription() != "") {
            map = new HashMap<String, String>();
            map.put("line1", "Description");
            map.put("line2", mEvent.getDescription());
            list.add(map);
        }

        // Adds the category of the event if it exists.
        if (mEvent.getCategory() != "") {
            map = new HashMap<String, String>();
            map.put("line1", "Category");
            map.put("line2", mEvent.getCategory());
            list.add(map);
        }

        // Adds the start date of the event.
        map = new HashMap<String, String>();
        map.put("line1", "Start");
        map.put("line2", mEvent.getStartDateString());
        list.add(map);

        // Adds the end date of the event.
        map = new HashMap<String, String>();
        map.put("line1", "End");
        map.put("line2", mEvent.getEndDateString());
        list.add(map);

        // Create an adapter and the ListView.
        String[] from = { "line1", "line2" };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, from, to);
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
