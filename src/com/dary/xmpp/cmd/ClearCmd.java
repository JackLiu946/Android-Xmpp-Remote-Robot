package com.dary.xmpp.cmd;

import android.os.Message;

import com.dary.xmpp.ui.MainActivity;

public class ClearCmd extends CmdBase {
	public static void Clear() {
		if (null != MainActivity.MsgHandler) {
			Message msg = new Message();
			msg.what = MainActivity.CLEAR_MSG;
			MainActivity.MsgHandler.sendMessage(msg);
		}
	}
}
