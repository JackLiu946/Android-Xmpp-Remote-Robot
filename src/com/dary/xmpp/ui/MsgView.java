package com.dary.xmpp.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MsgView extends LinearLayout {

	public static final int RECEIVE = 0;
	public static final int SEND = 1;
	public static final int LOG = 2;

	public MsgView(Context context) {
		super(context);
	}

	public MsgView(Context context, int type, String strFrom, String strTime, String strMsg) {
		super(context);
		TextView from = new TextView(context);
		TextView time = new TextView(context);
		TextView msg = new TextView(context);

		from.setTextColor(Color.WHITE);
		this.setOrientation(LinearLayout.VERTICAL);
		if (type == RECEIVE) {
			msg.setTextColor(Color.YELLOW);
		} else if (type == SEND) {
			msg.setTextColor(Color.GREEN);
		} else if (type == LOG) {
			msg.setTextColor(Color.RED);
		}
		LinearLayout fromAndTime = new LinearLayout(context);
		fromAndTime.setOrientation(LinearLayout.HORIZONTAL);

		from.setText(strFrom + " :");
		time.setText(strTime);
		time.setGravity(Gravity.RIGHT);
		msg.setText(strMsg + "\n");
		fromAndTime.addView(from, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		fromAndTime.addView(time, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		addView(fromAndTime, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		addView(msg, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	}

}
