src/com/allplayers/objects/MessageData.javapackage com.allplayers.objects;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("serial")
public class MessageThreadData extends DataObject {
    
    private Date updatedDate = null;

    private boolean variablesUpdated = false;
    private String body = "";
    private String is_new = "";
    private String mid = "";
    private String sender = "";
    private String sender_uuid = "";
    private String subject = "";
    private String timestamp;
    private String uri = "";

    /**
     * Default constructor.
     */
    public MessageThreadData() {

    }

    /**
     * Called to make sure that all of the variables are up to date.
     */
    private void updateVariables() {

        timestamp += "000"; //Converts to milliseconds.
        updatedDate = parseTimestamp(timestamp);
        timestamp = Long.toString(updatedDate.getTime()); //update the string in case someone uses it
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
        return timestamp;
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
     * Returns the message thread's id.
     * 
     * @return The message thread's id.
     */
    public String getMId() {
        return mid;
    }

    /**
     * Returns the message thread's id (needed for backward compatability).
     * 
     * @return The message thread's id.
     */
    @Override
    public String getId() {
        return mid;
    }

    /**
     * Returns the body of the message thread.
     *  
     * @return The body of the message thread.
     */
    public String getMessageBody() {
        return body;
    }

    /**
     * Returns the name of the sender of the message.
     * 
     * @return The name of the sender of the message.
     */
    public String getSenderName() {
        return sender;
    }

    /**
     * Returns the id of the sender of the message.
     * 
     * @return The id of the sender of the message.
     */
    public String getSenderId() {
        return sender_uuid;
    }

    /**
     * Returns the subject of the message thread.
     * 
     * @return The subject of the message thread.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Returns whether or not the message thread is new.
     * 
     * @return Whether or not the message thread is new.
     */
    public String getNew() {
        return is_new;
    }

    /**
     * Returns the URI of the message thread.
     * 
     * @return The URI of the message thread.
     */
    public String getURI() {
        return uri;
    }
}