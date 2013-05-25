package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.dary.xmpp.application.MyApp;
import com.dary.xmpp.tools.Contact;
import com.dary.xmpp.tools.Tools;

public class SmsCmd extends SmsCmdBase {

	public static void Sms(Chat chat, String message) {

		Cursor cur;
		Uri uri;
		final int RECEIVE = 1;
		final int SEND = 2;
		final int DRAFT = 3;
		final int OUTBOX = 4;
		final int FAILED = 5;
		final int QUEUED = 6;

		// 不带参数,则返回短信记录.
		if (!hasArgs(message)) {
			int smsCommandDisplayItemsNumber = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(MyApp.getContext()).getString(
					"smsCommandDisplayItemsNumber", "5"));
			// 如果值为0,则设为最大值
			if (smsCommandDisplayItemsNumber == 0) {
				smsCommandDisplayItemsNumber = Integer.MAX_VALUE;
			}
			uri = Uri.parse("content://sms");
			String[] projection = new String[] { "_id", "address", "body", "date", "type" };
			cur = MyApp.getContext().getContentResolver().query(uri, projection, null, null, null);
			StringBuilder sb = new StringBuilder();
			int smsNumber = 0;
			if (cur.moveToFirst()) {
				int index_Date = cur.getColumnIndex("date");
				int index_Type = cur.getColumnIndex("type");
				int index_Address = cur.getColumnIndex("address");
				int index_Body = cur.getColumnIndex("body");
				do {
					String strAddress = "";

					String strbody = cur.getString(index_Body);
					long longDate = cur.getLong(index_Date);
					int intType = cur.getInt(index_Type);
					// 如果短信类型不为Draft,才取取字段
					if (intType != DRAFT) {
						strAddress = cur.getString(index_Address);
					}
					String strDate = Tools.getTimeStr(longDate);

					String strType = "";

					switch (intType) {
					case RECEIVE:
						strType = "Receive";
						break;
					case SEND:
						strType = "Send";
						break;
					case DRAFT:
						strType = "Draft";
						break;
					case OUTBOX:
						strType = "Outbox";
						break;
					case FAILED:
						strType = "Failed";
						break;
					case QUEUED:
						strType = "Queued";
					}

					sb.append("[ ");
					sb.append(strDate + " , ");
					sb.append(strType + " , ");
					if (intType != DRAFT) {
						sb.append(Contact.getContactNameByNumber(strAddress) + " , ");
					} else {
						sb.append("UnKnown" + " , ");
					}
					sb.append(strbody);
					sb.append(" ]\n\n");
					smsNumber++;
				} while (cur.moveToNext() && smsNumber < smsCommandDisplayItemsNumber);
				if (!cur.isClosed()) {
					cur.close();
					cur = null;
				}
			} else {
				sb.append("No Result!");
			}
			sendMessageAndUpdateView(chat, Tools.delLastLine(sb));
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

			// 标记最后收到的短信为已读状态
			ContentValues cvalues = new ContentValues();
			cvalues.put("read", "1");
			MyApp.getContext().getContentResolver().update(uri, cvalues, " address='" + lastAddress + "'", null);

			// MainService.cur.close();
			// MainService.cur = null;

			// 发送短信并插入到短信库中
			// 如果有参数,但参数为空(即长度为0)
			if (getArgsCaseSensitive(message).length() == 0) {
				sendMessageAndUpdateView(chat, "Make Last Message As Read Done");
			} else {
				sendSMSAndInsertToLibrary(lastAddress, getArgsCaseSensitive(message));
				sendMessageAndUpdateView(chat, "Send SMS " + "( Number : " + Contact.getContactNameByNumber(lastAddress) + " Body : "
						+ getArgsCaseSensitive(message) + " )" + "Done");
			}
		}
	}
}