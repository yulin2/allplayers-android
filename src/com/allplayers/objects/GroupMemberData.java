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
        //To fix names that are all lowercase
        if (fname != null && !fname.equals("null") && !fname.equals("")) {
            name = fname.substring(0, 1).toUpperCase() + fname.substring(1).toLowerCase();
        }
        if (lname != null && !lname.equals("null") && !lname.equals("")) {
            //If a first name has been added already
            if (!name.equals("")) {
                name += " ";
            }
            name += lname.substring(0, 1).toUpperCase() + lname.substring(1).toLowerCase();
        }

        return name;
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