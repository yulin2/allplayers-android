package com.allplayers.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.allplayers.android.activities.AllplayersSherlockActivity;
import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.EventData;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class EventDetailActivity extends AllplayersSherlockListActivity {
    private EventData mEvent;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_activity_layout);

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(2);

        mEvent = (new Router(this)).getIntentEvent();
        
        HashMap<String, String> map;
        map = new HashMap<String, String>();
        map.put("line1", "Title");
        map.put("line2", mEvent.getTitle());
        list.add(map);

        if (mEvent.getDescription() != "") {
            map = new HashMap<String, String>();
            map.put("line1", "Description");
            map.put("line2", mEvent.getDescription());
            list.add(map);
        }
        
        if (mEvent.getDescription() != "") {
            map = new HashMap<String, String>();
            map.put("line1", "Category");
            map.put("line2", mEvent.getCategory());
            list.add(map);
        }
        
        map = new HashMap<String, String>();
        map.put("line1", "Start");
        map.put("line2", mEvent.getStartDateString());
        list.add(map);
        
        map = new HashMap<String, String>();
        map.put("line1", "End");
        map.put("line2", mEvent.getEndDateString());
        list.add(map);

        String[] from = { "line1", "line2" };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, from, to);
        setListAdapter(adapter);
        
        actionbar = getSupportActionBar();
        actionbar.setIcon(R.drawable.menu_icon);
        actionbar.setTitle(mEvent.getTitle());

        sideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);
    }
}
