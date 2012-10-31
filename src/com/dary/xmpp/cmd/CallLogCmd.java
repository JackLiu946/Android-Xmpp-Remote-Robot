package com.dary.xmpp.cmd;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.Chat;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.CallLog;

import com.dary.xmpp.MainService;
import com.dary.xmpp.MyApp;

public class CallLogCmd extends CmdBase {
	public static void Calllog(Chat chat) {

		int defaultShowNumber = 5;
		int intType;
		String strType = "";
		String time = "";
		StringBuilder sb = new StringBuilder();
		ContentResolver cr = MyApp.getContext().getContentResolver();
		final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[] { CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE, CallLog.Calls.DATE }, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
		// �ж�ͨ����¼�Ƿ�Ϊ��
		if (cursor.moveToFirst()) {
			for (int i = 0; i < defaultShowNumber; i++)
			// for (int i = 0; i < cursor.getCount(); i++)
			{
				// �ж��Ƿ�С��Ĭ��ָ���Ĵ���
				boolean isHaveEntry = cursor.moveToPosition(i);
				if (isHaveEntry) {
					String strNumber = cursor.getString(0);
					String strName = cursor.getString(1);
					// ����ȥΪ2,������δ��Ϊ3.����������Ϊ1.
					intType = cursor.getInt(2);

					if (intType == 1) {
						strType = "Dial in";
					} else if (intType == 2) {
						strType = "Dial out";
					} else if (intType == 3) {
						strType = "Missed call";
					}

					SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					Date date = new Date(Long.parseLong(cursor.getString(3)));
					time = sfd.format(date);
					sb.append("[ " + time + " , " + strType + " , " + strName + " , " + strNumber + " ]" + "\n\n");
				}
			}
			// ȥ�����һ�����з�
			sb.delete(sb.toString().length() - 1, sb.toString().length());
		} else {
			sb.append("CallLog is Empty");
		}
		sendMessageAndUpdateView(chat, sb.toString());
	}
}
