package com.allplayers.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupData extends DataObject {
    private String uuid = "";
    private String title = "";
    private String description = "";
    private String logo = "";

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
}