package com.allplayers.objects;

@SuppressWarnings("serial")
public class GroupMemberData extends DataObject {
    
    private String fname = "";
    private String lname = "";
    private String name = "";
    private String picture = "";
    private String uuid = "";

    /**
     * Default constructor.
     */
    public GroupMemberData() {

    }

    /**
     * Returns the UUID of the group member.
     * 
     * @return The UUID of the group member.
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Returns the UUID of the group member (needed for backward compatability).
     * 
     * @return The UUID of the group member.
     */
    @Override
    public String getId() {
        return uuid;
    }

    /**
     * Returns the name of the group member (fname + " " + lname).
     * 
     * @return The name of the group member.
     */
    public String getName() {
        return fname + " " + lname;
    }

    /**
     * Sets the name of the group member to the passed value.
     * 
     * @param s The new name of the group member.
     */
    public void setName(String s) {
        name = s;
    }

    /**
     * Returns the picture of the group member.
     * 
     * @return The picture of the group member.
     */
    public String getPicture() {
        return picture;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        if (getName().equals(" ")) {
            return name;
        }
        return getName();
    }
}