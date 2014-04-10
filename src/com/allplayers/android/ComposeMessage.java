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

/**
 * Interface for the user to compose a message after selecting its recipients.
 */
public class ComposeMessage extends AllplayersSherlockActivity {
    
    private Activity mActivity = this;
    private ArrayList<String> mRecipientUuidList = new ArrayList<String>();
    private Button mSendButton;
    private CreateNewMessageTask mCreateNewMessageTask;
    
    private String mMessageBody;
    private String mMessageSubject;

    /**
     *  Called when the activity is starting.  
     *  
     *  @param savedInstanceState If the activity is being re-initialized after previously being
     *  shut down then this Bundle contains the data it most recently supplied in
     *  onSaveInstanceState(Bundle). Otherwise it is null.
     */
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
        
        // Set the subtitle for the action bar depending on how many message recipients there are.
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

        // Set up the send button.
        mSendButton  = (Button)findViewById(R.id.sendMessageButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // Pull the body and subject from the form.
                mMessageBody = bodyField.getText().toString();
                mMessageSubject = subjectField.getText().toString();

                // Spawn a thread to send the message.
                mCreateNewMessageTask = new CreateNewMessageTask();
                mCreateNewMessageTask.execute(mMessageSubject, mMessageBody);
            }
        });
    }
    
    /**
     * Called when you are no longer visible to the user. You will next receive either onRestart(),
     * onDestroy(), or nothing, depending on later user activity.
     */
    @Override
    public void onStop() {
        super.onStop();
        
        if (mCreateNewMessageTask != null) {
            mCreateNewMessageTask.cancel(true);
        }
    }

    /**
     * Sends the user's message.
     */
    public class CreateNewMessageTask extends AsyncTask<String, Void, Void> {
        Toast toast;
        
        /**
         * Runs on the UI thread before doInBackground(Params...).
         */
        @Override
        protected void onPreExecute() {
            
            // Show the progress spinner and a message so we know that the message is in progress of
            // being sent.
            setProgressBarIndeterminateVisibility(true);
            toast = Toast.makeText(mActivity, "Sending Message...", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        
        /**
         * Performs a computation on a background thread.
         * @param params 
         *      • params[0] Message sumbject.
         *      • params[1] Message body.
         */
        @Override
        protected Void doInBackground(String... params) {
            RestApiV1.createMessageNew(mRecipientUuidList.toArray(new String[mRecipientUuidList.size()]), params[0], params[1]);
            return null;
        }
        
        /**
         * Runs on the UI thread after doInBackground(Params...).
         * @param voids Nothing... Nothing at all.
         */
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
