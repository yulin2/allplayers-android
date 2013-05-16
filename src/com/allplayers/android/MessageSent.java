package com.allplayers.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.allplayers.android.activities.AllplayersSherlockListActivity;
import com.allplayers.objects.MessageData;
import com.allplayers.rest.RestApiV1;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;

public class MessageSent extends AllplayersSherlockListActivity {

    private ArrayList<MessageData> mMessageList;
    private boolean hasMessages;
    private String mJsonResult = "";
    private ProgressBar mLoadingIndicator;
    ArrayList<HashMap<String, String>> mInfoList = new ArrayList<HashMap<String, String>>(2);

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_sent);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.progress_indicator);

        mActionBar.setTitle("Messages");
        mActionBar.setSubtitle("Sent");

        mSideNavigationView = (SideNavigationView)findViewById(R.id.side_navigation_view);
        mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        mSideNavigationView.setMenuClickCallback(this);
        mSideNavigationView.setMode(Mode.LEFT);

        //check local storage
        if (LocalStorage.getTimeSinceLastModification("Sentbox") / 1000 / 60 < 15) { //more recent than 15 minutes
            mJsonResult = LocalStorage.readSentbox(getBaseContext());
            HashMap<String, String> map;

            MessagesMap messages = new MessagesMap(mJsonResult);
            mMessageList = messages.getMessageData();

            if (!mMessageList.isEmpty()) {
                hasMessages = true;

                for (int i = 0; i < mMessageList.size(); i++) {
                    map = new HashMap<String, String>();
                    map.put("line1", mMessageList.get(i).getSubject());
                    map.put("line2", "Last sent from: " + mMessageList.get(i).getLastSender());
                    mInfoList.add(map);
                }
            } else {
                hasMessages = false;

                map = new HashMap<String, String>();
                map.put("line1", "You have no sent messages.");
                map.put("line2", "");
                mInfoList.add(map);
            }

            String[] from = { "line1", "line2" };

            int[] to = { android.R.id.text1, android.R.id.text2 };

            SimpleAdapter adapter = new SimpleAdapter(this, mInfoList, android.R.layout.simple_list_item_2, from, to);
            setListAdapter(adapter);
        } else {
            new GetUserSentBoxTask().execute();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (hasMessages) {
            // Go to the message thread.
            Intent intent = (new Router(MessageSent.this)).getMessageThreadIntent(mMessageList.get(position));
            startActivity(intent);
        }
    }

    /*
     * Gets a user's sent mail box and populates a hash map with the data.
     */
    public class GetUserSentBoxTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... Args) {
            return RestApiV1.getUserSentBox();
        }

        protected void onPostExecute(String jsonResult) {
            LocalStorage.writeSentbox(getBaseContext(), jsonResult, false);
            HashMap<String, String> map;

            MessagesMap messages = new MessagesMap(jsonResult);
            mMessageList = messages.getMessageData();

            if (!mMessageList.isEmpty()) {
                hasMessages = true;

                for (int i = 0; i < mMessageList.size(); i++) {
                    map = new HashMap<String, String>();
                    map.put("line1", mMessageList.get(i).getSubject());
                    map.put("line2", "Last sent from: " + mMessageList.get(i).getLastSender());
                    mInfoList.add(map);
                }
            } else {
                hasMessages = false;

                map = new HashMap<String, String>();
                map.put("line1", "You have no sent messages.");
                map.put("line2", "");
                mInfoList.add(map);
            }

            String[] from = { "line1", "line2" };

            int[] to = { android.R.id.text1, android.R.id.text2 };

            SimpleAdapter adapter = new SimpleAdapter(MessageSent.this, mInfoList, android.R.layout.simple_list_item_2, from, to);
            setListAdapter(adapter);
            mLoadingIndicator.setVisibility(View.GONE);
        }
    }
}