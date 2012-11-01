package com.dary.xmpp.cmd;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.Message;

import com.dary.xmpp.Tools;

public class SelCmd extends CmdBase {
	private static ArrayList<String> choices = null;
	private static String from = null;

	public static void Sel(Message message) {
		// ��Sel�����String����רΪint����
		int sel = Integer.parseInt(getArgs(message));
		// return choicesALL.get(sel-1).toString();
		selDone(choices.get(sel - 1).toString());
	}

	// ����ѡ��
	public static String createChoices(ArrayList<String> choices, String from) {
		// Ϊstatic�ı�����ֵ.
		SelCmd.choices = choices;
		SelCmd.from = from;

		// ��ѡ���������.
		int numberOfChoices = choices.size();
		StringBuilder choicesSB = new StringBuilder();
		// �������From�Ĳ�ͬ,����ʹ�ò�ͬ����ʾ���.
		choicesSB.append("From : " + from + "\n" + "Just Make A Choices :" + "\n");
		for (int i = 0; i < numberOfChoices; i++) {
			choicesSB.append(i + 1 + " " + choices.get(i).toString() + "\n");
		}
		// ȥ�����һ�����з�
		return Tools.delLastLine(choicesSB);
	}

	// ��ѡ�����ʱ��,����From(ѡ�񴴽�����Դ)�Ĳ�ͬ,ȥִ�в�ͬ�ķ���.
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
