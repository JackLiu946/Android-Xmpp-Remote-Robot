package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;

import com.dary.xmpp.ServiceManager;

public class CopyCmd extends CmdBase {
	public static void Copy(Chat chat, String message) {

		// 不带参数,将手机上剪贴板内容返回去
		if (!hasArgs(message)) {
			if (ServiceManager.cliManager.getText().length() > 0) {
				sendMessageAndUpdateView(chat, "Phone's Clipboard : " + ServiceManager.cliManager.getText());
			}
			// 如果剪贴板返回Null,说明内容为空,或者不是文本
			else {
				sendMessageAndUpdateView(chat, "Phone's Clipboard is empty or not text");
			}

		}
		// 如果带参数,将参数内容复制到剪贴板
		else {
			ServiceManager.cliManager.setText(getArgsCaseSensitive(message));
			sendMessageAndUpdateView(chat, "Copy \"" + getArgsCaseSensitive(message) + "\" Done");
		}
	}
}
