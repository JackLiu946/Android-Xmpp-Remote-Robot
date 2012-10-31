package com.dary.xmpp;

import java.util.ArrayList;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.dary.xmpp.cmd.SelCmd;

public class Contact {
	// ����ȥ�ҵ�һ����ϵ������.�����Ϊ���򴴽�ѡ��,���ش���ѡ�����ַ���.
	public static String getSingleContactName(String findStr, String from) {
		// ���ص���ϵ�˵�����
		int numberOfContactNames = getContactNamesByName(findStr).size();
		// ����ҵ�
		if (numberOfContactNames != 0) {
			// �ҵ�,������ϵ������Ϊ1�Һ����ֺ��û��������һ��.
			if (numberOfContactNames == 1 && findStr.equals(getContactNamesByName(findStr).get(0))) {
				return findStr;
			}
			// �ҵ�,(������ϵ����������1)
			else {
				return SelCmd.createChoices(getContactNamesByName(findStr), from + " Select Contacts");
			}
		}
		return "Can't Find It";

	}

	// ͨ����ѯ�����ȡ��ϵ������.����Ҳ�������ԭ����
	public static String getContactNameByNumber(String mNumber) {
		mNumber = mNumber.replace("+86", "");
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };
		Cursor cursor = MainService.mainservice.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + mNumber + "'", null, null);

		if (null == cursor) {
			System.out.println("cursor null");
			return mNumber;
		}
		// ��������ʹ��ͬһ������,����ϵ��������ͬ�Ŀ����Ժ�С..�����ҵ���һ���ͷ�����.
		else {
			cursor.moveToFirst();
			int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
			String name = cursor.getString(nameFieldColumnIndex);
			return name;
		}
	}

	// ͨ����ѯ����ȡ����ϵ�˺���
	public static ArrayList<String> getContactNumberByName(String mName) {
		ArrayList<String> listPhoneNumber = new ArrayList<String>();
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };

		Cursor cursor = MainService.mainservice.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = '" + mName + "'", null, null);

		if (null == cursor) {
			System.out.println("cursor null");
			return listPhoneNumber;
		}
		if (cursor.moveToFirst()) {
			do {
				String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				// ȥ��"-"����
				phoneNumber = phoneNumber.replace("-", "");
				listPhoneNumber.add(phoneNumber);
			} while (cursor.moveToNext());
			return listPhoneNumber;
		}
		return listPhoneNumber;
	}

	// ͨ����ѯ���ַ��ض�����������ֶε���ϵ������
	// �Ժ�ῼ���޸�ʵ�ֲ�ѯ����������ȡ����ϵ������
	// ֻ�����˺��е绰�������ϵ�˵������ѽ��,�ǲ�ѯʱ����ĵ�һ������,�������URI���µ�

	public static ArrayList<String> getContactNamesByName(String mName) {
		ArrayList<String> listContactNames = new ArrayList<String>();
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME };

		Cursor cursor = MainService.mainservice.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?", new String[] { "" + "%" + mName + "%" + "" }, null);

		// �Ҳ���ָ����ϵ��
		if (null == cursor) {
			System.out.println("cursor null,�Ҳ����ƶ���ϵ��");
			return listContactNames;
		}

		if (cursor.moveToFirst()) {
			String lastContactName = "";
			do {
				String ContactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
				// ������Ҫ�ж�ǰһ��ȡ�õ���ϵ�����ֺ���һ���Ƿ�һ��,ֻ�в�һ�µ�ʱ�����ӵ�ArrayList��
				if (!ContactName.equals(lastContactName)) {
					listContactNames.add(ContactName);
				}
				lastContactName = new String(ContactName);
			} while (cursor.moveToNext());
			return listContactNames;
		}
		return listContactNames;
	}

	// ͨ����ѯ����ȡ����ϵ������
	public static ArrayList<String> getContactEmailByName(String mName) {
		ArrayList<String> listEmail = new ArrayList<String>();
		Cursor cursor = MainService.mainservice.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = '" + mName + "'", null, null);

		if (null == cursor) {
			System.out.println("cursor null");
			return listEmail;
		}
		if (cursor.moveToFirst()) {
			do {
				String email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
				listEmail.add(email);
			} while (cursor.moveToNext());
			return listEmail;
		}
		return listEmail;
	}
}
