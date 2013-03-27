package com.dary.xmpp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.dary.xmpp.cmd.CmdBase;

public class IncallService extends Service {

	private mPhoneCallListener phoneListener = new mPhoneCallListener();
	public static boolean isFirstStart;

	private class mPhoneCallListener extends PhoneStateListener {
		public void onCallStateChanged(int state, String incomingNumber) {
			if (!isFirstStart) {
				if (state == TelephonyManager.CALL_STATE_RINGING) {
					CmdBase.sendMessageAndUpdateView(MainService.chat, "RINGING");
					CmdBase.sendMessageAndUpdateView(MainService.chat, "From : " + Contact.getContactNameByNumber(incomingNumber));
					System.out.println("RINGING: Number " + incomingNumber);
				} else if (state == TelephonyManager.CALL_STATE_IDLE) {
					CmdBase.sendMessageAndUpdateView(MainService.chat, "IDLE");
					System.out.println("IDLE");
				} else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
					CmdBase.sendMessageAndUpdateView(MainService.chat, "OFFHOOK");
					System.out.println("OFFHOOK");
				}
			}
			isFirstStart = false;
		}
	}

	@Override
	public void onCreate() {
		// 有问题,改为不调用ServiceManager中的telManager
		isFirstStart = true;
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telManager.listen(phoneListener, mPhoneCallListener.LISTEN_CALL_STATE);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
