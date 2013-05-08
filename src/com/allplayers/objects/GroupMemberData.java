package com.allplayers.objects;

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
    	name = fname + " " + lname;
        return name;
    }

    public String getPicture() {
        return picture;
    }
}