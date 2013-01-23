package com.allplayers.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class PhotoData extends DataObject {
    private String uuid = "";
    private String group_uuid = "";
    private String title = "";
    private String description = "";
    private String photo_full = "";
    private String photo_thumb = "";
    private PhotoData previousPhoto = null;
    private PhotoData nextPhoto = null;

    public PhotoData() {

    }

    public String getUUID() {
        return uuid;
    }

    public String getId() {
        return uuid;
    }

    public String getGroupUUID() {
        return group_uuid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoFull() {
        return photo_full;
    }

    public String getPhotoThumb() {
        return photo_thumb;
    }

    //Link Photos
    public void setPreviousPhoto(PhotoData p) {
        previousPhoto = p;
    }

    public void setNextPhoto(PhotoData p) {
        nextPhoto = p;
    }

    public PhotoData previousPhoto() {
        return previousPhoto;
    }

    public PhotoData nextPhoto() {
        return nextPhoto;
    }
}