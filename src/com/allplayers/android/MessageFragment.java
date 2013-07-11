package com.allplayers.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.allplayers.objects.MessageData;
import com.allplayers.rest.RestApiV1;

/**
 * Fragment displaying links to the user's inbox, sentbox, and message composition activities..
 */
public class MessageFragment extends ListFragment {
    
    private Activity parentActivity;
    private ArrayList<HashMap<String, String>> mChoiceList = new ArrayList<HashMap<String, String>>(2);
    private ArrayList<MessageData> mMessageList;
    
    private int mNumUnread = 0; 
    private String mJsonResult = "";
   
    /**
     * Called to do initial creation of a fragment. This is called after onAttach(Activity) and
     * before onCreateView(LayoutInflater, ViewGroup, Bundle).
     * 
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = this.getActivity();

        // Gets the user's inbox. Needed to display the number of messages the user has in their
        // inbox.
        new GetUserInboxTask().execute();
    }

    /**
     * This method will be called when an item in the list is selected.
     * 
     * @param l The ListView where the click happened.
     * @param v The view that was clicked within the ListView.
     * @param position The position of the view in the list.
     * @param id The row id of the item that was clicked.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // Go to the inbox.
        if (position == 0) {

            Bundle bundle = new Bundle();
            bundle.putString("inboxJSON", mJsonResult);

            Intent intent = new Intent(parentActivity, MessageInbox.class);
            intent.putExtras(bundle);
            startActivity(intent);
            
        // Go to the sentbox.
        } else if (position == 1) {

            Intent intent = new Intent(parentActivity, MessageSent.class);
            startActivity(intent);
            
        // Go to the message creation interface.
        } else if (position == 2) {

            Intent intent = new Intent(parentActivity, SelectMessageContacts.class);
            startActivity(intent);       
        }
    }

    /**
     * Uses the API call result passed to populate the inbox of the user with the messages.
     */
    protected void populateInbox() {
        MessagesMap messages = new MessagesMap(mJsonResult);
        mMessageList = messages.getMessageData();
        HashMap<String, String> map;

        if (!mMessageList.isEmpty()) {
            for (int i = 0; i < mMessageList.size(); i++) {
                if (Integer.parseInt(mMessageList.get(i).getNew()) > 0) {
                    mNumUnread++;
                }
            }
        }

        map = new HashMap<String, String>();
        map.put("line1", "Inbox");
        map.put("line2", mNumUnread + " Unread");
        mChoiceList.add(map);

        map = new HashMap<String, String>();
        map.put("line1", "Sent");
        map.put("line2", "");
        mChoiceList.add(map);

        map = new HashMap<String, String>();
        map.put("line1", "Compose");
        map.put("line2", "");
        mChoiceList.add(map);

        String[] from = { "line1", "line2" };

        int[] to = { android.R.id.text1, android.R.id.text2 };

        SimpleAdapter adapter = new SimpleAdapter(parentActivity, mChoiceList, android.R.layout.simple_list_item_2, from, to);
        setListAdapter(adapter);
    }

    /**
     * Fetches the user's inbox from the API.
     */
    public class GetUserInboxTask extends AsyncTask<Void, Void, Void> {
        
        /**
         * Performs a calculation on a background thread. Used to fetch the user's inbox from the
         * API.
         */
        @Override
        protected Void doInBackground(Void... args) {
            mJsonResult = RestApiV1.getUserInbox(0,0);
            return null;
        }

        /**
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         */
        @Override
        protected void onPostExecute(Void voids) {
            populateInbox();
        }
    }

}
