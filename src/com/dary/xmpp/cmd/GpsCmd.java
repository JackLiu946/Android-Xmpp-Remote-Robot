package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.dary.xmpp.ServiceManager;

public class GpsCmd extends CmdBase {
	public static void Gps(Chat chat) {
		// if (MainService.locationmanager
		// .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER))
		// {
		// System.out.println("gps3");
		// Toast.makeText(XmppActivity.xmppactivity,
		// "GPSģ������", Toast.LENGTH_SHORT)
		// .show();
		// //return;
		// }
		// Toast.makeText(XmppActivity.xmppactivity, "Please Open GPS",
		// Toast.LENGTH_SHORT).show();
		// Intent intent = new Intent(
		// Settings.ACTION_SECURITY_SETTINGS);
		// MainService.mainservice.startActivity(intent);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE); // �;���
		// criteria.setAltitudeRequired(false); //�߶�
		// criteria.setBearingRequired(false); //����
		criteria.setCostAllowed(false); // ���
		// criteria.setPowerRequirement(Criteria.POWER_LOW); ����
		String provider = ServiceManager.locManager.getBestProvider(criteria, true); // ��ȡGPS��Ϣ
		Location location = ServiceManager.locManager.getLastKnownLocation(provider); // ͨ��GPS��ȡλ��
		if (location != null) {
			System.out.println(location.getLatitude());
			System.out.println(location.getLongitude());

			sendMessageAndUpdateView(chat, "http://maps.google.com/maps?q=" + location.getLatitude() + ",+" + location.getLongitude());

		}
		// ���ü��������Զ����µ���Сʱ��Ϊ���N��(1��Ϊ1*1000������д��ҪΪ�˷���)����Сλ�Ʊ仯����N��
		ServiceManager.locManager.requestLocationUpdates(provider, 10 * 1000, 100, new LocationListener() {

			public void onLocationChanged(Location location) {
				// // method stub
				// if (location != null)
				// {
				// System.out.println(location.getLatitude());
				// System.out.println(location.getLongitude());
				// try
				// {
				// chat.sendMessage("http://maps.google.com/maps?q="+
				// location.getLatitude() + ",+" +
				// location.getLongitude());
				// }
				// catch (XMPPException e)
				// {
				// e.printStackTrace();
				// }
				// }

			}

			public void onProviderDisabled(String provider) {

			}

			public void onProviderEnabled(String provider) {

			}

			public void onStatusChanged(String provider, int status, Bundle extras) {

			}

		});
	}
}
