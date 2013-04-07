package com.dary.xmpp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.dary.xmpp.IncallService;
import com.dary.xmpp.MainService;
import com.dary.xmpp.MyApp;
import com.dary.xmpp.Tools;
import com.dary.xmpp.ui.MainActivity;

public class ConnectionChangeReceiver extends BroadcastReceiver {

	private static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			System.out.println("尝试重新连接");
			Tools.doLog("Try Relogin");
			Intent mainserviceIntent = new Intent();
			mainserviceIntent.setClass(MyApp.getContext(), MainService.class);
			MyApp.getContext().startService(mainserviceIntent);

			Intent incallserviceIntent = new Intent();
			incallserviceIntent.setClass(MyApp.getContext(), IncallService.class);
			MyApp.getContext().startService(incallserviceIntent);
			super.handleMessage(msg);
		}
	};

	@Override
	public void onReceive(final Context context, Intent intent) {
		System.out.println("连接状态改变");
		Tools.doLog("Connectivty Change");
		MainService.sendMsg(MainActivity.NOT_LOGGED_IN);

		MyApp myApp = (MyApp) context.getApplicationContext();
		if (myApp.getIsShouldRunning()) {
			// Tools.doLog("isShouldRunning");
		} else {
			// Tools.doLog("isNotShouldRunning");
		}

		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isAutoReconnect = mPrefs.getBoolean("isAutoReconnect", true);
		if (isAutoReconnect && myApp.getIsShouldRunning()) {

			NetworkInfo netInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			// NetworkInfo activeNetInfo =
			// ServiceManager.conManager.getActiveNetworkInfo();

			// System.out.println("netInfo " + netInfo.isConnected() + " " +
			// netInfo.getType());
			// System.out.println("activeNetInfo " + activeNetInfo.isConnected()
			// + " " + activeNetInfo.getType());

			if (netInfo != null && netInfo.isAvailable() && !netInfo.isFailover() && netInfo.isConnected() && netInfo.getState() == NetworkInfo.State.CONNECTED) {
				if (null == MainService.connection || MainService.connection.isAuthenticated() != true) {
					handler.removeMessages(0);
					handler.sendEmptyMessageDelayed(0, 3000);
				}
			}
		}
	}
}
