package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.dary.xmpp.IncallService;
import com.dary.xmpp.MainService;
import com.dary.xmpp.MyApp;
import com.dary.xmpp.Tools;
import com.dary.xmpp.XmppActivity;

public class CmdBase {
	public static void sendMessageAndUpdateView(Chat chat, String message) {

		try {
			chat.sendMessage(message);
			if (null != XmppActivity.MsgHandler) {
				android.os.Message msg = new android.os.Message();
				msg.what = XmppActivity.SEND_MESSAGE;
				Bundle bundle = new Bundle();
				bundle.putString("msg", message);
				msg.setData(bundle);
				XmppActivity.MsgHandler.sendMessage(msg);
				System.out.println("Send Message: " + message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Tools.doLog("Send Message Failed");
			Toast.makeText(MyApp.getContext(), "Send Message Failed", Toast.LENGTH_SHORT).show();
			Intent mainserviceIntent = new Intent();
			mainserviceIntent.setClass(MyApp.getContext(), MainService.class);
			MyApp.getContext().stopService(mainserviceIntent);
			Intent incallserviceIntent = new Intent();
			incallserviceIntent.setClass(MyApp.getContext(), IncallService.class);
			MyApp.getContext().stopService(incallserviceIntent);
			MainService.sendMsg(XmppActivity.NOT_LOGGED_IN);
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
