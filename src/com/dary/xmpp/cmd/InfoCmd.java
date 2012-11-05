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
		// ���������򷵻�ȫ����Ϣ(��������ϵ��).
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
		// ���������2������ҲҪ������ȥ.ע��˳��
		else if (getArgs(message).equals("contact") || getFirArgs(message).equals("contact")) {
			// ȡ���û�������ִ�
			String findStr;
			// �����е�2������,��"info:contact"
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
		// ת��IP��ַ��ʽ
		int ip = ServiceManager.wifManager.getConnectionInfo().getIpAddress();
		String ipaddress = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
		wifiInfo.append("WiFi IP Address : " + ipaddress + "\n");
		return wifiInfo.toString();
	}

	// ͨ����ϵ��������Ϣȥ������ϵ��,���ص绰.������ȥ��ȡ���ʼ�,��ַ,��˾�ȵȵ�������Ϣ.
	static String getContactInfo(String findStr) {

		if (Contact.getSingleContactName(findStr, "Contact Info").equals(findStr)) {
			return getContactPhoneInfo(findStr);
		} else {
			return Contact.getSingleContactName(findStr, "Contact Info");
		}
	}

	static String getContactPhoneInfo(String ContactName) {
		// ȡ����ϵ�˵ĵ绰���������
		int numberOfPhoneNumber = Contact.getContactNumberByName(ContactName).size();
		// �����ϵ�˴��ڵ绰����
		if (numberOfPhoneNumber != 0) {
			StringBuilder contactInfo = new StringBuilder();
			// ��һ��Ϊ��ϵ������
			contactInfo.append(ContactName + "\n");
			// ����ȡ����ϵ�˵ĺ���
			for (int i = 0; i < numberOfPhoneNumber; i++) {
				contactInfo.append(Contact.getContactNumberByName(ContactName).get(i).toString());
				contactInfo.append("\n");
			}
			// ȥ�����һ�����з�
			return Tools.delLastLine(contactInfo);
		}
		return ContactName + "\nContact Has No Number";
	}

	// ��ϵ����ͬʱ����"X"��"X?"���ֵ�ʱ�������ѭ��.�ѽ��.
	// ��ѡ������Ժ�,������ȥģ����ѯ��.
	public static void SelDone(String ContactName) {
		sendMessageAndUpdateView(chat, getContactPhoneInfo(ContactName));
	}
}
