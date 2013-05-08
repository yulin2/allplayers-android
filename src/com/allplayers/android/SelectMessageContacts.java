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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private ArrayList<GroupMemberData> mRecipientList = new ArrayList<GroupMemberData>();
    private ArrayAdapter<String> mAdapter;

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

        if (icicle != null) {
            String currentRecipients = icicle.getString("currentRecipients");
            addRecipientsToList(currentRecipients);
        }
        setContentView(R.layout.selectmessagecontacts);

        actionbar.setTitle("Compose Messsage");
        actionbar.setSubtitle("Recipients");

        sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        setListAdapter(mAdapter);

        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, final int position, long arg3) {
                PopupMenu menu = new PopupMenu(SelectMessageContacts.this, view);
                menu.inflate(R.menu.message_recipient_menu);
                menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem arg0) {
                        switch (arg0.getItemId()) {
                        case R.id.removeRecipient:
                            mAdapter.remove(mAdapter.getItem(position));
                            mRecipientList.remove(position);
                            break;
                        case R.id.cancel:
                        }
                        for (int i = 0; i < mAdapter.getCount(); i++) {
                        }
                        return true;
                    }
                });
                menu.show();
                return true;
            }
        });

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
                if (!mRecipientList.isEmpty()) {
                    Intent intent = new Intent(SelectMessageContacts.this, ComposeMessage.class);
                    Gson gson = new Gson();
                    String userData = gson.toJson(mRecipientList);
                    intent.putExtra("userData", userData);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), "You need to add at least one recipient", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

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

    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        Gson gson = new Gson();
        String currentRecipients = gson.toJson(mRecipientList);
        icicle.putString("currentRecipients", currentRecipients);
    }

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
                    }
                }
                for (int i = previousSize; i < mRecipientList.size(); i++) {
                    GroupMemberData member = (GroupMemberData) mRecipientList.get(i);
                    mAdapter.add(member.getName());
                }
            }
            Collections.sort(mRecipientList, new RecipientComparator());
            mAdapter.sort(new NameComparator());
            mAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class NameComparator implements Comparator<String> {
        @Override
        public int compare(String lhs, String rhs) {
            int spaceIndex1 = lhs.lastIndexOf(' ');
            int spaceIndex2 = rhs.lastIndexOf(' ');
            if (spaceIndex1 == -1) spaceIndex1 = 0;
            if (spaceIndex2 == -1) spaceIndex2 = 0;
            return(lhs.substring(spaceIndex1).compareTo(rhs.substring(spaceIndex2)));
        }
    }

    public class RecipientComparator implements Comparator<Object> {

        @Override
        public int compare(Object lhs, Object rhs) {
            NameComparator helper = new NameComparator();
            return helper.compare(((GroupMemberData) lhs).getName(), ((GroupMemberData) rhs).getName());
        }

    }
}