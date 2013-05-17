package com.allplayers.android;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.GroupData;
import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class GroupMembersActivity extends AllplayersSherlockListActivity {
    private ProgressBar loading;

    private ArrayList<GroupMemberData> membersList;
    private GroupData mGroup;
    
    private int mOffset;
    private boolean mEndOfData;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOffset = 0;
        mEndOfData = false;
        membersList = new ArrayList<GroupMemberData>();
        
        setContentView(R.layout.members_list);
        loading = (ProgressBar) findViewById(R.id.progress_indicator);

        mGroup = (new Router(this)).getIntentGroup();

        mActionBar = getSupportActionBar();
        mActionBar.setIcon(R.drawable.menu_icon);
        mActionBar.setTitle(mGroup.getTitle());
        mActionBar.setSubtitle("Members");

        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Populate the list with the first 8 members.
        new GetGroupMembersByGroupIdTask().execute(mGroup);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        
        // Check if they pressed the "load more" button.
        if (position == membersList.size()) {
            new GetGroupMembersByGroupIdTask().execute(mGroup); 
            
        }
    
    }

    /*
     * Gets a group's members using a rest call and populates an array with the data.
     */
    public class GetGroupMembersByGroupIdTask extends AsyncTask<GroupData, Void, String> {

        protected String doInBackground(GroupData... groups) {
            return RestApiV1.getGroupMembersByGroupId(groups[0].getUUID(), 8, mOffset);
        }

        protected void onPostExecute(String jsonResult) {
            GroupMembersMap groupMembers = new GroupMembersMap(jsonResult);
            if (groupMembers.size() < 8) {
                mEndOfData = true;
            }
            
            membersList.addAll(groupMembers.getGroupMemberData());
            
            ArrayList<String> values;
            if (!membersList.isEmpty()) {
                values = new ArrayList<String>();
                for (int i = 0; i < membersList.size(); i++) {
                    values.add( membersList.get(i).getName());
                }
            } else {
                values = new ArrayList<String>();
                values.add("No Members to Display");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(GroupMembersActivity.this,
                    android.R.layout.simple_list_item_1, values);
            if(!mEndOfData) {
                adapter.add("--Load More Data--");
                mOffset += 8;
            }
            setListAdapter(adapter);
            loading.setVisibility(View.GONE);
        }
    }
}