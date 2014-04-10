package com.allplayers.android;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.allplayers.objects.MessageThreadData;
import com.google.gson.Gson;

/**
 * Custom ArrayList used to hold MessageThreadData.
 */
public class MessageThreadMap {
    private ArrayList<MessageThreadData> mMessageList = new ArrayList<MessageThreadData>();
    private String[] mNames;

    /**
     * Constructor.
     * 
     * @param jsonResult Result from API call. Used directly to populate the ArrayList.
     */
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

    /**
     * Returns all of the name values in the passed in JSONObject.
     * 
     * @param jo The JSONObject to be searched for names to return.
     * @return All of the name values in the passed in JSONObject.
     */
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

    /**
     * Returns the ArrayList of MessageData. 
     * 
     * @return The ArrayList of MessageData.
     */
    public ArrayList<MessageThreadData> getMessageThreadData() {
        return mMessageList;
    }
    
    /**
     * Returns the size of the ArrayList of MessageThreadData.
     * 
     * @return The size of the ArrayList of MessageThreadData.
     */
    public int size() {
        return mMessageList.size();
    }
}