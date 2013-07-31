package com.allplayers.android;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allplayers.objects.MessageData;

/**
 * Custom Adapter for MessageData
 */
public class SentMessageAdapter extends BaseAdapter {
    
    private Context context;
    private List<MessageData> messageDataList;

    /**
     * Constructor.
     * 
     * @param context The current context.
     * @param listMessageData A List of MessageData.
     */
    public SentMessageAdapter(Context context, List<MessageData> listMessageData) {
        this.context = context;
        messageDataList = listMessageData;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. 
     * 
     * @param position The position of the item within the adapter's data set of the item whose view
     * we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     */
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        MessageData entry = messageDataList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                                      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sentboxrow, null);
        }

        TextView subject = (TextView) convertView.findViewById(R.id.subject);
        subject.setText("Subject: " + entry.getSubject());
        
        TextView body = (TextView) convertView.findViewById(R.id.body);
        body.setText(entry.getMessageBody());

        TextView senderName = (TextView) convertView.findViewById(R.id.date_updated);
        senderName.setText(entry.getDateString());

        return convertView;
    }

    /**
     * Returns the size of the List of MessageData objects.
     * 
     * @return The size of the List of MessageData objects.
     */
    public int getCount() {
        return messageDataList.size();
    }

    /**
     * /**
     * Returns the MessageData object at the specified position.
     * 
     * @param position The position in the list of the MessageData object to be returned.
     * @return The MessageData object at the specified position.
     */
    public Object getItem(int position) {
        return messageDataList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     * 
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return In our use case, return position. Normally we would return "The id of the item at the
     * specified position" but we don't have anything like that with our implimentation. 
     * 
     * TODO Figure out what this functions true purpose is so we can find something more useful to
     * return.
     */
    @Override
    public long getItemId(int position) {
        
        // We have no use for this but it has to be here. 
        return position;
    }
}