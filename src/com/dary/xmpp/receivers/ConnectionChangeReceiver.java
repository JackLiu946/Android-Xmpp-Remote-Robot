package com.dary.xmpp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dary.xmpp.IncallService;
import com.dary.xmpp.MainService;
import com.dary.xmpp.Tools;

public class ConnectionChangeReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			System.out.println("�������Ӹı�");
			Tools.doLog("Connectivty Change");
			// NetworkInfo activeNetInfo = ServiceManager.conManager.getActiveNetworkInfo();
			NetworkInfo activeNetInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			// ״̬�ı��ʱ�����Զ���¼..����Ӧ����ȥ�����Դ
			if (activeNetInfo != null && activeNetInfo.isAvailable() && !activeNetInfo.isFailover() && activeNetInfo.isConnected() && activeNetInfo.getState().toString().equals("CONNECTED")) {
				if (null == MainService.connection || MainService.connection.isAuthenticated() != true) {
					System.out.println("������������");
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
