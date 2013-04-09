package com.dary.xmpp.cmd;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

import com.dary.xmpp.MyApp;

public class SmsCmdBase extends CmdBase {
	// 发送短信,并插入短信库中
	static void sendSMSAndInsertToLibrary(String addressNumber, String body) {
		SmsManager smsManager = SmsManager.getDefault();
		PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApp.getContext(), 0, new Intent(), 0);
		smsManager.sendTextMessage(addressNumber, null, body, pendingIntent, null);
		insertSMSToLibrary(addressNumber, body);
	}

	// 将短信插入短信库中.
	static void insertSMSToLibrary(String addressNumber, String body) {
		ContentValues values = new ContentValues();
		values.put("address", addressNumber);
		values.put("date", System.currentTimeMillis());
		values.put("body", body);
		MyApp.getContext().getContentResolver().insert(Uri.parse("content://sms/sent"), values);
	}
}
