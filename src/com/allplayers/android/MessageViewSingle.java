package com.allplayers.android;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.allplayers.android.activities.AllplayersSherlockActivity;
import com.allplayers.objects.MessageData;
import com.allplayers.objects.MessageThreadData;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

/**
 * Displays the content of a message.
 */
public class MessageViewSingle extends AllplayersSherlockActivity {

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
        setContentView(R.layout.viewsinglemessage);

        // Grab the message from the intent.
        Router router = new Router(this);
        MessageThreadData messageThreadList = router.getIntentMessageThread();
        final MessageData message = router.getIntentMessage();

        // Populate the fields.
        String subject = messageThreadList.getSubject();
        String sender = messageThreadList.getSenderName();
        String date = messageThreadList.getDateString();
        String body = messageThreadList.getMessageBody();

        final TextView subjectText = (TextView)findViewById(R.id.subjectText);
        subjectText.setText("This is the Subject.");
        subjectText.setText(subject);

        final TextView senderText = (TextView)findViewById(R.id.senderText);
        senderText.setText("This is the Sender's Name.");
        senderText.setText("From: " + sender);

        final TextView dateText = (TextView)findViewById(R.id.dateText);
        dateText.setText("This is the Date last sent.");
        dateText.setText("" + date);

        final TextView bodyText = (TextView)findViewById(R.id.bodyText);
        bodyText.setText("This is the body text.");
        bodyText.setText(body);
        bodyText.setMovementMethod(LinkMovementMethod.getInstance());

        // Setup the reply button
        final Button replyButton = (Button)findViewById(R.id.replyButton);

        replyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = (new Router(MessageViewSingle.this)).getMessagReplyIntent(message);
                startActivity(intent);
            }
        });

        // Set up the ActionBar.
        mActionBar.setTitle("Messages");
        mActionBar.setSubtitle("Message From: " + messageThreadList.getSenderName());

        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);
    }
}