package com.dary.xmpp;

import org.jivesoftware.smack.Chat;

import android.os.Bundle;

public class SendMessageAndUpdateView {
	public static void sendMessageAndUpdateView(Chat chat, String message) {
		try {
			chat.sendMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null != XmppActivity.MsgHandler) {
			android.os.Message msg = new android.os.Message();
			msg.what = XmppActivity.SEND_MESSAGE;
			Bundle bundle = new Bundle();
			bundle.putString("msg", message);
			msg.setData(bundle);
			XmppActivity.MsgHandler.sendMessage(msg);
			System.out.println("发出去的消息: " + message);
		}
	}
}
