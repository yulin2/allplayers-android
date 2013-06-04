package com.allplayers.objects;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MessageThreadData extends DataObject {
    private String mid = "";
    private String subject = "";
    private String body = "";
    private String timestamp;
    private String is_new = "";
    private String sender = "";
    private String sender_uuid = "";
    private String uri = "";
    private Date updatedDate = null;
    private boolean variablesUpdated = false;

    public MessageThreadData() {

    }

    private void updateVariables() {

        timestamp += "000"; //Converts to milliseconds.
        updatedDate = parseTimestamp(timestamp);
        timestamp = Long.toString(updatedDate.getTime()); //update the string in case someone uses it
        variablesUpdated = true;
    }

    private Date parseTimestamp(String timestamp) {
        Date date = new Date(Long.parseLong(timestamp));

        TimeZone timezone = TimeZone.getDefault();
        int offset = timezone.getOffset(date.getTime());
        date = new Date(date.getTime() + offset);
        return date;
    }

    public String getTimestampString() {
        if (!variablesUpdated) {
            updateVariables();
        }
        return timestamp;
    }

    public Date getDate() {
        if (!variablesUpdated) {
            updateVariables();
        }
        return updatedDate;
    }

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

    public String getMId() {
        return mid;
    }

    public String getId() {
        return mid;
    }

    public String getMessageBody() {
        return body;
    }

    public String getSenderName() {
        return sender;
    }

    public String getSenderId() {
        return sender_uuid;
    }

    public String getSubject() {
        return subject;
    }

    public String getNew() {
        return is_new;
    }

    public String getURI() {
        return uri;
    }
}