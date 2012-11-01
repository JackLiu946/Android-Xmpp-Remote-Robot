package com.dary.xmpp;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Vibrator;

public class Tools {

	public static String delLastLine(StringBuilder sb) {
		return sb.delete(sb.toString().length() - 1, sb.toString().length()).toString();
	}

	public static String getTimeStr() {
		Date date = new Date();
		String timeStr = new DecimalFormat("00").format(date.getHours()) + ":" + new DecimalFormat("00").format(date.getMinutes()) + ":" + new DecimalFormat("00").format(date.getSeconds()) + "";
		return timeStr;
	}

	public static String getTimeStrHyphen() {
		Date date = new Date();
		String timeStr = new DecimalFormat("00").format(date.getHours()) + "-" + new DecimalFormat("00").format(date.getMinutes()) + "-" + new DecimalFormat("00").format(date.getSeconds()) + "";
		return timeStr;
	}

	public static String getLongTimeStr() {
		Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("MM-dd HH-mm-ss");
		String retStrFormatNowDate = sdFormatter.format(nowTime);
		return retStrFormatNowDate;
	}

	public static void Vibrator(Context context, int time) {
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 0, time };
		vibrator.vibrate(pattern, -1);
	}

	public static void doLog(String str) {
		try {
			// 注意如果文件不存在的时候(确切的说应该是文件的内容为空时),添加新内容之前要先添加换行符.
			File fileDirectory = new File("/data/data/com.dary.xmpp/files/");
			if (!fileDirectory.exists())
			{
				fileDirectory.mkdir();
			}
			File file = new File("/data/data/com.dary.xmpp/files/LoginLog");
			StringBuilder sb = new StringBuilder();
			if (file.exists()) {
				sb.append("\n");
			} else {
				file.createNewFile();
			}
			sb.append(str);
			//占满整个一行,对齐
			int length = 29;
			for (int i = 0; i < length - str.length(); i++) {
				sb.append(" ");
			}
			sb.append(Tools.getLongTimeStr());
			// FileOutputStream fos = openFileOutput("LoginLog", Context.MODE_APPEND);
			FileOutputStream fos = new FileOutputStream(file, true);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
