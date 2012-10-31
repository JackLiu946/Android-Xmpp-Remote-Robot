package com.dary.xmpp;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {

	private static Context mContext;

	public void onCreate() {
		super.onCreate();

		mContext = getApplicationContext();

	}

	public static Context getContext() {
		return mContext;
	}
}
