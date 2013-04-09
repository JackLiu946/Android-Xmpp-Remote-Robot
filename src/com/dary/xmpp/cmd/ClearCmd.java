package com.dary.xmpp.cmd;

import com.dary.xmpp.ui.MainActivity;

public class ClearCmd extends CmdBase {
	public static void Clear() {
		MainActivity.clearMsg();
	}
}
