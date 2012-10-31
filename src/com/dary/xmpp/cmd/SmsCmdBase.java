package com.dary.xmpp.cmd;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

import com.dary.xmpp.MainService;

public class SmsCmdBase extends CmdBase {
	// ���Ͷ���,��������ſ���
	static void sendSMSAndInsertToLibrary(String addressNumber, String body) {
		SmsManager smsManager = SmsManager.getDefault();
		PendingIntent pendingIntent = PendingIntent.getBroadcast(MainService.mainservice, 0, new Intent(), 0);
		smsManager.sendTextMessage(addressNumber, null, body, pendingIntent, null);
		insertSMSToLibrary(addressNumber, body);
	}

	// �����Ų�����ſ���.
	static void insertSMSToLibrary(String addressNumber, String body) {
		ContentValues values = new ContentValues();
		values.put("address", addressNumber);
		values.put("date", System.currentTimeMillis());
		values.put("body", body);
		MainService.mainservice.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
	}
}
