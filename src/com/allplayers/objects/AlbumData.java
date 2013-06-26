package com.allplayers.objects;

import java.util.Date;
import java.util.TimeZone;

/**
 * Stores photo album related data.
 */
@SuppressWarnings("serial")
public class AlbumData extends DataObject {
    
    private int photo_count = 0;
    private String description = "";
    private String modified_date = "";
    private String rep_photo = ""; // Cover Photo.
    private String title = "";
    private String uuid = "";
    
    /**
     * Default constructor.
     */
    public AlbumData() {

    }

    /**
     * Parse the passed timestamp into a Date object.
     * 
     * @param timestamp The passed timestamp to be converted.
     * @return The Date object parsed from the passed timestamp.
     */
    private Date parseTimestamp(String timestamp) {
        Date date = new Date(Long.parseLong(timestamp));
        TimeZone timezone = TimeZone.getDefault();
        int offset = timezone.getOffset(date.getTime());
        date = new Date(date.getTime() + offset);
        return date;
    }

    /**
     * Returns the UUID of the album.
     * 
     * @return The UUID of the album.
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Returns the UUID of the album. (needed for backward compatability).
     * 
     * @return The UUID of the album.
     */
    @Override
    public String getId() {
        return uuid;
    }

    /**
     * Returns the title of the album.
     * 
     * @return The title of the album.
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Returns the description of the album.
     * 
     * @return The desrciption of the album.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the number of photos in the album.
     *  
     * @return The number of photos in the album.
     */
    public int getPhotoCount() {
        return photo_count;
    }

    /**
     * Returns the album's date (in the form of a Date object).
     * 
     * @return The album's date.
     */
    public Date getModifedDate() {
        return parseTimestamp(modified_date + "000"); // "+ "000"" Converts from seconds to milliseconds.
    }

    /**
     * Returns the album's date (in the form of a String).
     * 
     * @return The album's date.
     */
    public String getModifiedDateString() {
        return parseTimestamp(modified_date + "000").toString(); // "+ "000"" Converts from seconds to milliseconds.
    }

    /**
     * Returns the album's cover photo.
     * 
     * @return The album's cover photo.
     */
    public String getCoverPhoto() {
        return rep_photo;
    }

    /**
     * Sets the title of the album.
     * 
     * @param t The new title of the album.
     */
    public void setTitle(String t) {
        title = t;
    }
}