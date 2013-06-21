package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.allplayers.android.activities.AllplayersSherlockActivity;
import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.google.gson.Gson;

public class ComposeMessage extends AllplayersSherlockActivity {
    private String mMessageBody;
    private String mMessageSubject;
    private ArrayList<String> mRecipientUuidList = new ArrayList<String>();
    private Activity mActivity = this;

    /** called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.composemessage);

        // Set up the ActionBar.
        mActionBar.setTitle("Compose Message");

        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Pull the list of recipients from the intent.
        Intent intent = getIntent();
        if (intent.hasExtra("userData")) {
            try {
                JSONArray jsonArray = new JSONArray(intent.getStringExtra("userData"));
                if (jsonArray.length() > 0) {
                    // Used to create GroupMemberData objects from json.
                    Gson gson = new Gson();
                    // Add in the user's uuids into the recipient list.
                    for (int i = 0; i < jsonArray.length(); i++) {
                        GroupMemberData member = gson.fromJson(jsonArray.getString(i), GroupMemberData.class);
                        mRecipientUuidList.add(member.getUUID());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (mRecipientUuidList.size() == 1) {
            mActionBar.setSubtitle("New Message to 1 recipient");
        } else {
            mActionBar.setSubtitle("New Message to " + mRecipientUuidList.size() + " recipients");
        }

        // The field for the message subject.
        final EditText subjectField = (EditText)findViewById(R.id.subjectField);
        subjectField.setText("");

        // The field for the message body.
        final EditText bodyField = (EditText)findViewById(R.id.bodyField);
        bodyField.setText("");

        final Button sendButton = (Button)findViewById(R.id.sendMessageButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pull the body and subject from the form.
                mMessageBody = bodyField.getText().toString();
                mMessageSubject = subjectField.getText().toString();

                // Spawn a thread to send the message.
                new createNewMessageTask().execute(mMessageSubject, mMessageBody);
            }
        });
    }

    /**
     * Posts a user's message using a rest call.
     */
    public class createNewMessageTask extends AsyncTask<String, Void, Void> {
        Toast toast;
        
        @Override
        protected void onPreExecute() {
            
            // Show the progress spinner and a message so we know that the message is in progress of
            // being sent.
            setProgressBarIndeterminateVisibility(true);
            toast = Toast.makeText(mActivity, "Sending Message...", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        
        @Override
        protected Void doInBackground(String... params) {
            RestApiV1.createNewMessage(mRecipientUuidList.toArray(new String[mRecipientUuidList.size()]), params[0], params[1]);
            return null;
        }
        
        @Override
        protected void onPostExecute(Void voids) {
            toast.cancel();
            toast = Toast.makeText(mActivity, "Message Sent!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            mActivity.finish();
        }
    }
}