package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import com.dary.xmpp.Contact;

public class SmsToCmd extends SmsCmdBase {
	static String body = null;
	static String contactName = null;
	static Chat chat = null;

	// Ŀǰ��Smsto����ֻ�ܽ�����ȷ����ϵ������.�Ժ�Ҫ�����޸�Ϊ�ܽ���ģ������ϵ����Ϣ,��ֱ��Ϊ����ķ�ʽ.
	public static void Smsto(Chat chat, Message message) {
		// ���������������򷵻���Ϣ
		if (hasSecArgs(message)) {
			SmsToCmd.chat = chat;
			String findStr = getFirArgsCaseSensitive(message);
			body = getSecArgsCaseSensitive(message);
			// �ҵ�Ψһ����ϵ��,��ʱfindStr,���û�����Ĳ���,��Ϊ��ϵ������.
			if (Contact.getSingleContactName(findStr, "SmsTo").equals(findStr)) {
				sendSMS(findStr);
			}
			// �ҵ�����ϵ��Ϊ���,����Ϊ��,��ֱ�ӷ��ͳ���Ϣ.
			else {
				sendMessageAndUpdateView(chat, Contact.getSingleContactName(findStr, "SmsTo"));
			}
		} else {
			sendMessageAndUpdateView(chat, "Parameters incomplete");
		}
	}

	// ѡ����ϵ�����.�ѻ�õ�һ����ϵ��,ȥ����SMS.
	public static void SelContactDone(String Contact) {
		sendSMS(Contact);
	}

	// ѡ��������,�ѻ�õ�һ�ĺ���,ȥ�����ķ���SMS.
	public static void SelPhoneNumberDone(String addressNumber) {
		sendSMSAndInsertToLibrary(addressNumber, body);

		// ����ȥ��̬ע��㲥���������ж϶��ŷ��͵�״̬
		sendMessageAndUpdateView(chat, "Send SMS To : " + contactName + " ( Number : " + addressNumber + " Body : " + body + " )" + " Done");
	}

	static void sendSMS(String findStr) {
		int NumberOfPhoneNumber = Contact.getContactNumberByName(findStr).size();
		// ������ҵ���ϵ�˵ĺ���.
		if (NumberOfPhoneNumber != 0) {
			contactName = findStr;
			// ������벻Ψһ,ȥ����ѡ��.
			if (NumberOfPhoneNumber != 1) {
				sendMessageAndUpdateView(chat, SelCmd.createChoices(Contact.getContactNumberByName(contactName), "Select PhoneNumbers"));
			}
			// ����Ψһ��ֱ�ӷ���
			else {

				String addressNumber = Contact.getContactNumberByName(contactName).get(0).toString();
				// ����Ӧ����ȥ�ж��Ƿ��е�2������(�����ŵ����ݲ���).

				sendSMSAndInsertToLibrary(addressNumber, body);

				// ����ȥ��̬ע��㲥���������ж϶��ŷ��͵�״̬
				sendMessageAndUpdateView(chat, "Send SMS To : " + contactName + " ( Number : " + addressNumber + " Body : " + body + " )" + " Done");
			}
		}
		// ����Ҳ����򷵻���Ϣ.
		else {
			sendMessageAndUpdateView(chat, "Contact Has No Number");
		}
	}
}
