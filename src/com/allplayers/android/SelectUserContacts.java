package com.allplayers.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;
import com.google.gson.Gson;

/**
 * List of selectable message recipients.
 */
public class SelectUserContacts extends SherlockListActivity {

    private ArrayList<GroupMemberData> mMembersList;
    private ArrayList<GroupMemberData> mSelectedMembers;
    private ProgressBar mLoadingIndicator;
    private ActionBar actionbar;

    /**
     * This sets up the action bar, side navigation interface, and page UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the page UI
        setContentView(R.layout.selectusercontacts);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.progress_indicator);
        
        actionbar = getSupportActionBar();
        actionbar.setTitle("Compose Message");
        actionbar.setSubtitle("Select Individual Recipients");

        mSelectedMembers = new ArrayList<GroupMemberData>();

        GetUserGroupmatesTask helper = new GetUserGroupmatesTask();
        helper.execute();

        final Button doneButton = (Button)findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Gson gson = new Gson();
                String userData = gson.toJson(mSelectedMembers);
                Intent intent = new Intent();
                intent.putExtra("userData", userData);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
    
    /**
     * Listener for the Action Bar Options Menu.
     *
     * @param item: The selected menu item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home: {
        	finish();
            return true;
        }
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (!mSelectedMembers.contains(mMembersList.get(position))) {
            v.setBackgroundResource(R.color.android_blue);
            mSelectedMembers.add(mMembersList.get(position));
        } else {
            v.setBackgroundResource(R.drawable.backgroundstate);
            mSelectedMembers.remove(mMembersList.get(position));
        }
    }

    /*
     * Gets a group's members using a rest call and populates an array with the
     * data.
     */
    public class GetUserGroupmatesTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... args) {
            // @TODO: Move to asynchronous loading.
            return RestApiV1.getUserGroupmates(0);
        }

        protected void onPostExecute(String jsonResult) {
            jsonResult = jsonResult.replaceAll("firstname", "fname");
            jsonResult = jsonResult.replaceAll("lastname", "lname");
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectUserContacts.this,
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
            mLoadingIndicator.setVisibility(View.GONE);
        }
    }
}