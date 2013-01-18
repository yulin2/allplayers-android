package com.allplayers.android;

import org.json.JSONArray;
import org.json.JSONException;

import com.allplayers.objects.DataObject;
import com.allplayers.objects.GroupMemberData;
import com.google.gson.Gson;

import java.util.ArrayList;

public class GroupMembersMap {
    private ArrayList<GroupMemberData> members = new ArrayList<GroupMemberData>();

    public GroupMembersMap(String jsonResult) {
        try {
            JSONArray jsonArray = new JSONArray(jsonResult);

            if (jsonArray.length() > 0) {
            	
            	// Used to create GroupMemberData objects from json.
            	Gson gson = new Gson();
                for (int i = 0; i < jsonResult.length(); i++) {
                    //GroupMemberData member = new GroupMemberData(jsonArray.getString(i));
                	GroupMemberData member = gson.fromJson(jsonArray.getString(i), GroupMemberData.class);

                    if (member.isNew(members)) {
                        members.add(member);
                    }
                }
            }
        } catch (JSONException ex) {
            System.err.println("GroupMembersMap/" + ex);
        }
    }

    public ArrayList<GroupMemberData> getGroupMemberData() {
        return members;
    }
}