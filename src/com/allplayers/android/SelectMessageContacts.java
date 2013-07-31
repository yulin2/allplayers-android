package com.allplayers.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.GroupMemberData;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.google.gson.Gson;

/**
 * "Main Screen" for message composition. It holds an aggregate list of all intended message
 * recipients and contains the navigation buttons to add user recipients, add group recipients, and
 * compose the message itself.
 */
public class SelectMessageContacts extends AllplayersSherlockListActivity {
    
    private ArrayAdapter<String> mAdapter;
    private ArrayList<GroupMemberData> mRecipientList = new ArrayList<GroupMemberData>();
    private ArrayList<GroupMemberData> mSelectedRecipients = new ArrayList<GroupMemberData>();

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in
     * onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice);

        // Check if there is any data already in a previous version of this activity.
        if (icicle != null) {
            String currentRecipients = icicle.getString("currentRecipients");
            addRecipientsToList(currentRecipients);
        }

        // Check if any data was sent in from a GroupPageActivity broadcast.
        if (getIntent().getExtras() != null) {
            addRecipientsToList(getIntent().getExtras().getString("broadcastRecipients"));
        }
        setContentView(R.layout.selectmessagecontacts);

        // Set up the ActionBar.
        mActionBar.setTitle("Compose Messsage");
        mActionBar.setSubtitle("Recipients");

        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Set up the page's ListView
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        setListAdapter(mAdapter);
        ListView listView = getListView();
        for (int i = 0; i < getListAdapter().getCount(); i++) {
            listView.setItemChecked(i, true);
        }

        // "Add User Recipient" button.
        final Button addUserRecipientButton = (Button)findViewById(R.id.addGroupmatesRecipientButton);
        addUserRecipientButton.setOnClickListener(new View.OnClickListener() {
            
            /**
             * Called when a view has been clicked.
             * 
             * @param v: The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMessageContacts.this, SelectUserContacts.class);
                startActivityForResult(intent, 0);
            }
        });

        // "Add Friend Recipient" button.
        final Button addGroupRecipientButton = (Button)findViewById(R.id.addFriendsRecipientButton);
        addGroupRecipientButton.setOnClickListener(new View.OnClickListener() {
            
            /**
             * Called when a view has been clicked.
             * 
             * @param v: The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMessageContacts.this, SelectFriendContacts.class);
                startActivityForResult(intent, 1);
            }
        });

        // "Compose Message" button.
        final Button composeMessageButton = (Button)findViewById(R.id.composeMessageButton);
        composeMessageButton.setOnClickListener(new View.OnClickListener() {
            
            /**
             * Called when a view has been clicked.
             * 
             * @param v: The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                if (!mRecipientList.isEmpty()) {
                    Intent intent = new Intent(SelectMessageContacts.this, ComposeMessage.class);
                    Gson gson = new Gson();
                    String userData = gson.toJson(mSelectedRecipients);
                    intent.putExtra("userData", userData);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), "You need to add at least one recipient", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it. The resultCode will be
     * RESULT_CANCELED if the activity explicitly returned that, didn't return any result, or
     * crashed during its operation.
     * 
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     * allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its
     * setResult().
     * @param data An Intent, which can return result data to the caller (various data can be
     * attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 || requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String userData = data.getStringExtra("userData");
                addRecipientsToList(userData);
            }
        }
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so that the state
     * can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle) (the Bundle populated
     * by this method will be passed to both).
     * 
     * @param icicle Bundle in which to place your saved state.
     */
    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        Gson gson = new Gson();
        String currentRecipients = gson.toJson(mRecipientList);
        icicle.putString("currentRecipients", currentRecipients);
    }
    
    /**
     * This method will be called when an item in the list is selected.
     * 
     * @param l: The ListView where the click happened.
     * @param v: The view that was clicked within the ListView.
     * @param position: The position of the view in the list.
     * @param id: The row id of the item that was clicked.
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (mSelectedRecipients.contains(mRecipientList.get(position))) {
            mSelectedRecipients.remove(mRecipientList.get(position));
        } else {
            mSelectedRecipients.add(mRecipientList.get(position));
        }
    }

    /**
     * Adds recipients to an ArrayList directly from a json string returned by the API.
     * 
     * @param json The recipients to be added to the ArrayList in the form of a json string.
     */
    public void addRecipientsToList(String json) {
        try {
            int previousSize = mRecipientList.size();
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() > 0) {
                // Used to create GroupMemberData objects from json.
                Gson gson = new Gson();
                for (int i = 0; i < jsonArray.length(); i++) {
                    GroupMemberData member = gson.fromJson(jsonArray.getString(i), GroupMemberData.class);
                    if (member.isNew(mRecipientList)) {
                        mRecipientList.add(member);
                        mSelectedRecipients.add(member);
                    }
                }
                for (int i = previousSize; i < mRecipientList.size(); i++) {
                    GroupMemberData member = mRecipientList.get(i);
                    mAdapter.add(member.getName());
                }
            }
            Collections.sort(mRecipientList, new RecipientComparator());
            mAdapter.sort(new NameComparator());
            mAdapter.notifyDataSetChanged();
            ListView listView = getListView();
            for (int i = 0; i < getListAdapter().getCount(); i++) {
                listView.setItemChecked(i, true);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compares names.
     */
    public class NameComparator implements Comparator<String> {
        
        /**
         * Compares its two arguments for order. Returns a negative integer, zero, or a positive
         * integer as the first argument is less than, equal to, or greater than the second.
         * 
         * @param lhs First object to compare.
         * @param rhs Second object to compare.
         * @return A negative integer, zero, or a positive integer as the first argument is less
         * than, equal to, or greater than the second.
         */
        @Override
        public int compare(String lhs, String rhs) {
            int spaceIndex1 = lhs.lastIndexOf(' ');
            int spaceIndex2 = rhs.lastIndexOf(' ');
            if (spaceIndex1 == -1) spaceIndex1 = 0;
            if (spaceIndex2 == -1) spaceIndex2 = 0;
            return(lhs.substring(spaceIndex1).compareTo(rhs.substring(spaceIndex2)));
        }
    }

    /**
     * Conpares recipients.
     */
    public class RecipientComparator implements Comparator<Object> {

        /**
         * Compares its two arguments for order. Returns a negative integer, zero, or a positive
         * integer as the first argument is less than, equal to, or greater than the second.
         * 
         * @param lhs First object to compare.
         * @param rhs Second object to compare.
         * @return A negative integer, zero, or a positive integer as the first argument is less
         * than, equal to, or greater than the second.
         */
        @Override
        public int compare(Object lhs, Object rhs) {
            NameComparator comp = new NameComparator();
            return comp.compare(((GroupMemberData) lhs).getName(), ((GroupMemberData) rhs).getName());
        }
    }
}
