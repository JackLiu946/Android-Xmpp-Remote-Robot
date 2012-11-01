package com.dary.xmpp.cmd;

import java.io.DataInputStream;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

public class CmdCmd extends CmdBase {
	public static void Cmd(Chat chat, Message message) {
		// ��Ҫ���ж���������Ƿ�Ϊ��.
		if (!hasArgs(message)) {
			sendMessageAndUpdateView(chat, "Command is empty");
		} else {
			Process process = null;
			try {
				// ִ������
				process = Runtime.getRuntime().exec(getArgsCaseSensitive(message));
				// process = Runtime.getRuntime(). exec(new
				// String[]
				// {"/system/bin/sh", "-c",
				// MsgListener.getArgs(message)});
				// ��ȡ���
				StringBuffer output = new StringBuffer();
				DataInputStream stdout = new DataInputStream(process.getInputStream());
				new DataInputStream(process.getInputStream());
				String line;
				while ((line = stdout.readLine()) != null) {
					output.append(line).append('\n');
				}
				process.waitFor();
				// ɾ�����һ�����з�
				output.delete(output.toString().length() - 1, output.toString().length());
				// chat.sendMessage(MsgListener.getArgs(message)
				// + "\n"
				// + output.toString());
				sendMessageAndUpdateView(chat, getArgsCaseSensitive(message) + "\n\n" + output.toString());

			} catch (Exception e) {

				sendMessageAndUpdateView(chat, "Insufficient permissions Or System error");

			} finally {
				try {
					process.destroy();
				} catch (Exception e) {

					sendMessageAndUpdateView(chat, "Insufficient permissions Or System error");

				}
			}
		}
	}
}
