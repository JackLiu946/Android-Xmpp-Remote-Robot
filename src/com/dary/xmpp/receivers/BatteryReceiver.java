package com.dary.xmpp.receivers;

import org.jivesoftware.smack.packet.Presence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.dary.xmpp.MainService;

public class BatteryReceiver extends BroadcastReceiver {
	private int intLevel;
	private int intScale;
	private String strPlugged;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
			int intPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			intLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			intScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			int batteryPct = intLevel * 100 / intScale;
			if (intPlugged == 0) {
				strPlugged = "Battery";
			} else {
				strPlugged = "USB";
			}
			if (MainService.connection.isConnected()) {
				Presence presence = new Presence(Presence.Type.available);
				presence.setStatus(strPlugged + " Power: " + String.valueOf(batteryPct) + "%");
				MainService.connection.sendPacket(presence);
			}
		}

	}
}