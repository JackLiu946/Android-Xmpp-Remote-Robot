package com.dary.xmpp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.dary.xmpp.MainService;

//开机启动服务
public class StartUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 判断配置选项
		SharedPreferences prefs = context.getSharedPreferences("com.dary.xmpp_preferences", 0);
		boolean isStartAtBoot = prefs.getBoolean("isStartAtBoot", false);
		if (isStartAtBoot) {
			Intent startserviceintent = new Intent(context, MainService.class);
			context.startService(startserviceintent);
		}
	}
}
