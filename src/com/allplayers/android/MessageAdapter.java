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

public class MessageAdapter extends BaseAdapter {
    private Context mContext;

    private List<MessageData> mMessageDataList;

    public MessageAdapter(Context context, List<MessageData> listMessageData) {
        this.mContext = context;
        mMessageDataList = listMessageData;
    }

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

    public int getCount() {
        return mMessageDataList.size();
    }

    public Object getItem(int position) {
        return mMessageDataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
}