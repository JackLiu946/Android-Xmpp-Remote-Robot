package com.dary.xmpp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;

public abstract class ServiceManager {
	public static TelephonyManager telManager = (TelephonyManager) MainService.mainservice.getSystemService(Context.TELEPHONY_SERVICE);
	public static WifiManager wifManager = (WifiManager) MainService.mainservice.getSystemService(Context.WIFI_SERVICE);
	public static ClipboardManager cliManager = (ClipboardManager) MainService.mainservice.getSystemService(Context.CLIPBOARD_SERVICE);
	public static LocationManager locManager = (LocationManager) MainService.mainservice.getSystemService(Context.LOCATION_SERVICE);
	public static AudioManager audManager = (AudioManager) MainService.mainservice.getSystemService(Context.AUDIO_SERVICE);
//	public static ConnectivityManager conManager =(ConnectivityManager)MainService.mainservice.getSystemService(Context.CONNECTIVITY_SERVICE);
	public static PackageManager pacManager = (PackageManager) MainService.mainservice.getPackageManager();
	public static NotificationManager notificationManager = (NotificationManager) MainService.mainservice.getSystemService(Context.NOTIFICATION_SERVICE);
}
