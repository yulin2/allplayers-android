package com.allplayers.android;

import java.util.ArrayList;
import java.util.Collections;

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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
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
    
    private ActionBar actionbar;
    private ArrayList<GroupMemberData> contactsList;
    private ArrayList<GroupMemberData> selectedContacts;
    private Intent parentIntent;
    private ProgressBar spinner;
    private SideNavigationView sideNavigationView;

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
        
        selectedContacts = new ArrayList<GroupMemberData>();

        new GetUserContactsTask().execute(); // Fetches the user's contacts (groupmates and friends)
                                             // and stores them in contactsList
        
        final Button doneButton = (Button)findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                parentIntent = new Intent();
                Gson gson = new Gson();
                String userData = gson.toJson(selectedContacts);
                parentIntent.putExtra("userData", userData);
                setResult(Activity.RESULT_OK, parentIntent);
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
                sideNavigationView.toggleMenu();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Listener for the Side Navigation Menu.
     * 
     * @param itemId: The ID of the list item that was selected.
     */
    @Override
    public void onSideNavigationItemClick(int itemId) {

        switch (itemId) {

            case R.id.side_navigation_menu_item1:
                invokeActivity(GroupsActivity.class);
                break;

            case R.id.side_navigation_menu_item2:
                invokeActivity(MessageActivity.class);
                break;

            case R.id.side_navigation_menu_item3:
                invokeActivity(PhotosActivity.class);
                break;

            case R.id.side_navigation_menu_item4:
                invokeActivity(EventsActivity.class);
                break;

            case R.id.side_navigation_menu_item5: {
                search();
                break;
            }

            case R.id.side_navigation_menu_item6: {
                logOut();
                break;
            }

            case R.id.side_navigation_menu_item7: {
                refresh();
                break;
            }

            default:
                return;
        }

        finish();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(!selectedContacts.contains(contactsList.get(position))) {
            v.setBackgroundResource(R.color.android_blue);
            selectedContacts.add(contactsList.get(position));
        }
        else {
            v.setBackgroundResource(R.drawable.backgroundstate);
            selectedContacts.remove(contactsList.get(position));
        }
    }

    /*
     * Gets a group's members using a rest call and populates an array with the
     * data.
     */
    public class GetUserContactsTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... args) {
            return RestApiV1.getUserGroupmates();
        }

        protected void onPostExecute(String jsonResult) {
            jsonResult = jsonResult.replaceAll("firstname", "fname");
            jsonResult = jsonResult.replaceAll("lastname", "lname");
            GroupMembersMap contacts = new GroupMembersMap(jsonResult);
            contactsList = contacts.getGroupMemberData();
            Collections.sort(contactsList, new SelectMessageContacts().new RecipientComparator());
            String[] values;
            if (!contactsList.isEmpty()) {
                values = new String[contactsList.size()];
                for (int i = 0; i < contactsList.size(); i++) {
                    values[i] = contactsList.get(i).getName();
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