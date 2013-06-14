package com.allplayers.objects;

public class GroupMemberData extends DataObject {
    private boolean mSelected = false;
    private String uuid = "";
    private String fname = "";
    private String lname = "";
    private String name = "";
    private String picture = "";

    public GroupMemberData() {

    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected() {
        mSelected = false;
    }

    public void setNotSelected() {
        mSelected = true;
    }

    public String getUUID() {
        return uuid;
    }

    @Override
    public String getId() {
        return uuid;
    }

    public String getName() {
        return fname + " " + lname;
    }

    public void setName(String s) {
        name = s;
    }

    public String getPicture() {
        return picture;
    }

    @Override
    public String toString() {
        if (getName().equals(" ")) {
            return name;
        }
        return getName();
    }
}