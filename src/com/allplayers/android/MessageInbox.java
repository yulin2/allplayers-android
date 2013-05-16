package com.allplayers.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;

import com.allplayers.android.activities.AllplayersSherlockActivity;
import com.allplayers.objects.MessageData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class MessageInbox extends AllplayersSherlockActivity implements ISideNavigationCallback {
    private ArrayList<MessageData> mMessageList;
    private boolean hasMessages = false;
    private ProgressBar mLoadingIndicator;
    private ListView mList;
    private MessageAdapter mAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inboxlist);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.progress_indicator);

        new GetUserInboxTask().execute();

        mActionBar.setTitle("Messages");
        mActionBar.setSubtitle("Inbox");

        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

    }

    public void populateInbox(String json) {
        MessagesMap messages = new MessagesMap(json);
        mMessageList = messages.getMessageData();

        Collections.reverse(mMessageList);

        mList = (ListView) findViewById(R.id.customListView);
        mList.setClickable(true);

        if (!mMessageList.isEmpty()) {
            hasMessages = true;
        } else {
            hasMessages = false;
        }

        final List<MessageData> messageList2 = mMessageList;
        mAdapter = new MessageAdapter(MessageInbox.this, messageList2);

        mList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
                if (hasMessages) {
                    // Go to the message thread.
                    Intent intent = (new Router(MessageInbox.this)).getMessageThreadIntent(mMessageList.get(position));
                    startActivity(intent);
                }
            }
        });
        mList.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
            final int position, long id) {
                PopupMenu menu = new PopupMenu(getBaseContext(), view);
                menu.inflate(R.menu.message_thread_menu);
                menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        switch (item.getItemId()) {
                        case R.id.delete:
                            new DeleteMessageThreadTask(position).execute();
                            //Toast.makeText(getBaseContext(), "Deleting message", Toast.LENGTH_LONG).show();
                            break;
                        }
                        return true;
                    }
                });
                menu.show();
                return false;
            }

        });

        mList.setAdapter(mAdapter);
    }

    public class GetUserInboxTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... Args) {
            return RestApiV1.getUserInbox();
        }

        protected void onPostExecute(String jsonResult) {
            populateInbox(jsonResult);
            mLoadingIndicator.setVisibility(View.GONE);
        }
    }

    public class DeleteMessageThreadTask extends AsyncTask<Void, Void, String> {
        private int position;

        public DeleteMessageThreadTask(int i) {
            position = i;
        }

        @Override
        protected String doInBackground(Void... params) {
            return RestApiV1.deleteMessageThread(mMessageList.get(position).getId());
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("done")) {
                mMessageList.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        }

    }
}