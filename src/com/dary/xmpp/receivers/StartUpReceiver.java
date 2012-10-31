package com.dary.xmpp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.dary.xmpp.MainService;

//������������
public class StartUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// �ж�����ѡ��
		SharedPreferences prefs = context.getSharedPreferences("com.dary.xmpp_preferences", 0);
		boolean isStartAtBoot = prefs.getBoolean("isStartAtBoot", false);
		if (isStartAtBoot) {
			Intent startserviceintent = new Intent(context, MainService.class);
			context.startService(startserviceintent);
		}
	}
}
