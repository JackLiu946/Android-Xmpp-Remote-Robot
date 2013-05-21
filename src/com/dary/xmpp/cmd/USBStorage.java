package com.dary.xmpp.cmd;

import android.content.ContentResolver;
import android.content.Intent;
import android.provider.Settings;

import com.dary.xmpp.application.MyApp;

public class USBStorage extends CmdBase {

	public static void OpenUSBStorage() {
		ContentResolver cr = MyApp.getContext().getContentResolver();
		// Settings.System.putString(cr,
		// Settings.System.USB_MASS_STORAGE_ENABLED, "1");
		Settings.System.putString(cr, Settings.Secure.USB_MASS_STORAGE_ENABLED, "1");
		Intent intentOn = new Intent(Intent.ACTION_MEDIA_MOUNTED);
		MyApp.getContext().sendBroadcast(intentOn);
	}
}
