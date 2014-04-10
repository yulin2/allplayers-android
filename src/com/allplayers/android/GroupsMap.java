package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.allplayers.objects.GroupData;
import com.google.gson.Gson;

/**
 * Custom ArrayList used to hold GroupData.
 */
public class GroupsMap {
    private ArrayList<GroupData> mGroupsList = new ArrayList<GroupData>();

    /**
     * Constructor.
     * 
     * @param jsonResult Result from API call. Used directly to populate the ArrayList.
     */
    public GroupsMap(String jsonResult) {
        try {
            JSONArray jsonArray = new JSONArray(jsonResult);

            if (jsonArray.length() > 0) {

                // Used to create GroupData objects from json.
                Gson gson = new Gson();
                for (int i = 0; i < jsonResult.length(); i++) {
                    GroupData group = gson.fromJson(jsonArray.getString(i), GroupData.class);
                    if (group.isNew(mGroupsList)) {
                        mGroupsList.add(group);
                    }
                }
            }
        } catch (JSONException ex) {
            System.err.println("GroupsMap/" + ex);
        }
    }
    
    /**
     * Returns the ArrayList. 
     * @return the ArrayList.
     */
    public ArrayList<GroupData> getGroupData() {
        return mGroupsList;
    }

    /**
     * Returns the size of the ArrayList. 
     * @return The size of the ArrayList.
     */
    public int size() {
        return mGroupsList.size();
    }
}