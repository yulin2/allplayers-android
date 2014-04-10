package com.allplayers.objects;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Stores event related data.
 */
@SuppressWarnings("serial")
public class EventData extends DataObject {
    
    private String category = "";
    private String description = "";
    private String end = "";
    private String latitude = "";
    private String longitude = "";
    private String start = "";
    private String title = "";
    private String uuid = "";
    private String zip = "";

    /**
     * Default constructor.
     */
    public EventData() {

    }

    /**
     * Check if this DataObject exists in an ArrayList list.
     *
     * @param list The list to look through.
     * @return Whether or not this object is new to the list its being added to.
     */
    @Override
    public boolean isNew(ArrayList <? extends DataObject > list) {
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                EventData object = (EventData) list.get(i);

                if (uuid.equals(object.getUUID())
                        && title.equals(object.getTitle())
                        && description.equals(object.getDescription())
                        && category.equals(object.getCategory())
                        && start.equals(object.getStart())
                        && end.equals(object.getEnd())
                        && longitude.equals(object.getLongitude())
                        && latitude.equals(object.getLatitude())
                        && zip.equals(object.getZip())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Parse the passed timestamp into a Date object.
     * 
     * @param datetime The passed timestamp to be converted.
     * @return The Date object parsed from the passed timestamp.
     */
    private Date parseDatetime(String datetime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = dateFormat.parse(datetime, new ParsePosition(0));

        TimeZone timezone = TimeZone.getDefault();
        int offset = timezone.getOffset(date.getTime());
        date = new Date(date.getTime() + offset);
        return date;
    }

    /**
     * Parse the passed Date object into a timestamp.
     * 
     * @param date The passed Date object to be converted.
     * @return The timestamp parsed from the passed Date object.
     */
    public String getDateString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; //because calendar uses 0-11 instead of 1-12
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String AmPm = "AM";

        if (hour >= 12) {
            AmPm = "PM";

            if (hour > 12) {
                hour = hour - 12;
            }
        } else if (hour == 0) {
            hour = 12;
        }

        DecimalFormat df = new DecimalFormat("00");

        return "" + df.format(month) + "/" + df.format(day) + "/" + year + " " + df.format(hour) + ":" + df.format(minute) + AmPm;
    }

    /**
     * Set the event's Latitude.
     * 
     * @param newLatitude The new latitude of the event.
     */
    public void setLatitude(String newLatitude) {
        latitude = newLatitude;
    }

    /**
     * Set the event's Longitude.
     * 
     * @param newLongitude The new londitude of the event.
     */
    public void setLongitude(String newLongitude) {
        longitude = newLongitude;
    }

    /**
     * Set the event's zip.
     * 
     * @param s The new zip of the event.
     */
    public void setZip(String s) {
        zip = s;
    }

    /**
     * Returns the event's UUID.
     * 
     * @return The event's UUID.
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
     * Returns the event's title.
     * 
     * @return The event's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the event's description.
     * 
     * @return The event's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the event's category.
     * 
     * @return The event's category.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns the event's start (in the form of a String (timestamp)).
     * 
     * @return The event's start.
     */
    public String getStart() {
        return start;
    }

    /**
     * Returns the event's start time (in the form of a Date object).
     * 
     * @return The event's start time.
     */
    public Date getStartDate() {
        return parseDatetime(start);
    }

    /**
     * Returns the event's start (in the form of a String).
     * 
     * @return The event's start.
     */
    public String getStartDateString() {
        return getDateString(parseDatetime(start));
    }

    /**
     * Returns the event's end (in the form of a String (timestamp)).
     * 
     * @return The event's end.
     */
    public String getEnd() {
        return end;
    }

    /**
     * Returns the event's end time (in the form of a Date object).
     * 
     * @return The event's end time.
     */
    public Date getEndDate() {
        return parseDatetime(end);
    }

    /**
     * Returns the event's end (in the form of a String).
     * 
     * @return The event's end.
     */
    public String getEndDateString() {
        return getDateString(parseDatetime(end));
    }

    /**
     * Returns the event's latitude.
     * 
     * @return The event's latitude.
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * Returns the event's longitude.
     * 
     * @return The event's longitude.
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * Returns the event's zip.
     * @return The event's zip.
     */
    public String getZip() {
        return zip;
    }
}