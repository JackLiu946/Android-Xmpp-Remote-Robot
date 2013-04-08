package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;

public class HelpCmd extends CmdBase {
	public static void Help(Chat chat, String message) {
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