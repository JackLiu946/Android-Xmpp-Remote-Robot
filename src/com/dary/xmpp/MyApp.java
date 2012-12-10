package com.dary.xmpp;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application{

	private static Context mContext;
	private int status = XmppActivity.NOT_LOGGED_IN;

	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
	}

	public static Context getContext() {
		return mContext;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int s) {
		this.status = s;
	}
}