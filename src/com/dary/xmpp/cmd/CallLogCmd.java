package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;

import android.content.ContentResolver;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.CallLog;

import com.dary.xmpp.application.MyApp;
import com.dary.xmpp.tools.Tools;

public class CallLogCmd extends CmdBase {
	public static void Calllog(Chat chat) {

		int calllogCommandDisplayItemsNumber = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(MyApp.getContext()).getString("calllogCommandDisplayItemsNumber", "5"));
		// 如果值为0,则设为最大值
		if (calllogCommandDisplayItemsNumber == 0) {
			calllogCommandDisplayItemsNumber = Integer.MAX_VALUE;
		}
		int intType;
		String strType = "";
		String time = "";
		StringBuilder sb = new StringBuilder();
		ContentResolver cr = MyApp.getContext().getContentResolver();
		Cursor cur = cr.query(CallLog.Calls.CONTENT_URI, new String[] { CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE, CallLog.Calls.DATE }, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
		// Cursor cur = cr.query(CallLog.Calls.CONTENT_URI, null, null, null,
		// null);
		int calllogNumber = 0;
		// 判断是否有记录
		if (cur.moveToFirst()) {
			do {
				calllogNumber++;
				String strNumber = cur.getString(0);
				String strName = cur.getString(1);
				if (strName == null) {
					strName = "Unkown Contact";
				}
				intType = cur.getInt(2);

				if (intType == 1) {
					strType = "Dial in";
				} else if (intType == 2) {
					strType = "Dial out";
				} else if (intType == 3) {
					strType = "Missed call";
				}
				time = Tools.getTimeStr(Long.parseLong(cur.getString(3)));
				sb.append("[ " + time + " , " + strType + " , " + strName + " , " + strNumber + " ]" + "\n\n");
			} while ((cur.moveToNext() && calllogNumber < calllogCommandDisplayItemsNumber));
			if (!cur.isClosed()) {
				cur.close();
			} else {
				sb.append("CallLog is Empty");
			}
			cur.close();
			sendMessageAndUpdateView(chat, Tools.delLastLine(sb));
		}
	}
}