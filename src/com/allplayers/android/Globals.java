package com.allplayers.android;

import com.allplayers.objects.*;



import java.util.ArrayList;

/* This class is used for storing global variables across activities.
 * Also, update timestamps should be kept to avoid making too many unnecessary calls, such as the
 * getUserGroups() call for several activities.
 *
 * Replace global data with database.
 */
public class Globals {
    public static String secretKey;

    public static PhotoData currentPhoto = new PhotoData();
    public static EventData currentEvent = new EventData();
    public static MessageData currentMessage = new MessageData();
    public static MessageThreadData currentMessageThread = new MessageThreadData();

    public static ArrayList<GroupData> groupList = new ArrayList<GroupData>();

    public static String search = "";
    public static int zipcode = 00000;
    public static int distance = 10;

    public static boolean isUnique(DataObject data, ArrayList <? extends DataObject > list) {
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                DataObject object = list.get(i);

                if (object.getId().equals(data.getId())) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void reset() {
        currentPhoto = new PhotoData();
        currentEvent = new EventData();
        currentMessage = new MessageData();

        groupList = new ArrayList<GroupData>();

        search = "";
        zipcode = 00000;
        distance = 10;
    }
}