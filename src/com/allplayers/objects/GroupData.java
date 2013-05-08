package com.allplayers.objects;

public class GroupData extends DataObject {
    private String uuid = "";
    private String title = "";
    private String description = "";
    private String logo = "";
    private String zip = "";
    private String lat = "";
    private String lon = "";

    public GroupData() {

    }

    public String getUUID() {
        return uuid;
    }

    public String getId() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLogo() {
        return logo.replace("imagecache/profile_small/", "");
    }

    public String getZip() {
        return zip;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public void setZip(String s) {
        zip = s;
    }

    public void setLatLon(String la, String lo) {
        lat = la;
        lon = lo;
    }
}