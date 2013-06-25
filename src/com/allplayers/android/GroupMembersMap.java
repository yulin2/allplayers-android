package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.allplayers.objects.GroupMemberData;
import com.google.gson.Gson;

/**
 * Custom ArrayList used to hold GroupMemberData.
 */
public class GroupMembersMap {
    private ArrayList<GroupMemberData> mMembersList = new ArrayList<GroupMemberData>();

    /**
     * Constructor. 
     * 
     * @param jsonResult Result from API call. Used directly to populate the ArrayList.
     */
    public GroupMembersMap(String jsonResult) {
        
        try {
            JSONArray jsonArray = new JSONArray(jsonResult);
            if (jsonArray.length() > 0) {
                Gson gson = new Gson();
                for (int i = 0; i < jsonArray.length(); i++) {
                    GroupMemberData member = gson.fromJson(jsonArray.getString(i), GroupMemberData.class);
                    Log.d("Putting Data in Map", jsonArray.getString(i));
                    if (member.isNew(mMembersList)) {
                        mMembersList.add(member);
                    }
                }
            }
        } catch (JSONException ex) {
            System.err.println("GroupMembersMap/" + ex);
        }
    }

    /**
     * Returns the array list.
     * 
     * @return the array list.
     */
    public ArrayList<GroupMemberData> getGroupMemberData() {
        return mMembersList;
    }

    /**
     * Returns the size of the array list.
     * 
     * @return the size of the array list.
     */
    public int size() {
        return mMembersList.size();
    }
}