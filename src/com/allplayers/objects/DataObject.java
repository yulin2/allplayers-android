package com.allplayers.objects;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class DataObject implements Serializable {
    public abstract String getId();

    /**
     * Check if this DataObject exists in an ArrayList list.
     *
     * @param list to look through.
     * @return if this is new to list.
     */
    public boolean isNew(ArrayList <? extends DataObject > list) {
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                DataObject object = list.get(i);

                if (object.getId().equals(this.getId())) {
                    return false;
                }
            }
        }

        return true;
    }
}
