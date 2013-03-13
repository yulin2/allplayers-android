package com.allplayers.android;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.allplayers.objects.MessageData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MessageReply extends SherlockActivity implements ISideNavigationCallback {
    private String threadID;
    private String sendBody;
    private ActionBar actionbar;
    private SideNavigationView sideNavigationView;

    /** called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.replymessage);

        actionbar = getSupportActionBar();
        actionbar.setIcon(R.drawable.menu_icon);
        actionbar.setTitle("Messages");
        actionbar.setSubtitle("Reply");

        sideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);

        MessageData message = (new Router(this)).getIntentMessage();

        String subject = message.getSubject();
        String sender = message.getLastSender();
        String date = message.getDateString();
        threadID = message.getThreadID();

        final TextView subjectText = (TextView)findViewById(R.id.subjectText);
        subjectText.setText("This is the Subject.");
        subjectText.setText(subject);

        final TextView senderText = (TextView)findViewById(R.id.senderText);
        senderText.setText("This is the Sender's Name.");
        senderText.setText("From: " + sender);

        final TextView dateText = (TextView)findViewById(R.id.dateText);
        dateText.setText("This is the Date last sent.");
        dateText.setText("Last Message: " + date);


        final EditText bodyField = (EditText)findViewById(R.id.bodyField);
        bodyField.setText("This is the Body text.");
        bodyField.setText("");

        final Button sendButton = (Button)findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendBody = bodyField.getText().toString();

                PostMessageTask helper = new PostMessageTask();
                helper.execute(Integer.parseInt(threadID), sendBody);

                Toast toast = Toast.makeText(getBaseContext(), "Message Sent!", Toast.LENGTH_LONG);
                toast.show();

                finish();
            }
        });
    }

    /**
     * Creates the Action Bar Options Menu.
     * @param menu: The menu to be created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.defaultmenu, menu);

        return true;
    }

    /**
     * Listener for the Action Bar Options Menu.
     * @param item: The selected menu item.
     * TODO: Add options for:
     *          Logout
     *          Search
     *          Refresh
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

        case android.R.id.home:
            sideNavigationView.toggleMenu();

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Listener for the Side Navigation Menu.
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

        default:
            return;
        }

        finish();
    }

    /**
     * Helper method for onSideNavigationItemClick. Starts the passed in
     * activity.
     * @param activity: The activity to be started.
     */
    private void invokeActivity(Class activity) {

        Intent intent = new Intent(this, activity);
        startActivity(intent);

        overridePendingTransition(0, 0); // Disables new activity animation.
    }

    /*
     * Posts a user's message using a rest call.
     * It was necessary to use an "Object" due to the fact that you cannot pass
     *      variables of different type into doIbBackground.
     */
    public class PostMessageTask extends AsyncTask<Object, Void, Void> {
        protected Void doInBackground(Object... args) {
            RestApiV1.postMessage((Integer)args[0], (String)args[1]);
            return null;
        }
    }
}