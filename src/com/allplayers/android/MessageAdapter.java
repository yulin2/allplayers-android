package com.allplayers.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<String>
{
	private final Context context;
	private final String[] senderNames;
	private final String[] dates;
	private final String[] subjects;
	private final String[] msgCounts;

	public MessageAdapter(Context context, String[] senderNames, String[] dates, String[] subjects, String[] msgCounts) 
	{
		super(context, R.layout.custominbox, senderNames);
		this.context = context;
		this.senderNames = senderNames;
		this.dates = dates;
		this.subjects = subjects;
		this.msgCounts = msgCounts;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.custominbox, parent, false);
		
		TextView senderName = (TextView) rowView.findViewById(R.id.senderName);
		TextView date = (TextView) rowView.findViewById(R.id.date);
		TextView subject = (TextView) rowView.findViewById(R.id.subject);
		TextView msgCount = (TextView) rowView.findViewById(R.id.msgCount);

		//textView.setText(values[position]);

		return rowView;
	}
}