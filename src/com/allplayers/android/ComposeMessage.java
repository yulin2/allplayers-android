package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.allplayers.android.activities.AllplayersSherlockActivity;
import com.allplayers.objects.GroupMemberData;
import com.allplayers.objects.MessageData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.google.gson.Gson;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ComposeMessage extends AllplayersSherlockActivity {
    private String threadID;
    private String sendBody;
    private String sendSubject;
    private ActionBar actionbar;
    private SideNavigationView sideNavigationView;
    private ArrayList<GroupMemberData> recipientList = new ArrayList<GroupMemberData>();
    private ArrayList<String> recipientUuidList = new ArrayList<String>();

    /** called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.composemessage);

        actionbar = getSupportActionBar();
        actionbar.setIcon(R.drawable.menu_icon);
        actionbar.setTitle("Compose Message");
        actionbar.setSubtitle("New Message");

        sideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);

        Intent intent = getIntent();        
        if(intent.hasExtra("userData")) {
            try {
                JSONArray jsonArray = new JSONArray(intent.getStringExtra("userData"));
                if (jsonArray.length() > 0) {
                    // Used to create GroupMemberData objects from json.
                    Gson gson = new Gson();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        GroupMemberData member = gson.fromJson(jsonArray.getString(i), GroupMemberData.class);
                        recipientUuidList.add(member.getUUID());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
                
        // The field for the message subject.
        final EditText subjectField = (EditText)findViewById(R.id.subjectField);
        subjectField.setText("");
        
        // The field for the message body.
        final EditText bodyField = (EditText)findViewById(R.id.bodyField);
        bodyField.setText("");

        final Button sendButton = (Button)findViewById(R.id.sendMessageButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendBody = bodyField.getText().toString();
                sendSubject = subjectField.getText().toString();

                createNewMessageTask helper = new createNewMessageTask();
                helper.execute(sendSubject, sendBody);

                Toast toast = Toast.makeText(getBaseContext(), "Message Sent!", Toast.LENGTH_LONG);
                toast.show();

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

    /*
     * Posts a user's message using a rest call.
     * It was necessary to use an "Object" due to the fact that you cannot pass
     *      variables of different type into doIbBackground.
     */
    public class createNewMessageTask extends AsyncTask<Object, Void, Void> {
        protected Void doInBackground(Object... args) {
            RestApiV1.createNewMessage(recipientUuidList.toArray(new String[recipientUuidList.size()]), (String)args[0], (String)args[1]);
            return null;
        }
    }
}