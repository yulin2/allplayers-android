package com.allplayers.android;

import com.allplayers.objects.MessageThreadData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MessageViewSingle extends Activity {
    /** called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewsinglemessage);

        MessageThreadData messageThreadList = Globals.currentMessageThread;

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

        final Button replyButton = (Button)findViewById(R.id.replyButton);

        replyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MessageViewSingle.this, MessageReply.class));
            }
        });
    }
}