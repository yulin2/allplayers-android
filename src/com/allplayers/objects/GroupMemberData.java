package com.allplayers.objects;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupMemberData extends DataObject {
    private String uuid = "";
    private String fname = "";
    private String lname = "";
    private String name = "";
    private String picture = "";

    public GroupMemberData() {

    }

    public String getUUID() {
        return uuid;
    }

    public String getId() {
        return uuid;
    }

    public String getName() {
        return fname + " " + lname;
    }

    public String getPicture() {
        return picture;
    }

    public ArrayList<String> getSerializedList(ArrayList<GroupMemberData> list) {

        ArrayList<String> tbr = new ArrayList<String>();

        String uuid = "";
        String fname = "";
        String lname = "";
        String name = "";
        String picture = "";

        for (int i = 0; i < list.size(); i++) {

        }
        return null;

    }
}