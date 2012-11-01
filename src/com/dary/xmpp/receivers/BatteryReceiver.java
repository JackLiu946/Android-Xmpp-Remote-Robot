package com.dary.xmpp.receivers;

import org.jivesoftware.smack.packet.Presence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dary.xmpp.MainService;

public class BatteryReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
			int intPlugged = intent.getIntExtra("plugged", 0);
			MainService.intLevel = intent.getIntExtra("level", 0);
			MainService.intScale = intent.getIntExtra("scale", 100);
			if (intPlugged == 0) {
				MainService.strPlugged = "Battery";
			}
			// USB状态貌似应该是2的.
			else {
				MainService.strPlugged = "USB";
			}
			// 这里需要判断是否登录.
			if (MainService.connection.isConnected()) {
				Presence presence = new Presence(Presence.Type.available);
				presence.setStatus(MainService.strPlugged + " Power: " + String.valueOf(MainService.intLevel * 100 / MainService.intScale) + "%");
				MainService.connection.sendPacket(presence);
			}
		}

	}
}