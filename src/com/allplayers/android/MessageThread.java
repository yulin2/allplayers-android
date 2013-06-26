package com.allplayers.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.MessageData;
import com.allplayers.objects.MessageThreadData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

/**
 * Displays the messages in a message thread.
 */
public class MessageThread extends AllplayersSherlockListActivity {
    
    private ArrayList<MessageThreadData> mMessageThreadList;
    private ArrayList<HashMap<String, String>> mInfoList = new ArrayList<HashMap<String, String>>(2);
    private MessageData mMessage;
    private ProgressBar mLoadingIndicator;
    private SimpleAdapter mAdapter;

    private boolean hasMessages = false;
    private int mThreadId;
    private String mJsonResult = "";

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
        setContentView(R.layout.message_thread);
        
        // Grab the message thread ID from the intent.
        mMessage = (new Router(this)).getIntentMessage();
        String threadID = mMessage.getThreadID();

        // Set up the loading indicator.
        mLoadingIndicator = (ProgressBar) findViewById(R.id.progress_indicator);

        // Set up the Actionbar.
        mActionBar.setTitle("Messages");

        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Grab the thread's messages.
        new PutAndGetMessagesTask().execute(threadID);

        // It has been found that deleting a message through the API completely obliterates it from
        // the site. This is not expected functionality and needs to be researched more.
        // TODO Figure out why the API does this. It's not very nice.
        /*getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, final int position, long arg3) {
                PopupMenu menu = new PopupMenu(getBaseContext(), view);
                menu.inflate(R.menu.message_thread_menu);
                menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        switch (item.getItemId()) {
                        case R.id.delete:
                            new DeleteMessageTask(position).execute();
                            break;
                        }
                        return true;
                    }
                });
                menu.show();
                return true;
            }
        });*/
    }

    /**
     * This method will be called when an item in the list is selected.
     * 
     * @param l The ListView where the click happened.
     * @param v The view that was clicked within the ListView.
     * @param position: The position of the view in the list.
     * @param id: The row id of the item that was clicked.
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (hasMessages) {
            Intent intent = (new Router(this)).getMessageViewSingleIntent(mMessage, mMessageThreadList.get(position));
            startActivity(intent);
        }
    }

    /**
     * An async task containing the REST calls needed to populate the messages
     * list.
     */
    public class PutAndGetMessagesTask extends AsyncTask<String, Void, String> {

        /**
         * Performs a calculation on the background thread. Since we have accessed this thread, we
         * need to mark it as "read".
         * 
         * @param threadID The ID of the current thread.
         * @return The result of getting the thread's messages from the API.
         */
        @Override 
        protected String doInBackground(String... threadID) {

            mThreadId = Integer.parseInt(threadID[0]);
            RestApiV1.putMessage(mThreadId, 0, "thread");

            mJsonResult = RestApiV1.getUserMessagesByThreadId(threadID[0]);

            return mJsonResult;
        }

        /**
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         * 
         * @param jsonResult: Result of fetching the thread's messages.
         */
        protected void onPostExecute(String jsonResult) {

            HashMap<String, String> map;
            MessageThreadMap messages = new MessageThreadMap(jsonResult);
            mMessageThreadList = messages.getMessageThreadData();

            mActionBar.setSubtitle("Thread started by " + mMessageThreadList.get(0).getSenderName());

            // Sort the messages by time. 
            Collections.sort(mMessageThreadList, new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    MessageThreadData m1 = (MessageThreadData) o1;
                    MessageThreadData m2 = (MessageThreadData) o2;
                    return m2.getTimestampString().compareToIgnoreCase(m1.getTimestampString());
                }
            });

            // Populate the list items with message data.
            if (!mMessageThreadList.isEmpty()) {
                hasMessages = true;
                for (int i = 0; i < mMessageThreadList.size(); i++) {
                    map = new HashMap<String, String>();
                    map.put("line1", mMessageThreadList.get(i).getMessageBody());
                    map.put("line2", "From: " + mMessageThreadList.get(i).getSenderName() + " - " + mMessageThreadList.get(i).getDateString());
                    mInfoList.add(map);
                }
            } else {
                hasMessages = false;

                map = new HashMap<String, String>();
                map.put("line1", "You have no new messages.");
                map.put("line2", "");
                mInfoList.add(map);
            }

            String[] from = { "line1", "line2" };

            int[] to = { android.R.id.text1, android.R.id.text2 };

            mAdapter = new SimpleAdapter(MessageThread.this, mInfoList, android.R.layout.simple_list_item_2, from, to);
            setListAdapter(mAdapter);
            mLoadingIndicator.setVisibility(View.GONE);
        }
    }

    /**
     * Deletes a message from the thread.
     * 
     * TODO Currently there is an issue in the API where individual message deletion has some
     * unwanted effects, these need to be fixed before single message deletion can be implemented.
     */
    public class DeleteMessageTask extends AsyncTask<Void, Void, String> {
        private int position;

        public DeleteMessageTask(int i) {
            setProgressBarIndeterminateVisibility(false);
            position = i;
        }

        @Override
        protected String doInBackground(Void... params) {
            return RestApiV1.deleteMessage(mMessageThreadList.get(position).getId(), "msg");
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("done")) {
                mMessageThreadList.remove(position);
                mInfoList.remove(position);
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getBaseContext(), "There was an error deleting the message.\n" + result, Toast.LENGTH_LONG).show();
            }
            setProgressBarIndeterminateVisibility(false);
        }
    }
}