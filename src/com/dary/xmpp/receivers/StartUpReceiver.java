package com.dary.xmpp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dary.xmpp.MainService;
import com.dary.xmpp.MyApp;

public class StartUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isStartAtBoot = mPrefs.getBoolean("isStartAtBoot", false);
		if (isStartAtBoot) {
			System.out.println("StartUp Service");
			Intent startserviceintent = new Intent(context, MainService.class);
			context.startService(startserviceintent);

			MyApp myApp = (MyApp) context.getApplicationContext();
			myApp.setIsShouldRunning(true);
		}
	}
}
