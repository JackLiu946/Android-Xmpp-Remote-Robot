package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.os.Bundle;

import com.dary.xmpp.XmppActivity;

public class CmdBase {
	public static void sendMessageAndUpdateView(Chat chat, String message) {
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}

		if (null != XmppActivity.MsgHandler) {
			android.os.Message msg = new android.os.Message();
			msg.what = XmppActivity.SEND_MESSAGE;
			Bundle bundle = new Bundle();
			bundle.putString("msg", message);
			msg.setData(bundle);
			XmppActivity.MsgHandler.sendMessage(msg);
			System.out.println("Send Message: " + message);
		}
	}

	// 解析消息的全部参数部分,区分大小写.
	static String getArgsCaseSensitive(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1);
		return message.getBody().split(":", 2)[1];
	}

	// 解析消息的全部参数部分,不区分大小写.
	static String getArgs(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1).toLowerCase();
		return message.getBody().split(":", 2)[1].toLowerCase();
	}

	// 解析消息的第一个参数部分,不区分大小写.
	static String getFirArgs(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1, message.getBody().indexOf(":", message.getBody().indexOf(":") +
		// 1))
		// .toLowerCase();
		return message.getBody().split(":", -1)[1].toLowerCase();
	}

	// 解析消息的第一个参数部分,区分大小写.
	static String getFirArgsCaseSensitive(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1, message.getBody().indexOf(":", message.getBody().indexOf(":") +
		// 1));
		return message.getBody().split(":", -1)[1];
	}

	// 解析消息的第二个参数部分,不区分大小写.
	static String getSecArgs(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":",
		// message.getBody().indexOf(":") + 1) + 1).toLowerCase();
		return message.getBody().split(":", -1)[2].toLowerCase();
	}

	// 解析消息的第二个参数部分,区分大小写.
	static String getSecArgsCaseSensitive(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":",
		// message.getBody().indexOf(":") + 1) + 1);
		return message.getBody().split(":", -1)[2];
	}

	// 判断是否含有参数.(不去判断是否为空),也可去分割消息,判断所得数组的长度.
	static boolean hasArgs(Message message) {
		// return message.getBody().indexOf(":") != -1;
		return message.getBody().matches(".*:.*");
	}

	// 判断是否含有第2个参数.(不判断是否为空),也可去分割消息,判断所得数组的长度.
	static boolean hasSecArgs(Message message) {
		// return
		// message.getBody().indexOf(":",message.getBody().indexOf(":")+1) !=
		// -1;
		return message.getBody().matches(".*:.*:.*");
	}
}
