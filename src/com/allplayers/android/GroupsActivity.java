package com.allplayers.android;

import com.allplayers.objects.GroupData;
import com.allplayers.rest.RestApiV1;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class GroupsActivity extends ListActivity {
    private ArrayList<GroupData> groupList;
    private boolean hasGroups = false;
    private String jsonResult;
    private int currentAmountShown = 0;
    ArrayAdapter<String> adapter;
    Button loadMore;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupList = new ArrayList<GroupData>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        loadMore = new Button(this);
        loadMore.setText("Load More");
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetUserGroupsTask helper = new GetUserGroupsTask();
                helper.execute();
            }
        });
        getListView().addFooterView(loadMore);

        //check local storage
        if (LocalStorage.getTimeSinceLastModification("UserGroups") / 1000 / 60 < 60) { //more recent than 60 minutes
            jsonResult = LocalStorage.readUserGroups(getBaseContext());
            updateGroupData();
        } else {
            GetUserGroupsTask helper = new GetUserGroupsTask();
            helper.execute();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (hasGroups) {
            //Display the group page for the selected group
            Intent intent = (new Router(this)).getGroupPageActivityIntent(groupList.get(position));
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            startActivity(new Intent(GroupsActivity.this, FindGroupsActivity.class));
        }

        return super.onKeyUp(keyCode, event);
    }

    /** Populates the list of groups to display to the UI thread. */
    protected void updateGroupData() {
        if (!groupList.isEmpty()) {
            // Counter to check if a full 10 new groups were loaded
            int counter = 0;
            for (int i = currentAmountShown; i < groupList.size(); i++) {
                adapter.add(groupList.get(currentAmountShown).getTitle());
                adapter.notifyDataSetChanged();
                currentAmountShown++;
                counter++;
            }
            // If we did not load 10 groups, we are at the end of the list, so remove the
            // "Load More" button.
            if (counter < 10) {
                loadMore.setVisibility(View.GONE);
            }
            hasGroups = true;
        } else {
            hasGroups = false;
            adapter.add("no groups to display");
        }
    }

    /*
     * Fetches the groups a user belongs to and stores the data locally.
     */
    public class GetUserGroupsTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... args) {
            return RestApiV1.getUserGroups(currentAmountShown);
        }

        protected void onPostExecute(String jsonResult) {
            GroupsActivity.this.jsonResult += jsonResult;
            LocalStorage.writeUserGroups(getBaseContext(), jsonResult, false);
            GroupsMap groups = new GroupsMap(jsonResult);
            groupList.addAll(groups.getGroupData());
            setListAdapter(adapter);
            updateGroupData();
        }
    }
}