package com.allplayers.android;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.allplayers.android.activities.AllplayersSherlockActivity;
import com.allplayers.objects.MessageData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

/**
 * User's messages sent mail box.
 */
public class MessageSent extends AllplayersSherlockActivity {

    private ArrayList<MessageData> mMessageList;
    private Button mLoadMoreButton;
    private DeleteMessageTask mDeleteMessageTask;
    private GetUserSentboxTask mGetUserSentboxTask;
    private ListView mListView;
    private ProgressBar mLoadingIndicator;
    private SentMessageAdapter mMessageListAdapter;
    private ViewGroup mFooter;
   
    private final int LIMIT = 15;
    private boolean mEndOfData;
    private int mOffset;
    
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
        setContentView(R.layout.message_sent);

        // Set up the ActionBar.
        mActionBar = getSupportActionBar();
        mActionBar.setIcon(R.drawable.menu_icon);
        mActionBar.setTitle("Messages");
        mActionBar.setSubtitle("Sent");

        // Set up the Side Navigation Menu.
        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        // Load the user's sentbox.
        mGetUserSentboxTask  = new GetUserSentboxTask();
        mGetUserSentboxTask.execute();

        // Variable initialization.
        mMessageList = new ArrayList<MessageData>();
        mListView = (ListView) findViewById(R.id.customListView);
        mMessageListAdapter = new SentMessageAdapter(MessageSent.this, mMessageList);
        mFooter = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.load_more, null);
        mLoadMoreButton = (Button) mFooter.findViewById(R.id.load_more_button);
        mLoadingIndicator = (ProgressBar) mFooter.findViewById(R.id.loading_indicator);

        // Set up the "load more" button.
        mLoadMoreButton.setOnClickListener(new OnClickListener() {
            
            /**
             * Called when a view has been clicked.
             * 
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                mLoadMoreButton.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                mGetUserSentboxTask = new GetUserSentboxTask();
                mGetUserSentboxTask.execute();
            }
        });

        // Set up the list view that will show all of the data.
        mListView.addFooterView(mFooter);
        mListView.setAdapter(mMessageListAdapter);

        // Currently, the API has an issue with DELETE functionality.
//        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            
//            /**
//             * Callback method to be invoked when an item in this view has been clicked and held.
//             * 
//             * @param parent The adapter view where the click happened.
//             * @param view The view within the AdapterView that was clicked.
//             * @param position The position of the view in the adapter.
//             * @param id The row id of the item that was clicked.
//             */
//            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//            @Override
//            public boolean onItemLongClick(AdapterView<?> arg0, View view, final int position, long arg3) {
//                
//             // Check which verion of Android is being run, anything pre-11 cannot display popup menus.
//                if (android.os.Build.VERSION.SDK_INT >= 11) {
//                    PopupMenu menu = new PopupMenu(getBaseContext(), view);
//                    menu.inflate(R.menu.message_thread_menu);
//                    menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//                        
//                        /**
//                         * Called when a menu item has been invoked. This is the first code that is
//                         * executed; if it returns true, no other callbacks will be executed.
//                         */
//                        @Override
//                        public boolean onMenuItemClick(android.view.MenuItem item) {
//                            switch (item.getItemId()) {
//                                case R.id.delete: {
//                                    mDeleteMessageTask = new DeleteMessageTask(position);
//                                    mDeleteMessageTask.execute();
//                                    break;
//                                }
//                            }
//                            return true;
//                        }
//                    });
//                    menu.show();
//                }
//                return true;
//            }
//        });
    }
    
    /**
     * Called when you are no longer visible to the user. You will next receive either onRestart(),
     * onDestroy(), or nothing, depending on later user activity.
     */
    @Override
    public void onStop() {
        super.onStop();
        
        if (mGetUserSentboxTask != null) {
            mGetUserSentboxTask.cancel(true);
        }
        
        if (mDeleteMessageTask != null) {
            mDeleteMessageTask.cancel(true);
        }
    }

    /**
     * Fetches the user's message sentbox asynchronously.
     */
    public class GetUserSentboxTask extends AsyncTask<Void, Void, String> {

        /**
         * Performs a computation on a background thread. Fetches the user's message sentbox.
         * 
         * @return The result of the API call.
         */
        @Override
        protected String doInBackground(Void... Args) {
            return RestApiV1.getUserSentBox(mOffset, LIMIT);
        }

        /**
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         * 
         * @param jsonResult: Result of fetching the user's sentbox.
         */
        @Override
        protected void onPostExecute(String jsonResult) {
            MessagesMap messages = new MessagesMap(jsonResult);

            // Check if there was any data returned. If there wasn't, either all of the data has
            // been fetched already or there wasn't any to fetch in the beginning.
            if (messages.size() == 0) {
                mEndOfData = true;
                mListView.removeFooterView(mFooter);

                // Check if the list of messages is empty. If so, there was never any data to be
                // fetched.
                if (mMessageList.size() == 0) {

                    // We need to display to the user that there aren't any messanges. We do this
                    // by making a blank MessageData object and setting its last_message_sender
                    // field to a notification. We use this field because it is the most prominent.
                    String[] blankMessage = {"No messages to display"};
                    ArrayAdapter<String> blank = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, blankMessage);
                    mListView.setAdapter(blank);
                    mListView.setEnabled(false);
                }
            }

            // If we made it here we know that there was at least part of a set of data fetched.
            else {

                // If the size of the returned data is less than the maximum size we specified, we
                // know that we have reached the end of the data to be fetched.
                if (messages.size() < LIMIT) {
                    mEndOfData = true;
                }

                mMessageList.addAll(messages.getMessageData());
                mMessageListAdapter.notifyDataSetChanged();
                if (!mEndOfData) {
                    mLoadMoreButton.setVisibility(View.VISIBLE);
                    mLoadingIndicator.setVisibility(View.GONE);
                    mOffset += LIMIT;
                } else {
                    mListView.removeFooterView(mFooter);
                }
            }

        }
    }
    
    /**
     * Deletes a message thread from the sentbox.
     * 
     * TODO Currently there is an issue in the API where individual message deletion has some
     * unwanted effects, these need to be fixed before single message deletion can be implemented.
     */
    public class DeleteMessageTask extends AsyncTask<Void, Void, String> {
        private int position;
        
        /**
         * Constructor.
         * 
         * @param i The position of the message to be deleted in the array list of messages.
         */
        public DeleteMessageTask(int i) {
            position = i;
        }

        /**
         * Performs a calculation on the background thread. Deletes a message thread.
         * 
         * @return The result of the API call.
         */
        @Override
        protected String doInBackground(Void... params) {
            return RestApiV1.deleteMessage(mMessageList.get(position).getId(), "msg");
        }

        /**
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         * 
         * @param jsonResult: Result of fetching the user's inbox.
         */
        @Override
        protected void onPostExecute(String result) {
            if (result.equals("done")) {
                mMessageList.remove(position);
                mMessageListAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getBaseContext(), "There was an error deleting the message.\n" + result, Toast.LENGTH_LONG).show();
            }
        }
    }
}