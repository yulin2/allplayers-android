package com.allplayers.android;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allplayers.objects.MessageData;

/**
 * Custom ArrayAdapter for messages.
 */
public class MessageAdapter extends BaseAdapter {
    
    private Context mContext;
    private List<MessageData> mMessageDataList;

    /**
     * Constructor.
     * @param context The current context.
     * @param listMessageData List of message data.
     */
    public MessageAdapter(Context context, List<MessageData> listMessageData) {
        this.mContext = context;
        mMessageDataList = listMessageData;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. 
     * 
     * @param position The position of the item within the adapter's data set of the item whose view
     * we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        MessageData entry = mMessageDataList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                                      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.inboxrow, null);
        }
        TextView senderName = (TextView) convertView.findViewById(R.id.senderName);
        senderName.setText(entry.getLastSender());

        TextView date = (TextView) convertView.findViewById(R.id.date);
        date.setText(entry.getDateString());

        TextView subject = (TextView) convertView.findViewById(R.id.subject);
        subject.setText(entry.getSubject());

        TextView body = (TextView) convertView.findViewById(R.id.body);
        body.setText(entry.getMessageBody());

        ImageView unreadIcon = (ImageView) convertView.findViewById(R.id.unreadIcon);
        if (Integer.parseInt(entry.getNew()) == 0) {
            unreadIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.placeholder));
        } else {
            unreadIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unread_message_icon));
        }

        return convertView;
    }

    /**
     * Returns the size of the list of MessageData objects.
     * 
     * @return The size of the list of MessageData objects.
     */
    public int getCount() {
        return mMessageDataList.size();
    }

    /**
     * Returns the MessageData object at the specified position.
     * 
     * @param position The position in the list of the MessageData object to be returned.
     * @return The MessageData object at the specified position.
     */
    public Object getItem(int position) {
        return mMessageDataList.get(position);
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