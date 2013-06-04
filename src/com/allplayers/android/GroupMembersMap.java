package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.allplayers.objects.GroupMemberData;
import com.google.gson.Gson;

public class GroupMembersMap {
    private ArrayList<GroupMemberData> mMembersList = new ArrayList<GroupMemberData>();

    public GroupMembersMap(String jsonResult) {
        try {
            JSONArray jsonArray = new JSONArray(jsonResult);

            if (jsonArray.length() > 0) {

                // Used to create GroupMemberData objects from json.
                Gson gson = new Gson();
                for (int i = 0; i < jsonArray.length(); i++) {
                    GroupMemberData member = gson.fromJson(jsonArray.getString(i), GroupMemberData.class);

                    if (member.isNew(mMembersList)) {
                        mMembersList.add(member);
                    }
                }
            }
        } catch (JSONException ex) {
            System.err.println("GroupMembersMap/" + ex);
        }
    }

    public ArrayList<GroupMemberData> getGroupMemberData() {
        return mMembersList;
    }

    public int size() {
        return mMembersList.size();
    }
}