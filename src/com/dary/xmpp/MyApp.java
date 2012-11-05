package com.dary.xmpp;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Looper;

public class MyApp extends Application implements Thread.UncaughtExceptionHandler {

	private static Context mContext;
	private int status;

	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		Thread.setDefaultUncaughtExceptionHandler(this);
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

	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		final String crashReport = getCrashReport(mContext, ex);
		// 显示异常信息&发送报告
		new Thread() {
			public void run() {
				Looper.prepare();
				sendAppCrashReport(mContext, crashReport);
				Looper.loop();
			}
		}.start();
	}

	private String getCrashReport(Context context, Throwable ex) {
		PackageInfo pinfo = getPackageInfo();
		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("Version: " + pinfo.versionName + "(" + pinfo.versionCode + ")\n");
		exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE + "(" + android.os.Build.MODEL + ")\n");
		exceptionStr.append("Exception: " + ex.getMessage() + "\n");
		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			exceptionStr.append(elements[i].toString() + "\n");
		}
		return exceptionStr.toString();
	}

	public static void sendAppCrashReport(final Context cont, final String crashReport) {
		Intent i = new Intent(Intent.ACTION_SEND);
		// i.setType("text/plain"); //模拟器
		i.setType("message/rfc822"); // 真机
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { "anyofyou@gmail.com" });
		i.putExtra(Intent.EXTRA_SUBJECT, "Android XMPP Remote Robot Error Report");
		i.putExtra(Intent.EXTRA_TEXT, crashReport);
		cont.startActivity(Intent.createChooser(i, "Send Error Report"));
		// 退出
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}

	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}
}
