package com.allplayers.android;

import org.json.JSONException;
import org.json.JSONObject;

import com.allplayers.objects.MessageThreadData;

import java.util.ArrayList;
import java.util.Iterator;

public class MessageThreadMap {
    private ArrayList<MessageThreadData> mail = new ArrayList<MessageThreadData>();
    private String[] names;

    public MessageThreadMap(String jsonResult) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResult);

            names = getNames(jsonObject);

            if (names.length > 0) {
                for (int i = 0; i < names.length; i++) {
                    MessageThreadData message = new MessageThreadData(jsonObject.getString(names[i]));

                    if (Globals.isUnique(message, mail)) {
                        mail.add(message);
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
        return mail;
    }
}