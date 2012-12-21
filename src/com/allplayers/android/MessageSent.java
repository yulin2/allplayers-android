package com.allplayers.android;

import com.allplayers.objects.MessageData;
import com.allplayers.rest.RestApiV1;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageSent extends ListActivity {
    private ArrayList<MessageData> messageList;
    private boolean hasMessages;
    private String jsonResult = "";

    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(2);

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check local storage
        if (LocalStorage.getTimeSinceLastModification("Sentbox") / 1000 / 60 < 15) { //more recent than 15 minutes
            jsonResult = LocalStorage.readSentbox(getBaseContext());
        } else {
            jsonResult = RestApiV1.getUserSentBox();
            LocalStorage.writeSentbox(getBaseContext(), jsonResult, false);
        }

        HashMap<String, String> map;

        MessagesMap messages = new MessagesMap(jsonResult);
        messageList = messages.getMessageData();

        if (!messageList.isEmpty()) {
            hasMessages = true;

            for (int i = 0; i < messageList.size(); i++) {
                map = new HashMap<String, String>();
                map.put("line1", messageList.get(i).getSubject());
                map.put("line2", "Last sent from: " + messageList.get(i).getLastSender());
                list.add(map);
            }
        } else {
            hasMessages = false;

            map = new HashMap<String, String>();
            map.put("line1", "You have no sent messages.");
            map.put("line2", "");
            list.add(map);
        }

        String[] from = { "line1", "line2" };

        int[] to = { android.R.id.text1, android.R.id.text2 };

        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, from, to);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (hasMessages) {
            // Go to the message thread.
            Intent intent = (new Router(MessageSent.this)).getMessageThreadIntent(messageList.get(position));
            startActivity(intent);
        }
    }
}