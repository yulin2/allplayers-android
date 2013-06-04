package com.allplayers.android;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.allplayers.objects.MessageThreadData;
import com.google.gson.Gson;

public class MessageThreadMap {
    private ArrayList<MessageThreadData> mMessageList = new ArrayList<MessageThreadData>();
    private String[] mNames;

    public MessageThreadMap(String jsonResult) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResult);

            mNames = getNames(jsonObject);

            // Used to create MessageThreadData objects from json.
            Gson gson = new Gson();
            if (mNames.length > 0) {
                for (int i = 0; i < mNames.length; i++) {
                    MessageThreadData message = gson.fromJson(jsonObject.getString(mNames[i]), MessageThreadData.class);

                    if (message.isNew(mMessageList)) {
                        mMessageList.add(message);
                    }
                }
            }
        } catch (JSONException ex) {
            System.err.println("MessageThreadMap/" + ex);
        }
    }

    private static String[] getNames(JSONObject jo) {
        int length = jo.length();

        if (length == 0) {
            return null;
        }

        Iterator<?> iterator = jo.keys();
        String[] names = new String[length];
        int i = 0;

        while (iterator.hasNext()) {
            names[i] = (String)iterator.next();
            i += 1;
        }
        return names;
    }

    public ArrayList<MessageThreadData> getMessageThreadData() {
        return mMessageList;
    }
}