package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import com.dary.xmpp.ServiceManager;

public class CopyCmd extends CmdBase {
	public static void Copy(Chat chat, Message message) {

		// ��������,���ֻ��ϼ��������ݷ���ȥ
		if (!hasArgs(message)) {
			if (ServiceManager.cliManager.getText().length() > 0) {
				sendMessageAndUpdateView(chat, "Phone's Clipboard : " + ServiceManager.cliManager.getText());
			}
			// ��������巵��Null,˵������Ϊ��,���߲����ı�
			else {
				sendMessageAndUpdateView(chat, "Phone's Clipboard is empty or not text");
			}

		}
		// ���������,���������ݸ��Ƶ�������
		else {
			ServiceManager.cliManager.setText(getArgsCaseSensitive(message));
			sendMessageAndUpdateView(chat, "Copy \"" + getArgsCaseSensitive(message) + "\" Done");
		}
	}
}
