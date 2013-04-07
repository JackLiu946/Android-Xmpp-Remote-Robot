package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

public class HelpCmd extends CmdBase {
	public static void Help(Chat chat, Message message) {
		if (!hasArgs(message)) {
			helpAll();
		} else {
			help(getArgs(message));
		}
	}

	private static void helpAll() {

	}

	private static void help(String args) {

	}
}
