package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.dary.xmpp.Contact;
import com.dary.xmpp.MyApp;
import com.dary.xmpp.Tools;

public class SmsCmd extends SmsCmdBase {

	public static void Sms(Chat chat, Message message) {

		Cursor cur;
		Uri uri;

		// 不带参数,则返回短信记录.
		if (!hasArgs(message)) {
			uri = Uri.parse("content://sms/");
			String[] projection = new String[] { "_id", "address", "body", "date", "type" };
			cur = MyApp.getContext().getContentResolver().query(uri, projection, null, null, null);
			StringBuilder smsBuilder = new StringBuilder();
			int smsNumber = 0;
			if (cur.moveToFirst()) {
				int index_Address = cur.getColumnIndex("address");
				int index_Body = cur.getColumnIndex("body");
				int index_Date = cur.getColumnIndex("date");
				int index_Type = cur.getColumnIndex("type");
				do {
					String strAddress = cur.getString(index_Address);
					String strbody = cur.getString(index_Body);
					long longDate = cur.getLong(index_Date);
					int intType = cur.getInt(index_Type);

					String strDate = Tools.getTimeStr(longDate);

					String strType = "";
					if (intType == 1) {
						strType = "Receive";
					} else if (intType == 2) {
						strType = "Send";
					} else {
						strType = "Null";
					}

					smsBuilder.append("[ ");
					smsBuilder.append(strDate + " , ");
					// smsBuilder.append(intPerson + ", ");
					smsBuilder.append(strType + " , ");
					smsBuilder.append(Contact.getContactNameByNumber(strAddress) + " , ");
					smsBuilder.append(strbody);
					smsBuilder.append(" ]\n\n");
					smsNumber++;
				} while (cur.moveToNext() && smsNumber < 5);
				if (!cur.isClosed()) {
					cur.close();
					cur = null;
				}
			} else {
				smsBuilder.append("No Result!");
			}
			sendMessageAndUpdateView(chat, smsBuilder.toString());

			System.out.println(smsBuilder.toString());
		} else {
			// 带参数就回复最后收到的短信
			// 获取最后(收到的)短信的号码
			uri = Uri.parse("content://sms/inbox");
			// MainService.uri =
			// Uri.parse("content://sms");
			String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
			cur = MyApp.getContext().getContentResolver().query(uri, projection, null, null, null);
			cur.moveToFirst();
			int index_Address = cur.getColumnIndex("address");
			String lastAddress = cur.getString(index_Address);
			System.out.println("LastAddress" + lastAddress);

			// 标记最后收到的短信为已读状态
			ContentValues cvalues = new ContentValues();
			cvalues.put("read", "1");
			MyApp.getContext().getContentResolver().update(uri, cvalues, " address='" + lastAddress + "'", null);

			// MainService.cur.close();
			// MainService.cur = null;

			// 发送短信并插入到短信库中
			sendSMSAndInsertToLibrary(lastAddress, getArgsCaseSensitive(message));

			sendMessageAndUpdateView(chat, "Send SMS " + "( Number : " + Contact.getContactNameByNumber(lastAddress) + " Body : " + getArgsCaseSensitive(message) + " )" + "Done");

		}
	}
}