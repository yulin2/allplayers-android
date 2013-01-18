package com.allplayers.android;

import com.allplayers.objects.DataObject;
import com.allplayers.objects.GroupData;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class GroupsMap {
    private ArrayList<GroupData> groups = new ArrayList<GroupData>();

    public GroupsMap(String jsonResult) {
        try {
            JSONArray jsonArray = new JSONArray(jsonResult);

            if (jsonArray.length() > 0) {
            	
            	// Used to create GroupData objects from json.
            	Gson gson = new Gson();
                for (int i = 0; i < jsonResult.length(); i++) {
                	GroupData group = gson.fromJson(jsonArray.getString(i), GroupData.class);

                    if (group.isNew(groups)) {
                        groups.add(group);
                    }
                }
            }
        } catch (JSONException ex) {
            System.err.println("GroupsMap/" + ex);
        }
    }

    public ArrayList<GroupData> getGroupData() {
        return groups;
    }
}