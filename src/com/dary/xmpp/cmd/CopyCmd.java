package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;

import android.content.Context;
import android.text.ClipboardManager;

import com.dary.xmpp.MyApp;

public class CopyCmd extends CmdBase {
	public static void Copy(Chat chat, String message) {

		ClipboardManager cliManager = (ClipboardManager) MyApp.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		// 不带参数,将手机上剪贴板内容返回去
		if (!hasArgs(message)) {
			if (cliManager.getText().length() > 0) {
				sendMessageAndUpdateView(chat, "Phone's Clipboard : " + cliManager.getText());
			}
			// 如果剪贴板返回Null,说明内容为空,或者不是文本
			else {
				sendMessageAndUpdateView(chat, "Phone's Clipboard is empty or not text");
			}

		}
		// 如果带参数,将参数内容复制到剪贴板
		else {
			cliManager.setText(getArgsCaseSensitive(message));
			sendMessageAndUpdateView(chat, "Copy \"" + getArgsCaseSensitive(message) + "\" Done");
		}
	}
}
