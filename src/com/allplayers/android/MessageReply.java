package com.allplayers.android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.allplayers.android.activities.AllplayersSherlockActivity;
import com.allplayers.objects.MessageData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

/**
 * Activity for the user to use to reply to a message.
 */
public class MessageReply extends AllplayersSherlockActivity {
    
    private PostMessageTask mPostMessageTask;
    
    private String mMessageBody;
    private String mThreadId;
   
    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in
     * onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replymessage);

        // Set up the ActionBar
        mActionBar.setTitle("Messages");
        mActionBar.setSubtitle("Reply");

        // Set up the Side Navigation Menu
        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Get the message information
        MessageData message = (new Router(this)).getIntentMessage();

        String subject = message.getSubject();
        String sender = message.getLastSender();
        String date = message.getDateString();
        mThreadId = message.getThreadID();

        // Setup the message information on the page.
        final TextView subjectText = (TextView)findViewById(R.id.subjectText);
        subjectText.setText(subject);

        final TextView senderText = (TextView)findViewById(R.id.senderText);
        senderText.setText("From: " + sender);

        final TextView dateText = (TextView)findViewById(R.id.dateText);
        dateText.setText("Last Message: " + date);

        // Set up the editable body text field.
        final EditText bodyField = (EditText)findViewById(R.id.bodyField);
        bodyField.setText("");

        // Set up the send button.
        final Button sendButton = (Button)findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMessageBody = bodyField.getText().toString();

                mPostMessageTask = new PostMessageTask();
                mPostMessageTask.execute(Integer.parseInt(mThreadId), mMessageBody);

                Toast toast = Toast.makeText(getBaseContext(), "Message Sent!", Toast.LENGTH_LONG);
                toast.show();

                finish();
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
        
        if (mPostMessageTask != null) {
            mPostMessageTask.cancel(true);
        }
    }

    /**
     * Posts a user's message.
     */
    public class PostMessageTask extends AsyncTask<Object, Void, Void> {
        
        /**
         * Performs a calculation on the background thread. Sends the composed message.
         * 
         * @return The result of the API call.
         */
        protected Void doInBackground(Object... args) {
            RestApiV1.createMessageReply((Integer)args[0], (String)args[1]);
            return null;
        }
    }
}