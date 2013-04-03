package com.allplayers.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
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
	private ArrayList<GroupMemberData> recipientList = new ArrayList<GroupMemberData>();
    private ActionBar actionbar;
    private SideNavigationView sideNavigationView;
    private ArrayAdapter<String> adapter;
    
    /**
     * Called when the activity is created or recreated. This sets up the action bar, side 
     * navigation interface, and page UI. It also controls the flow of data between the message 
     * composition activities. 
     * 
     * @param savedInstanceState: Passes data from other instances of the same activity.
     */
    @Override
    public void onCreate(Bundle icicle) {
    	super.onCreate(icicle);
        System.out.println("we started a new selectMessage");

        System.out.println(icicle);
        if (icicle != null) {
            String currentRecipients = icicle.getString("currentRecipients");
            System.out.println("bundle was not null");
            addRecipientsToList(currentRecipients);
        }
        setContentView(R.layout.selectmessagecontacts);

        actionbar = getSupportActionBar();
        actionbar.setIcon(R.drawable.menu_icon);
        actionbar.setTitle("Compose Messsage");
        actionbar.setSubtitle("Recipients");

        sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);

        Intent intent = getIntent();
        if (intent.hasExtra("userData")) {
            addRecipientsToList(intent.getStringExtra("userData"));
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        
        // "Add User Recipient" button.
        final Button addUserRecipientButton = (Button)findViewById(R.id.addUserRecipientButton);
        addUserRecipientButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SelectMessageContacts.this, SelectUserContacts.class);
                startActivityForResult(intent, 0);
            }
        });
        
        // "Add Group Recipient" button.
        final Button addGroupRecipientButton = (Button)findViewById(R.id.addGroupRecipientButton);
        addGroupRecipientButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SelectMessageContacts.this, SelectGroupContacts.class);
                startActivityForResult(intent, 1);
            }
        });
        
        // "Compose Message" button.
        final Button composeMessageButton = (Button)findViewById(R.id.composeMessageButton);
        composeMessageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SelectMessageContacts.this, ComposeMessage.class);
                Gson gson = new Gson();
                String userData = gson.toJson(recipientList);
                System.out.println("In SelectMessageContacts I sent to ComposeMessage this " + userData);
                intent.putExtra("userData", userData);
                startActivity(intent);
            }
        });
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 || requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String userData = data.getStringExtra("userData");
                addRecipientsToList(userData);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        Gson gson = new Gson();
        String currentRecipients = gson.toJson(recipientList);
        icicle.putString("currentRecipients", currentRecipients);
        System.out.println("added " + currentRecipients + " to the icicle");
    }

    public void addRecipientsToList(String json) {
        System.out.println("Adding \n" + json + "\nto the list");
        try {
            int previousSize = recipientList.size();
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() > 0) {
                // Used to create GroupMemberData objects from json.
                Gson gson = new Gson();
                for (int i = 0; i < jsonArray.length(); i++) {
                    GroupMemberData member = gson.fromJson(jsonArray.getString(i), GroupMemberData.class);
                    if (member.isNew(recipientList)) {
                        recipientList.add(member);
                    }
                }
                for (int i = previousSize; i < recipientList.size(); i++) {
                    GroupMemberData member = (GroupMemberData) recipientList.get(i);
                    adapter.add(member.getName());
                }
            }
            Collections.sort(recipientList, new RecipientComparator());
            adapter.sort(new NameComparator());
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    
    public class NameComparator implements Comparator<String> {
		@Override
		public int compare(String lhs, String rhs) {
			int spaceIndex1 = lhs.lastIndexOf(' ');
			int spaceIndex2 = rhs.lastIndexOf(' ');
			if(spaceIndex1 == -1) spaceIndex1 = 0;
			if(spaceIndex2 == -1) spaceIndex2 = 0;
			return(lhs.substring(spaceIndex1).compareTo(rhs.substring(spaceIndex2)));
		}   	
    }
    
    public class RecipientComparator implements Comparator<Object> {

		@Override
		public int compare(Object lhs, Object rhs) {
			NameComparator helper = new NameComparator();
			return helper.compare(((GroupMemberData) lhs).getName(),((GroupMemberData) rhs).getName());
		}
    	
    }
}