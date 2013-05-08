package com.allplayers.android;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.GroupData;
import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class GroupMembersActivity extends AllplayersSherlockListActivity {
    private ProgressBar mProgressBar;

    private ArrayList<GroupMemberData> mMembersList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.members_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_indicator);

        GroupData group = (new Router(this)).getIntentGroup();

        actionbar.setTitle(group.getTitle());
        actionbar.setSubtitle("Members");

        sideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);

        GetGroupMembersByGroupIdTask helper = new GetGroupMembersByGroupIdTask();
        helper.execute(group);
    }

    /*
     * Gets a group's members using a rest call and populates an array with the data.
     */
    public class GetGroupMembersByGroupIdTask extends AsyncTask<GroupData, Void, String> {

        protected String doInBackground(GroupData... groups) {
            // @TODO: Move to asynchronous loading.
            return RestApiV1.getGroupMembersByGroupId(groups[0].getUUID(), 1000);
        }

        protected void onPostExecute(String jsonResult) {
            GroupMembersMap groupMembers = new GroupMembersMap(jsonResult);
            mMembersList = groupMembers.getGroupMemberData();

            String[] values;
            if (!mMembersList.isEmpty()) {
                values = new String[mMembersList.size()];
                for (int i = 0; i < mMembersList.size(); i++) {
                    values[i] = mMembersList.get(i).getName();
                }
            } else {
                values = new String[] {"No members to display"};
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(GroupMembersActivity.this,
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}