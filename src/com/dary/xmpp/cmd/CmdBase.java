package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.os.Bundle;

import com.dary.xmpp.XmppActivity;

class CmdBase {
	static void sendMessageAndUpdateView(Chat chat, String message) {
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}

		if (null != XmppActivity.MsgHandler) {
			android.os.Message msg = new android.os.Message();
			msg.what = 0x0004;
			Bundle bundle = new Bundle();
			bundle.putString("msg", message);
			msg.setData(bundle);
			XmppActivity.MsgHandler.sendMessage(msg);
			System.out.println("����ȥ����Ϣ: " + message);
		}
	}

	// ������Ϣ��ȫ����������,���ִ�Сд.
	static String getArgsCaseSensitive(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1);
		return message.getBody().split(":", 2)[1];
	}

	// ������Ϣ��ȫ����������,�����ִ�Сд.
	static String getArgs(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1).toLowerCase();
		return message.getBody().split(":", 2)[1].toLowerCase();
	}

	// ������Ϣ�ĵ�һ����������,�����ִ�Сд.
	static String getFirArgs(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1, message.getBody().indexOf(":", message.getBody().indexOf(":") +
		// 1))
		// .toLowerCase();
		return message.getBody().split(":", -1)[1].toLowerCase();
	}

	// ������Ϣ�ĵ�һ����������,���ִ�Сд.
	static String getFirArgsCaseSensitive(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1, message.getBody().indexOf(":", message.getBody().indexOf(":") +
		// 1));
		return message.getBody().split(":", -1)[1];
	}

	// ������Ϣ�ĵڶ�����������,�����ִ�Сд.
	static String getSecArgs(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":",
		// message.getBody().indexOf(":") + 1) + 1).toLowerCase();
		return message.getBody().split(":", -1)[2].toLowerCase();
	}

	// ������Ϣ�ĵڶ�����������,���ִ�Сд.
	static String getSecArgsCaseSensitive(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":",
		// message.getBody().indexOf(":") + 1) + 1);
		return message.getBody().split(":", -1)[2];
	}

	// �ж��Ƿ��в���.(��ȥ�ж��Ƿ�Ϊ��),Ҳ��ȥ�ָ���Ϣ,�ж���������ĳ���.
	static boolean hasArgs(Message message) {
		// return message.getBody().indexOf(":") != -1;
		return message.getBody().matches(".*:.*");
	}

	// �ж��Ƿ��е�2������.(���ж��Ƿ�Ϊ��),Ҳ��ȥ�ָ���Ϣ,�ж���������ĳ���.
	static boolean hasSecArgs(Message message) {
		// return
		// message.getBody().indexOf(":",message.getBody().indexOf(":")+1) !=
		// -1;
		return message.getBody().matches(".*:.*:.*");
	}
}
