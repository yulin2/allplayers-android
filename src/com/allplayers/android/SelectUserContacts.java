package com.allplayers.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.google.gson.Gson;

/**
 * List of selectable message recipients.
 */
public class SelectUserContacts extends AllplayersSherlockListActivity {

    private ArrayList<GroupMemberData> membersList;
    private ArrayList<GroupMemberData> selectedMembers;
    private Intent parentIntent;
    private ProgressBar spinner;

    /**
     * This sets up the action bar, side navigation interface, and page UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the page UI
        setContentView(R.layout.selectusercontacts);

        spinner = (ProgressBar) findViewById(R.id.progress_indicator);

        actionbar = getSupportActionBar();
        actionbar.setIcon(R.drawable.menu_icon);
        actionbar.setTitle("Compose Message");
        actionbar.setSubtitle("Select Individual Recipients");

        sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);

        selectedMembers = new ArrayList<GroupMemberData>();

        GetUserGroupmatesTask helper = new GetUserGroupmatesTask();
        helper.execute();

        final Button doneButton = (Button)findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                parentIntent = new Intent();
                Gson gson = new Gson();
                String userData = gson.toJson(selectedMembers);
                parentIntent.putExtra("userData", userData);
                setResult(Activity.RESULT_OK, parentIntent);
                finish();
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (!selectedMembers.contains(membersList.get(position))) {
            v.setBackgroundResource(R.color.android_blue);
            selectedMembers.add(membersList.get(position));
        } else {
            v.setBackgroundResource(R.drawable.backgroundstate);
            selectedMembers.remove(membersList.get(position));
        }
    }

    /*
     * Gets a group's members using a rest call and populates an array with the
     * data.
     */
    public class GetUserGroupmatesTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... args) {
            return RestApiV1.getUserGroupmates();
        }

        protected void onPostExecute(String jsonResult) {
            Log.d("errorr", jsonResult);
            jsonResult = jsonResult.replaceAll("firstname", "fname");
            jsonResult = jsonResult.replaceAll("lastname", "lname");
            Log.d("errorr", jsonResult);
            GroupMembersMap groupMembers = new GroupMembersMap(jsonResult);
            membersList = groupMembers.getGroupMemberData();
            String[] values;

            if (!membersList.isEmpty()) {
                values = new String[membersList.size()];
                for (int i = 0; i < membersList.size(); i++) {
                    values[i] = membersList.get(i).getName();
                }
            } else {
                values = new String[] {"No members to display"};
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectUserContacts.this,
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
            spinner.setVisibility(View.GONE);
        }
    }
}