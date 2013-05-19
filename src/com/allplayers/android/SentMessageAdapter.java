package com.allplayers.android;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allplayers.objects.MessageData;

public class SentMessageAdapter extends BaseAdapter {
    private Context context;

    private List<MessageData> messageDataList;

    public SentMessageAdapter(Context context, List<MessageData> listMessageData) {
        this.context = context;
        messageDataList = listMessageData;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        MessageData entry = messageDataList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                                      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sentboxrow, null);
        }
        
        TextView subject = (TextView) convertView.findViewById(R.id.subject);
        subject.setText(entry.getSubject());
        
        TextView senderName = (TextView) convertView.findViewById(R.id.last_sender);
        senderName.setText("Last sent from " + entry.getLastSender());

        return convertView;
    }

    public int getCount() {
        return messageDataList.size();
    }

    public Object getItem(int position) {
        return messageDataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
}