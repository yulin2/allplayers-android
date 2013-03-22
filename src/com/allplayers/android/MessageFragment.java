package com.allplayers.android;

import java.util.ArrayList;
import java.util.HashMap;

import com.allplayers.objects.MessageData;
import com.allplayers.rest.RestApiV1;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MessageFragment extends ListFragment {
    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(2);

    private int numUnread = 0;

    private ArrayList<MessageData> messageList;
    private String jsonResult = "";

    private Activity parentActivity;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = this.getActivity();

        GetUserInboxTask helper = new GetUserInboxTask();
        helper.execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (position == 0) {

            Bundle bundle = new Bundle();
            bundle.putString("inboxJSON", jsonResult);

            Intent intent = new Intent(parentActivity, MessageInbox.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (position == 1) {

            Intent intent = new Intent(parentActivity, MessageSent.class);
            startActivity(intent);
        } else if (position == 2) {

            Intent intent = new Intent(parentActivity, SelectMessageContacts.class);
            startActivity(intent);
        } else if (position == 2) {
            Intent intent = new Intent(parentActivity, SelectMessageContacts.class);
            startActivity(intent);
        }
    }

    /**
     * Uses the json result passed in, and populates the inbox of the
     * user with the messages.
     */
    protected void populateInbox() {
        jsonResult = LocalStorage.readInbox(parentActivity.getBaseContext());
        MessagesMap messages = new MessagesMap(jsonResult);
        messageList = messages.getMessageData();
        HashMap<String, String> map;

        if (!messageList.isEmpty()) {
            for (int i = 0; i < messageList.size(); i++) {
                if (Integer.parseInt(messageList.get(i).getNew()) > 0) {
                    numUnread++;
                }
            }
        }

        map = new HashMap<String, String>();
        map.put("line1", "Inbox");
        map.put("line2", numUnread + " Unread");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("line1", "Sent");
        map.put("line2", "");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("line1", "Compose");
        map.put("line2", "");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("line1", "Compose");
        map.put("line2", "");
        list.add(map);

        String[] from = { "line1", "line2" };

        int[] to = { android.R.id.text1, android.R.id.text2 };

        SimpleAdapter adapter = new SimpleAdapter(parentActivity, list, android.R.layout.simple_list_item_2, from, to);
        setListAdapter(adapter);
    }

    public class GetUserInboxTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... args) {
            return RestApiV1.getUserInbox();
        }

        protected void onPostExecute(String jsonResult) {
            populateInbox();
        }
    }

}
