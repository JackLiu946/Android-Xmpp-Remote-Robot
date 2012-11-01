package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import android.media.AudioManager;

import com.dary.xmpp.ServiceManager;

public class RingerModeCmd extends CmdBase {
	public static void RingerMode(Chat chat, Message message) {
		// 不带参数,返回当前的RingerMode
		if (!hasArgs(message)) {
			int RingerMode = ServiceManager.audManager.getRingerMode();
			String strRingerMode = "";
			switch (RingerMode) {
			case AudioManager.RINGER_MODE_NORMAL:
				strRingerMode = "Normal";
				break;
			case AudioManager.RINGER_MODE_SILENT:
				strRingerMode = "Silent";
				break;
			case AudioManager.RINGER_MODE_VIBRATE:
				strRingerMode = "Vibrate";
				break;
			}
			sendMessageAndUpdateView(chat, "Phone 's RingerMode : " + strRingerMode);
		}
		// 带参数则根据参数来设置RingerMode
		else if (getArgs(message).equals("normal")) {
			ServiceManager.audManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			sendMessageAndUpdateView(chat, "Set RingerMode : Normal Done");
		} else if (getArgs(message).equals("silent")) {
			ServiceManager.audManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			sendMessageAndUpdateView(chat, "Set RingerMode : Silent Done");
		} else if (getArgs(message).equals("vibrate")) {
			ServiceManager.audManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			sendMessageAndUpdateView(chat, "Set RingerMode : Vibrate Done");
		} else {
			sendMessageAndUpdateView(chat, "Parameter Error");
		}
	}
}
