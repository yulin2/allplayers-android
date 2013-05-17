package com.allplayers.android;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.GroupData;
import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class GroupMembersActivity extends AllplayersSherlockListActivity {

    private ProgressBar mLoadingIndicator;
    private ArrayList<GroupMemberData> mMembersList;
    private Button mLoadMoreButton;
    private GroupData mGroup;
    private int mOffset;
    private boolean mEndOfData;
    private ArrayAdapter<GroupMemberData> mAdapter;
    private ListView mListView;
    private ViewGroup mFooter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.members_list);

        mOffset = 0;
        mEndOfData = false;
        mMembersList = new ArrayList<GroupMemberData>();
        mListView = getListView();
        mAdapter = new ArrayAdapter<GroupMemberData>(this, android.R.layout.simple_list_item_1, mMembersList);
        mFooter = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.load_more, null);
        mLoadMoreButton = (Button) mFooter.findViewById(R.id.load_more_button);
        mLoadingIndicator = (ProgressBar) mFooter.findViewById(R.id.loading_indicator);

        mLoadMoreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadMoreButton.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                new GetGroupMembersByGroupIdTask().execute(mGroup);
            }
        });

        mListView.addFooterView(mFooter);
        setListAdapter(mAdapter);

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

    /*
     * Gets a group's members using a rest call and populates an array with the data.
     */
    public class GetGroupMembersByGroupIdTask extends AsyncTask<GroupData, Void, String> {

        protected String doInBackground(GroupData... groups) {
            return RestApiV1.getGroupMembersByGroupId(groups[0].getUUID(), 8, mOffset);
        }

        protected void onPostExecute(String jsonResult) {
            GroupMembersMap groupMembers = new GroupMembersMap(jsonResult);
            if (groupMembers.size() == 0) {
                mEndOfData = true;
                if (mMembersList.size() == 0) {
                    GroupMemberData blank = new GroupMemberData();
                    blank.setName("No members to display");
                    mMembersList.add(blank);
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                if (groupMembers.size() < 8) {
                    mEndOfData = true;
                }

                mMembersList.addAll(groupMembers.getGroupMemberData());
                mAdapter.notifyDataSetChanged();
                if (!mEndOfData) {
                    mLoadMoreButton.setVisibility(View.VISIBLE);
                    mLoadingIndicator.setVisibility(View.GONE);
                    mOffset += 8;
                } else {
                    mListView.removeFooterView(mFooter);
                }
            }
        }
    }
}