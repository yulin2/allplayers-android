package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.allplayers.objects.GroupData;
import com.google.gson.Gson;

public class GroupsMap {
    private ArrayList<GroupData> mGroupsList = new ArrayList<GroupData>();

    public GroupsMap(String jsonResult) {
        try {
            JSONArray jsonArray = new JSONArray(jsonResult);

            if (jsonArray.length() > 0) {

                // Used to create GroupData objects from json.
                Gson gson = new Gson();
                for (int i = 0; i < jsonResult.length(); i++) {
                    GroupData group = gson.fromJson(jsonArray.getString(i), GroupData.class);
                    try {
                        // Used to set the groups lat and long because gson will not.
                        JSONObject groupInfo = new JSONObject(jsonArray.getString(i));
                        group.setZip(groupInfo.getJSONObject("location").getString("zip"));
                        group.setLatLon(groupInfo.getJSONObject("location").getString("latitude"), groupInfo.getJSONObject("location").getString("longitude"));
                    } catch (JSONException ex) {
                        // If the group does not have a location, ignore this exception.
                    }
                    //if (group.isNew(mGroupsList)) {
                    mGroupsList.add(group);
                    //}
                }
            }
        } catch (JSONException ex) {
            System.err.println("GroupsMap/" + ex);
        }
    }

    public ArrayList<GroupData> getGroupData() {
        return mGroupsList;
    }
}