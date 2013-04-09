package com.dary.xmpp.cmd;

import java.io.DataInputStream;

import org.jivesoftware.smack.Chat;

public class CmdCmd extends CmdBase {
	public static void Cmd(Chat chat, String message) {
		// 判断参数是否为空.
		if (!hasArgs(message)) {
			sendMessageAndUpdateView(chat, "Command is empty");
		} else {
			Process process = null;
			try {
				// ִ执行命令.
				process = Runtime.getRuntime().exec(getArgsCaseSensitive(message));
				// process = Runtime.getRuntime(). exec(new
				// String[]
				// {"/system/bin/sh", "-c",
				// MsgListener.getArgs(message)});
				// 生成结果.
				StringBuffer output = new StringBuffer();
				DataInputStream stdout = new DataInputStream(process.getInputStream());
				new DataInputStream(process.getInputStream());
				String line;
				while ((line = stdout.readLine()) != null) {
					output.append(line).append('\n');
				}
				process.waitFor();
				// 删除最后一个换行符.
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
