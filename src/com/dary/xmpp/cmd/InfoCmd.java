package com.dary.xmpp.cmd;

import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.dary.xmpp.Contact;
import com.dary.xmpp.ServiceManager;
import com.dary.xmpp.Tools;

public class InfoCmd extends CmdBase {
	static Chat chat = null;

	public static void Info(Chat chat, Message message) {
		InfoCmd.chat = chat;
		// 不带参数则返回全部信息(不包含联系人).
		if (!hasArgs(message)) {
			// infoSB.append("Active Network Type" +
			// ServiceManager.conManager.getActiveNetworkInfo().getTypeName()
			// +"\n");
			sendMessageAndUpdateView(chat, getTelInfo());
			sendMessageAndUpdateView(chat, getWiFiInfo());
			sendMessageAndUpdateView(chat, getAppInfo());
		} else if (getArgs(message).equals("app")) {
			sendMessageAndUpdateView(chat, getAppInfo());
		} else if (getArgs(message).equals("tel")) {
			sendMessageAndUpdateView(chat, getTelInfo());
		} else if (getArgs(message).equals("wifi")) {
			sendMessageAndUpdateView(chat, getWiFiInfo());
		}
		// 如果不含第2个参数也要包括进去.注意顺序
		else if (getArgs(message).equals("contact") || getFirArgs(message).equals("contact")) {
			// 取得用户输入的字串
			String findStr;
			// 不含有第2个参数,即"info:contact"
			if (!hasSecArgs(message)) {
				findStr = "";
			} else {
				findStr = getSecArgsCaseSensitive(message);
			}
			sendMessageAndUpdateView(chat, getContactInfo(findStr));
		}
	}

	static String getAppInfo() {
		List<PackageInfo> list = ServiceManager.pacManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		StringBuilder appInfo = new StringBuilder();
		appInfo.append("App Info : " + "\n\n");
		for (PackageInfo info : list) {
			ApplicationInfo aInfo = info.applicationInfo;
			appInfo.append("Application Name:" + aInfo.loadLabel(ServiceManager.pacManager) + "\n");
			appInfo.append("Package Name : " + info.packageName + "\n");
//			appInfo.append("" + "\n");
//			if (info.permissions != null) {
//				;
//				for (PermissionInfo p : info.permissions) {
//					appInfo.append("permission:" + p.name + "\n");
//				}
//			}
			appInfo.append("\n");
		}
		return appInfo.toString();
	}

	static String getTelInfo() {
		StringBuilder telInfo = new StringBuilder();
		telInfo.append("Tel Info : " + "\n\n");
		telInfo.append("DeviceID : " + ServiceManager.telManager.getDeviceId() + "\n");
		telInfo.append("Line1Number : " + ServiceManager.telManager.getLine1Number() + "\n");
		telInfo.append("Current operator : " + ServiceManager.telManager.getNetworkOperatorName() + "\n");
		telInfo.append("SimCountryIso : " + ServiceManager.telManager.getSimCountryIso() + "\n");
		telInfo.append("Device Software Version : " + ServiceManager.telManager.getDeviceSoftwareVersion() + "\n");
		telInfo.append("SIM Serial : " + ServiceManager.telManager.getSimSerialNumber() + "\n");
		telInfo.append("Subscriber ID : " + ServiceManager.telManager.getSubscriberId() + "\n");
		telInfo.append("Voice Mail Alpha Tag : " + ServiceManager.telManager.getVoiceMailAlphaTag() + "\n");
		telInfo.append("Voice Mail Number : " + ServiceManager.telManager.getVoiceMailNumber() + "\n");
		telInfo.append("Sim operator : " + ServiceManager.telManager.getSimOperatorName() + "\n");
		telInfo.append("Roaming activated : " + ServiceManager.telManager.isNetworkRoaming() + "\n");
		return telInfo.toString();
	}

	static String getWiFiInfo() {
		StringBuilder wifiInfo = new StringBuilder();
		wifiInfo.append("WiFi Info : " + "\n\n");
		wifiInfo.append("WiFi is Enabled : " + ServiceManager.wifManager.isWifiEnabled() + "\n");
		wifiInfo.append("WiFi Mac Address : " + ServiceManager.wifManager.getConnectionInfo().getMacAddress() + "\n");
		wifiInfo.append("WiFi Link Speed : " + ServiceManager.wifManager.getConnectionInfo().getLinkSpeed() + "\n");
		wifiInfo.append("WiFi SSID : " + ServiceManager.wifManager.getConnectionInfo().getSSID() + "\n");
		// 转换IP地址格式
		int ip = ServiceManager.wifManager.getConnectionInfo().getIpAddress();
		String ipaddress = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
		wifiInfo.append("WiFi IP Address : " + ipaddress + "\n");
		return wifiInfo.toString();
	}

	// 通过联系人姓名信息去查找联系人,返回电话.尝试再去获取如邮件,地址,公司等等的其他信息.
	static String getContactInfo(String findStr) {

		if (Contact.getSingleContactName(findStr, "Contact Info").equals(findStr)) {
			return getContactPhoneInfo(findStr);
		} else {
			return Contact.getSingleContactName(findStr, "Contact Info");
		}
	}

	static String getContactPhoneInfo(String ContactName) {
		// 取得联系人的电话号码的数量
		int numberOfPhoneNumber = Contact.getContactNumberByName(ContactName).size();
		// 如果联系人存在电话号码
		if (numberOfPhoneNumber != 0) {
			StringBuilder contactInfo = new StringBuilder();
			// 第一行为联系人姓名
			contactInfo.append(ContactName + "\n");
			// 依次取得联系人的号码
			for (int i = 0; i < numberOfPhoneNumber; i++) {
				contactInfo.append(Contact.getContactNumberByName(ContactName).get(i).toString());
				contactInfo.append("\n");
			}
			// 去除最后一个换行符
			return Tools.delLastLine(contactInfo);
		}
		return ContactName + "\nContact Has No Number";
	}

	// 联系人中同时存在"X"和"X?"名字的时候会陷入循环.已解决.
	// 在选择完成以后,不能再去模糊查询了.
	public static void SelDone(String ContactName) {
		sendMessageAndUpdateView(chat, getContactPhoneInfo(ContactName));
	}
}
