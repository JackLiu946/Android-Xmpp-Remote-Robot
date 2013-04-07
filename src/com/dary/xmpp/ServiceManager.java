package com.dary.xmpp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;

public abstract class ServiceManager {
	public static TelephonyManager telManager = (TelephonyManager) MyApp.getContext().getSystemService(Context.TELEPHONY_SERVICE);
	public static WifiManager wifManager = (WifiManager) MyApp.getContext().getSystemService(Context.WIFI_SERVICE);
	public static ClipboardManager cliManager = (ClipboardManager) MyApp.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
	public static LocationManager locManager = (LocationManager) MyApp.getContext().getSystemService(Context.LOCATION_SERVICE);
	public static AudioManager audManager = (AudioManager) MyApp.getContext().getSystemService(Context.AUDIO_SERVICE);
	public static ConnectivityManager conManager = (ConnectivityManager) MyApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	public static PackageManager pacManager = (PackageManager) MyApp.getContext().getPackageManager();
	public static NotificationManager notificationManager = (NotificationManager) MyApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
}
