package com.dary.xmpp.cmd;

import java.util.ArrayList;

import org.jivesoftware.smack.Chat;

import com.dary.xmpp.Tools;

public class SelCmd extends CmdBase {
	private static ArrayList<String> choices = null;
	private static String from = null;

	public static void Sel(Chat chat, String message) {
		// 将Sel命令的String参数专为int类型
		if (hasArgs(message)) {
			int sel = Integer.parseInt(getArgs(message));
			// return choicesALL.get(sel-1).toString();
			selDone(choices.get(sel - 1).toString());
		} else {
			sendMessageAndUpdateView(chat, "Parameters incomplete");
		}
	}

	// 创建选择
	public static String createChoices(ArrayList<String> choices, String from) {
		// 为static的变量赋值.
		SelCmd.choices = choices;
		SelCmd.from = from;

		// 将选项解析出来.
		int numberOfChoices = choices.size();
		StringBuilder choicesSB = new StringBuilder();
		// 这里根据From的不同,可以使用不同的提示语句.
		choicesSB.append("From : " + from + "\n" + "Just Make A Choices :" + "\n");
		for (int i = 0; i < numberOfChoices; i++) {
			choicesSB.append(i + 1 + " " + choices.get(i).toString() + "\n");
		}
		// 去除最后一个换行符
		return Tools.delLastLine(choicesSB);
	}

	// 当选择完成时候,根据From(选择创建的来源)的不同,去执行不同的方法.
	static void selDone(String selection) {
		if (from.equals("Select PhoneNumbers")) {
			SmsToCmd.SelPhoneNumberDone(selection);
		}
		if (from.equals("SmsTo Select Contacts")) {
			SmsToCmd.SelContactDone(selection);
		}
		if (from.equals("Contact Info Select Contacts")) {
			InfoCmd.SelDone(selection);
		}
	}
}
