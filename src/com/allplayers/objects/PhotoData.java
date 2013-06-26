package com.allplayers.objects;

@SuppressWarnings("serial")
public class PhotoData extends DataObject {
    
    private PhotoData previousPhoto = null;
    private PhotoData nextPhoto = null;
    
    private String description = "";
    private String group_uuid = "";
    private String photo_full = "";
    private String photo_thumb = "";
    private String title = "";
    private String uuid = "";

    /**
     * Default constructor.
     */
    public PhotoData() {

    }

    /**
     * Returns the UUID of the photo data.
     * 
     * @return The UUID of the photo data.
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Returns the UUID of the photo data (needed for backward compatability). 
     * 
     * @return The UUID of the photo data.
     */
    @Override
    public String getId() {
        return uuid;
    }

    /**
     * Returns the UUID of the group who the photo belongs to.
     * 
     * @return The UUID of the group who the photo belongs to.
     */
    public String getGroupUUID() {
        return group_uuid;
    }

    /**
     * Returns the title of the group.
     * 
     * @return The title of the group.
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Returns the description of the group.
     * 
     * @return The description of the group.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the URL of the full photo.
     * 
     * @return The URL of the full photo.
     */
    public String getPhotoFull() {
        return photo_full;
    }

    /**
     * Returns the URL of the photo thumbnail.
     * 
     * @return the URL of the photo thumbnail.
     */
    public String getPhotoThumb() {
        return photo_thumb;
    }
 
    /**
     * Used to link photos for use in the photo pager. Link this photo the the previous one sent as
     * a parameter.
     * 
     * @param p The previous photo.
     */
    public void setPreviousPhoto(PhotoData p) {
        previousPhoto = p;
    }

    /**
     * Used to link photos for use in the photo pager. Link this photo the the next one sent as
     * a parameter.
     * 
     * @param p The next photo.
     */
    public void setNextPhoto(PhotoData p) {
        nextPhoto = p;
    }

    /**
     * Returns the previous photo.
     * 
     * @return The previous photo.
     */
    public PhotoData previousPhoto() {
        return previousPhoto;
    }

    /**
     * Returns the next photo.
     * 
     * @return The next photo.
     */
    public PhotoData nextPhoto() {
        return nextPhoto;
    }
}