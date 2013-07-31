package com.allplayers.objects;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("serial")
public class MessageData extends DataObject implements Comparable<MessageData> {
    
    private Date updatedDate = null;

    private boolean variablesUpdated = false;
    private String is_new = "";
    private String last_message_sender = "";
    private String last_message_body = "";
    private String last_updated = "";
    private String subject = "";
    private String thread_id = "";
    
    /**
     * Default constructor.
     */
    public MessageData() {

    }

    /**
     * Called to make sure that all of the variables are up to date.
     */
    private void updateVariables() {
        last_updated += "000"; //Converts to milliseconds.
        updatedDate = parseTimestamp(last_updated);
        last_updated = Long.toString(updatedDate.getTime()); //update the string in case someone uses it
        variablesUpdated = true;
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
     * Returns the timestamp of the message (in the form of a string).
     * 
     * @return The timestamp of the message.
     */
    public String getTimestampString() {
        if (!variablesUpdated) {
            updateVariables();
        }
        return last_updated;
    }

    /**
     * Returns the message's updatedDate variable.
     * 
     * @return The message's updatedDate variable.
     */
    public Date getDate() {
        if (!variablesUpdated) {
            updateVariables();
        }
        return updatedDate;
    }

    /**
     * Parse the passed Date object into a timestamp.
     * 
     * @param date The passed Date object to be converted.
     * @return The timestamp parsed from the passed Date object.
     */
    public String getDateString() {
        Calendar calendar = Calendar.getInstance();
        if (!variablesUpdated) {
            updateVariables();
        }
        calendar.setTime(updatedDate);

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
     * Returns the thread id of the message.
     * 
     * @return The thread id of the message.
     */
    public String getThreadID() {
        return thread_id;
    }

    /**
     * Returns the thread id of the message (needed for backward compatability).
     * 
     * @return The thread id of the message.
     */
    @Override
    public String getId() {
        return thread_id;
    }

    /**
     * Returns the body of the message.
     *  
     * @return The body of the message.
     */
    public String getMessageBody() {
        return last_message_body;
    }

    /**
     * Returns the last sender of the message.
     * 
     * @return The last sender of the message.
     */
    public String getLastSender() {
        return last_message_sender;
    }

    /**
     * Returns the subject of the message.
     * 
     * @return The subject of the message.
     */
    public String getSubject() {
        return subject;
    }
    
    /**
     * Returns whether or not the message is new.
     * 
     * @return Whether or not the message is new.
     */
    public String getNew() {
        return is_new;
    }

    /**
     * Set the last sender of the message to the passed value.
     * 
     * @param lastSender The message's new last sender.
     */
    public void setLastSender(String lastSender) {
        last_message_sender = lastSender;
    }

    /**
     * Set whether the message has been read or not.
     * 
     * @param isRead Whether the message has been read or not.
     */
    public void setRead(boolean isRead) {
        if (isRead) {
            is_new = "0";
        } else {
            is_new = "1";
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo (MessageData another) {
        if (Integer.parseInt(this.last_updated) < Integer.parseInt(another.last_updated)) {
            return 1;
        } else if (Integer.parseInt(this.last_updated) > Integer.parseInt(another.last_updated)) {
            return -1;
        }
        return 0;
    }
}