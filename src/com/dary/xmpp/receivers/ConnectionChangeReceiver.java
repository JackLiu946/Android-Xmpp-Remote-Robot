package com.dary.xmpp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.dary.xmpp.application.MyApp;
import com.dary.xmpp.service.MainService;
import com.dary.xmpp.tools.Tools;
import com.dary.xmpp.ui.MainActivity;

public class ConnectionChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		Tools.doLogPrintAndFile("Connectivty Change");
		MyApp myApp = (MyApp) context.getApplicationContext();
		if (myApp.getStatus() != MainActivity.DEBUG) {
			MainService.sendMsg(MainActivity.NOT_LOGGED_IN);

			SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
			boolean isAutoReconnectWhenNetStatusChange = mPrefs.getBoolean("isAutoReconnectWhenNetStatusChange", true);
			if (isAutoReconnectWhenNetStatusChange && myApp.getIsShouldRunning()) {

				NetworkInfo netInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

				// ConnectivityManager conManager = (ConnectivityManager)
				// MyApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
				// NetworkInfo activeNetInfo =
				// conManager.getActiveNetworkInfo();
				// Tools.doLogJustPrint("netInfo " + netInfo.isConnected() + " "
				// + netInfo.getType());
				// Tools.doLogJustPrint("activeNetInfo " +
				// activeNetInfo.isConnected() + " " + activeNetInfo.getType());

				if (netInfo != null && netInfo.isAvailable() && !netInfo.isFailover() && netInfo.isConnected()
						&& netInfo.getState() == NetworkInfo.State.CONNECTED) {
					if (null == MainService.connection || MainService.connection.isAuthenticated() != true) {
						// 通过延迟发消息的方式使得在短时间如果收到多次连接改变的广播(并且网络可用),仅登录一次
						int delay = 5000;
						MainService.tryReconnectHandler.removeMessages(0);
						MainService.tryReconnectHandler.sendEmptyMessageDelayed(0, delay);
					}
				}
			}
		}
	}
}
