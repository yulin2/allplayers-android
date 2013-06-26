package com.allplayers.objects;

@SuppressWarnings("serial")
public class GroupData extends DataObject {
    
    private String description = "";
    private String lat = "";
    private String logo = "";
    private String lon = "";
    private String title = "";
    private String uuid = "";
    private String zip = "";

    /**
     * Default constructor.
     */
    public GroupData() {

    }

    /**
     * Returns the group's UUID.
     * 
     * @return The group's UUID.
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Returns the group's UUID (needed for backward compatability).
     * 
     * @return The group's UUID.
     */
    @Override
    public String getId() {
        return uuid;
    }

    /**
     * Returns the group's title.
     *  
     * @return The group's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the group's description.
     * 
     * @return The group's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the group's logo.
     * 
     * @return The group's logo.
     */
    public String getLogo() {
        return logo.replace("imagecache/profile_small/", "");
    }

    /**
     * Returns the group's zip.
     * 
     * @return The group's zip.
     */
    public String getZip() {
        return zip;
    }

    /**
     * Returns the group's latitude.
     * 
     * @return The group's latitude.
     */
    public String getLat() {
        return lat;
    }

    /**
     * Returns the group's longitude.
     * 
     * @return The group's longitude.
     */
    public String getLon() {
        return lon;
    }

    /**
     * Sets the group's zip to the passed value.
     * 
     * @param s The group's new zip value.
     */
    public void setZip(String s) {
        zip = s;
    }

    /**
     * Sets the group's latitude and longitude to the passed values.
     * 
     * @param la The group's new latitude.
     * @param lo The group's new longitude.
     */
    public void setLatLon(String la, String lo) {
        lat = la;
        lon = lo;
    }

    /**
     * Sets the group's title to the passed value.
     * 
     * @param string The group's new title.
     */
    public void setTitle(String string) {
        title = string;
    }
    
    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return title;
    }
}