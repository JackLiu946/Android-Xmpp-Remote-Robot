package com.dary.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.dary.xmpp.receivers.BatteryReceiver;
import com.dary.xmpp.receivers.ConnectionChangeReceiver;
import com.dary.xmpp.receivers.SMSReceiver;

public class MainService extends Service {

	public static Connection connection;
	private ConnectionConfiguration config;
	public static int intLevel;
	public static int intScale;
	public static String strPlugged = "";
	private String notifiedAddress;
	private String loginAddress;
	private String password;
	public Handler myHandler;
	private String serverHost;
	private String serverPort;
	private String resource;
	private boolean isautoReconnect;
	private boolean isDebugMode;
	public SMSReceiver smsReceiver = new SMSReceiver();
	private BatteryReceiver batteryReceiver = new BatteryReceiver();
	private ConnectionChangeReceiver connectionChangeReceiver;
	private boolean iscustomServer;
	private Context mContext = this;

	public static Chat chat;

	@Override
	public void onCreate() {
		// ����InCallService
		Intent incallserviceIntent = new Intent();
		incallserviceIntent.setClass(MainService.this, IncallService.class);
		startService(incallserviceIntent);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		IncallService.isFirstStart = true;

		// �ж������Ƿ�������
		getSetting();

		// ������ò�ȫ,��ʾToast
		if (loginAddress.equals("") || password.equals("") || notifiedAddress.equals("")) {
			sendMsg(XmppActivity.SET_INCOMPLETE);
		}
		// ����ŵ�¼
		else {
			// ������¼�߳�
			LoginInThread logininthread = new LoginInThread();
			Thread thread = new Thread(logininthread);
			thread.setName("LoginThread");
			thread.start();
			System.out.println("��¼�߳̿�ʼ����");

			// ���Խ���¼�ļ�¼�洢����,����ʱֻ�洢����ͨ���ı��ļ���
			Tools.doLog("Login");
			// ��¼��,������Ϣ,����UI.

			sendMsg(XmppActivity.LOGGING);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	// ��¼�߳�
	class LoginInThread implements Runnable {

		public void run() {
			if (iscustomServer) {
				config = new ConnectionConfiguration(serverHost, Integer.parseInt(serverPort));
			} else {
				config = new ConnectionConfiguration(serverHost);
			}

			// config.setTruststorePath("/system/etc/security/cacerts.bks");
			// config.setTruststorePassword("changeit");
			// config.setTruststoreType("bks");
			// config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
			// config.setReconnectionAllowed(false);
			// config.setSendPresence(false);
			// config.setCompressionEnabled(false);
			// config.setSASLAuthenticationEnabled(true);

			connection = new XMPPConnection(config);
			try {
				System.out.println("���������������");
				connection.connect();

				try {
					// ��ֹ��������ʱ��ε�¼.
					if (!connection.isAuthenticated() && connection.isConnected()) {

						System.out.println("��¼,��֤����");

						// connection.login(loginAddress,password,resource);
						connection.login(loginAddress, password, Tools.getTimeStr());

						// Tools.Vibrator(MainService.this, 500);

						System.out.println("��¼�ɹ�");
						Tools.doLog("Login Successful");
						makeNotification("Login Successful");

						// ��¼�ɹ�������Ϣ֪ͨActivity�ı䰴ť״̬
						sendMsg(XmppActivity.LOGIN_SUCCESSFUL);

						ChatManager chatmanager = connection.getChatManager();

						// ע����Ϣ������
						chat = chatmanager.createChat(notifiedAddress.toLowerCase(), new MsgListener());

						// ��¼�ɹ�֮�����ڳ���̬��ע������ı�,���ź����Ӹı�Ĺ㲥������,ע������ı�Ľ�����ʱ������Presence
						registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

						// ��������жϼ����������ӵĹ㲥�������Ƿ��Ѿ�ע����,����ᷴ��ע��.�����յ��㲥��ʱ���ε�¼
						if (isautoReconnect && connectionChangeReceiver == null) {
							connectionChangeReceiver = new ConnectionChangeReceiver();
							registerReceiver(connectionChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
						}

						registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

						// ��¼�ɹ�������Ϣ,���ڲ���
						if (isDebugMode) {
							SendMessageAndUpdateView.sendMessageAndUpdateView(chat, "Login is successful");
						}
					}

				} catch (XMPPException e) {
					System.out.println("��¼ʧ��");
					Tools.doLog("Login Failed");
					makeNotification("Login Failed");
					sendMsg(XmppActivity.LOGIN_FAILED);
					e.printStackTrace();
				}
			} catch (XMPPException e) {
				System.out.println("���ӷ�����ʧ��");
				Tools.doLog("Connection Failed");
				makeNotification("Connection Failed");
				sendMsg(XmppActivity.CONNECTION_FAILED);
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		if (connection.isConnected()) {
			Presence presence = new Presence(Presence.Type.unavailable);
			connection.sendPacket(presence);
			connection.disconnect();
		}
		// ��ע��㲥������
		unregisterReceiver(batteryReceiver);
		unregisterReceiver(smsReceiver);
		if (isautoReconnect) {
			unregisterReceiver(connectionChangeReceiver);
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void getSetting() {
		iscustomServer = getApplicationContext().getSharedPreferences("com.dary.xmpp_preferences", Activity.MODE_PRIVATE).getBoolean("isCustomServer", false);
		System.out.println("�Զ������������ " + iscustomServer);
		serverHost = getApplicationContext().getSharedPreferences("com.dary.xmpp_preferences", Activity.MODE_PRIVATE).getString("serverHost", "");
		System.out.println("���������� " + serverHost);
		serverPort = getApplicationContext().getSharedPreferences("com.dary.xmpp_preferences", Activity.MODE_PRIVATE).getString("serverPort", "5222");
		System.out.println("�������˿� " + serverPort);
		loginAddress = getApplicationContext().getSharedPreferences("com.dary.xmpp_preferences", Activity.MODE_PRIVATE).getString("loginAddress", "");
		System.out.println("��¼��ַ " + loginAddress);
		password = getApplicationContext().getSharedPreferences("com.dary.xmpp_preferences", Activity.MODE_PRIVATE).getString("password", "");
		System.out.println("���� " + password);
		notifiedAddress = getApplicationContext().getSharedPreferences("com.dary.xmpp_preferences", Activity.MODE_PRIVATE).getString("notifiedAddress", "");
		System.out.println("���ѵ�ַ " + notifiedAddress);
		resource = getApplicationContext().getSharedPreferences("com.dary.xmpp_preferences", Activity.MODE_PRIVATE).getString("resource", "");
		System.out.println("��Դ�� " + resource);
		isautoReconnect = getApplicationContext().getSharedPreferences("com.dary.xmpp_preferences", Activity.MODE_PRIVATE).getBoolean("isAutoReconnect", true);
		System.out.println("�Ƿ��������� " + isautoReconnect);
		isDebugMode = getApplicationContext().getSharedPreferences("com.dary.xmpp_preferences", Activity.MODE_PRIVATE).getBoolean("isDebugMode", false);
		System.out.println("����ģʽ " + isDebugMode);
	}

	private void sendMsg(int tag) {
		MyApp myApp = (MyApp) getApplication();
		myApp.setStatus(tag);
		// ��¼��,������Ϣ,����UI.
		if (null != XmppActivity.MsgHandler) {
			// �����޸�Ϊ,��Activity������ʱ��ȥ��ȡ״̬
			Message msg = new Message();
			msg.what = tag;
			XmppActivity.MsgHandler.sendMessage(msg);
		}
	}

	private void makeNotification(String str) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, str, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(mContext, XmppActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
		notification.setLatestEventInfo(mContext, str, str, contentIntent);
		notificationManager.notify(R.drawable.ic_launcher, notification);
	}

}