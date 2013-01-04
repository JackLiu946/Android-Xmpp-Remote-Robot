package com.dary.xmpp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.dary.xmpp.IncallService;
import com.dary.xmpp.MainService;
import com.dary.xmpp.MyApp;
import com.dary.xmpp.Tools;
import com.dary.xmpp.XmppActivity;

public class ConnectionChangeReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, Intent intent) {
		System.out.println("连接状态改变");
		Tools.doLog("Connectivty Change");
		MainService.sendMsg(XmppActivity.NOT_LOGGED_IN);

		MyApp myApp = (MyApp) context.getApplicationContext();
		if (myApp.getIsShouldRunning()) {
			Tools.doLog("isShouldRunning");
		} else {
			Tools.doLog("isNotShouldRunning");
		}

		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isAutoReconnect = mPrefs.getBoolean("isAutoReconnect", true);
		if (isAutoReconnect && myApp.getIsShouldRunning()) {
			// NetworkInfo activeNetInfo = ServiceManager.conManager.getActiveNetworkInfo();
			NetworkInfo activeNetInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (activeNetInfo != null && activeNetInfo.isAvailable() && !activeNetInfo.isFailover() && activeNetInfo.isConnected() && activeNetInfo.getState().toString().equals("CONNECTED")) {
				if (null == MainService.connection || MainService.connection.isAuthenticated() != true) {
					System.out.println("尝试重新连接");
					Tools.doLog("Try Relogin");
					Intent mainserviceIntent = new Intent();
					mainserviceIntent.setClass(context, MainService.class);
					// context.stopService(mainserviceIntent);
					context.startService(mainserviceIntent);

					Intent incallserviceIntent = new Intent();
					incallserviceIntent.setClass(context, IncallService.class);
					// context.stopService(incallserviceIntent);
					context.startService(incallserviceIntent);
				}
			}
		}
	}
}
