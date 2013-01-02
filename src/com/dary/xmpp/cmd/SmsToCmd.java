package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import com.dary.xmpp.Contact;
import com.dary.xmpp.Tools;

public class SmsToCmd extends SmsCmdBase {
	static String body = null;
	static String contactName = null;
	static Chat chat = null;

	// 目前的Smsto命令只能接收明确的联系人姓名.以后要考虑修改为能接受模糊的联系人信息,和直接为号码的方式.
	public static void Smsto(Chat chat, Message message) {
		// 判断是否含有两个参数
		if (hasSecArgs(message)) {
			SmsToCmd.chat = chat;
			String findStr = getFirArgsCaseSensitive(message);
			body = getSecArgsCaseSensitive(message);
			// 用户输入的完全为数字的情况
			if (Tools.isNumeric(findStr)) {
				sendSMSAndInsertToLibrary(findStr, body);
				// 考虑去动态注册广播接收器来判断短信发送的状态
				sendMessageAndUpdateView(chat, "Send SMS To : " + findStr + " ( Number : " + findStr + " Body : " + body + " )" + " Done");
			}
			// 不全为数字则去查询号码是否为联系人号码
			else {
				// 找到唯一的联系人,此时findStr,即用户输入的参数,即为联系人名字.
				if (Contact.getSingleContactName(findStr, "SmsTo").equals(findStr)) {
					sendSMS(findStr);
				}
				// 找到的联系人为多个,或者为空,则直接发送出消息.
				else {
					sendMessageAndUpdateView(chat, Contact.getSingleContactName(findStr, "SmsTo"));
				}
			}
		} else {
			sendMessageAndUpdateView(chat, "Parameters incomplete");
		}
	}

	// 选择联系人完成.已获得单一的联系人,去发送SMS.
	public static void SelContactDone(String Contact) {
		sendSMS(Contact);
	}

	// 选择号码完成,已获得单一的号码,去真正的发送SMS.
	public static void SelPhoneNumberDone(String addressNumber) {
		sendSMSAndInsertToLibrary(addressNumber, body);

		// 考虑去动态注册广播接收器来判断短信发送的状态
		sendMessageAndUpdateView(chat, "Send SMS To : " + contactName + " ( Number : " + addressNumber + " Body : " + body + " )" + " Done");
	}

	static void sendSMS(String findStr) {
		int NumberOfPhoneNumber = Contact.getContactNumberByName(findStr).size();
		// 如果能找到联系人的号码.
		if (NumberOfPhoneNumber != 0) {
			contactName = findStr;
			// 如果号码不唯一,去创建选择.
			if (NumberOfPhoneNumber != 1) {
				sendMessageAndUpdateView(chat, SelCmd.createChoices(Contact.getContactNumberByName(contactName), "Select PhoneNumbers"));
			}
			// 号码唯一则直接发送
			else {

				String addressNumber = Contact.getContactNumberByName(contactName).get(0).toString();
				// 这里应该再去判断是否含有第2个参数(即短信的内容部分).

				sendSMSAndInsertToLibrary(addressNumber, body);

				// 考虑去动态注册广播接收器来判断短信发送的状态
				sendMessageAndUpdateView(chat, "Send SMS To : " + contactName + " ( Number : " + addressNumber + " Body : " + body + " )" + " Done");
			}
		}
		// 如果找不到则返回消息.
		else {
			sendMessageAndUpdateView(chat, "Contact Has No Number");
		}
	}
}
