package com.allplayers.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.TimeZone;

public class AlbumData extends DataObject {
    private String uuid = "";
    private String title = "";
    private String description = "";
    private int photoCount = 0;
    private Date modifiedDate = null;
    private String coverPhoto = "";

    public AlbumData() {

    }

    public AlbumData(String jsonResult) {
        JSONObject albumObject = null;

        try {
            albumObject = new JSONObject(jsonResult);
        } catch (JSONException ex) {
            System.err.println("AlbumData/albumObject/" + ex);
        }

        try {
            uuid = albumObject.getString("uuid");
        } catch (JSONException ex) {
            System.err.println("AlbumData/uuid/" + ex);
        }

        try {
            title = albumObject.getString("title");
        } catch (JSONException ex) {
            System.err.println("AlbumData/title/" + ex);
        }

        try {
            description = albumObject.getString("description");
        } catch (JSONException ex) {
            System.err.println("AlbumData/description/" + ex);
        }

        try {
            photoCount = albumObject.getInt("photo_count");
        } catch (JSONException ex) {
            System.err.println("AlbumData/photoCount/" + ex);
        }

        try {
            modifiedDate = parseTimestamp(albumObject.getString("modified_date") + "000"); //to change seconds to milliseconds
        } catch (JSONException ex) {
            System.err.println("AlbumData/modifiedDate/" + ex);
        }

        try {
            coverPhoto = albumObject.getString("rep_photo");
        } catch (JSONException ex) {
            System.err.println("AlbumData/coverPhoto/" + ex);
        }
    }

    private Date parseTimestamp(String timestamp) {
        Date date = new Date(Long.parseLong(timestamp));

        TimeZone timezone = TimeZone.getDefault();
        int offset = timezone.getOffset(date.getTime());
        date = new Date(date.getTime() + offset);
        return date;
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

    public int getPhotoCount() {
        return photoCount;
    }

    public Date getModifedDate() {
        return modifiedDate;
    }

    public String getModifiedDateString() {
        return modifiedDate.toString();
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }
}