package com.dary.xmpp;

import java.util.Locale;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;

import com.dary.xmpp.cmd.CmdBase;
import com.dary.xmpp.receivers.BatteryReceiver;
import com.dary.xmpp.receivers.SMSReceiver;

public class MainService extends Service {

	public static Connection connection;
	private ConnectionConfiguration config;
	public static int intLevel;
	public static int intScale;
	public static String strPlugged = "";
	public static String notifiedAddress;
	public static String loginAddress;
	private String password;
	public Handler myHandler;
	private String serverHost;
	private String serverPort;
	private String resource;
	private boolean isautoReconnect;
	private boolean isDebugMode;
	public SMSReceiver smsReceiver = new SMSReceiver();
	private BatteryReceiver batteryReceiver = new BatteryReceiver();
	private boolean iscustomServer;
	private Context mContext = this;
	public static MyApp myApp;

	public static Chat chat;

	@Override
	public void onCreate() {
		// 启动InCallService
		myApp = (MyApp) getApplication();
		// 提高优先级
		setForeground(true);
		Intent incallserviceIntent = new Intent();
		incallserviceIntent.setClass(MainService.this, IncallService.class);
		startService(incallserviceIntent);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		IncallService.isFirstStart = true;

		getSetting();
		// 如果配置不全,显示Toast
		if (loginAddress.equals("") || password.equals("") || notifiedAddress.equals("")) {
			sendMsg(XmppActivity.SET_INCOMPLETE);
		}
		// 否则才登录
		else {
			// 启动登录线程
			LoginInThread loginInThread = new LoginInThread();
			Thread thread = new Thread(loginInThread);
			thread.setName("LoginThread");
			thread.start();
			System.out.println("登录线程开始运行");

			// 尝试将登录的记录存储下来,先暂时只存储到普通的文本文件中
			Tools.doLog("Login");
			// 登录中,发送消息,更新UI.

			sendMsg(XmppActivity.LOGGING);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	// 登录线程
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
				System.out.println("与服务器建立连接");
				connection.connect();

				try {
					// 防止重新连接时多次登录.
					if (!connection.isAuthenticated() && connection.isConnected()) {

						System.out.println("登录,验证口令");

						// connection.login(loginAddress,password,resource);
						connection.login(loginAddress, password, Tools.getTimeStr());

						// Tools.Vibrator(MainService.this, 500);
						System.out.println("登录成功");
						Tools.doLog("Login Successful");
						makeNotification("Login Successful");

						// 登录成功后发送消息通知Activity改变按钮状态
						sendMsg(XmppActivity.LOGIN_SUCCESSFUL);

						ChatManager chatmanager = connection.getChatManager();

						// 注册消息监听器
						chat = chatmanager.createChat(notifiedAddress.toLowerCase(Locale.getDefault()), new MsgListener());

						// 登录成功之后再在程序动态的注册电量改变,短信的广播接收器,注册电量改变的接收器时会设置Presence
						registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
						registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

						// 登录成功后发送消息,用于测试
						if (isDebugMode) {
							CmdBase.sendMessageAndUpdateView(chat, "Login is successful");
						}
					}

				} catch (XMPPException e) {
					System.out.println("登录失败");
					Tools.doLog("Login Failed");
					makeNotification("Login Failed");
					sendMsg(XmppActivity.LOGIN_FAILED);
					e.printStackTrace();
				}
			} catch (XMPPException e) {
				System.out.println("连接服务器失败");
				Tools.doLog("Connection Failed");
				makeNotification("Connection Failed");
				sendMsg(XmppActivity.CONNECTION_FAILED);
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		Tools.doLog("Service Destroy");
		sendMsg(XmppActivity.NOT_LOGGED_IN);
		if (connection.isConnected()) {
			Presence presence = new Presence(Presence.Type.unavailable);
			connection.sendPacket(presence);
			connection.disconnect();
		}
		// 反注册广播接收器
		unregisterReceiver(batteryReceiver);
		unregisterReceiver(smsReceiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void getSetting() {
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		iscustomServer = mPrefs.getBoolean("isCustomServer", false);
		System.out.println("自定义服务器设置 " + iscustomServer);
		serverHost = mPrefs.getString("serverHost", "");
		System.out.println("服务器主机 " + serverHost);
		serverPort = mPrefs.getString("serverPort", "5222");
		System.out.println("服务器端口 " + serverPort);
		loginAddress = mPrefs.getString("loginAddress", "");
		System.out.println("登录地址 " + loginAddress);
		password = mPrefs.getString("password", "");
		System.out.println("密码 " + password);
		notifiedAddress = mPrefs.getString("notifiedAddress", "");
		System.out.println("提醒地址 " + notifiedAddress);
		resource = mPrefs.getString("resource", "");
		System.out.println("资源名 " + resource);
		isautoReconnect = mPrefs.getBoolean("isAutoReconnect", true);
		System.out.println("是否重新连接 " + isautoReconnect);
		isDebugMode = mPrefs.getBoolean("isDebugMode", false);
		System.out.println("调试模式 " + isDebugMode);
	}

	public static void sendMsg(int tag) {
		myApp.setStatus(tag);
		// 登录中,发送消息,更新UI.
		if (null != XmppActivity.MsgHandler) {
			// 考虑修改为,当Activity启动的时候去读取状态
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