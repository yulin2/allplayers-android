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

    public PhotoData(String jsonResult) {
        JSONObject photoObject = null;

        try {
            photoObject = new JSONObject(jsonResult);
        } catch (JSONException ex) {
            System.err.println("PhotoData/photoObject/" + ex);
        }

        try {
            uuid = photoObject.getString("uuid");
        } catch (JSONException ex) {
            System.err.println("PhotoData/uuid/" + ex);
        }

        try {
            group_uuid = photoObject.getString("group_uuid");
        } catch (JSONException ex) {
            System.err.println("PhotoData/groupUUID/" + ex);
        }

        try {
            title = photoObject.getString("title");
        } catch (JSONException ex) {
            System.err.println("PhotoData/title/" + ex);
        }

        try {
            description = photoObject.getString("description");
        } catch (JSONException ex) {
            System.err.println("PhotoData/description/" + ex);
        }

        try {
            photo_full = photoObject.getString("photo_full");
        } catch (JSONException ex) {
            System.err.println("PhotoData/photoFull/" + ex);
        }

        try {
            photo_thumb = photoObject.getString("photo_thumb");
        } catch (JSONException ex) {
            System.err.println("PhotoData/photoThumb/" + ex);
        }

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