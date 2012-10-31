package com.dary.xmpp;

import java.util.ArrayList;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.dary.xmpp.cmd.SelCmd;

public class Contact {
	// 尝试去找单一的联系人名字.如果多为个则创建选择,返回创建选择后的字符串.
	public static String getSingleContactName(String findStr, String from) {
		// 返回的联系人的数量
		int numberOfContactNames = getContactNamesByName(findStr).size();
		// 如果找到
		if (numberOfContactNames != 0) {
			// 找到,并且联系人数量为1且和名字和用户所输入的一致.
			if (numberOfContactNames == 1 && findStr.equals(getContactNamesByName(findStr).get(0))) {
				return findStr;
			}
			// 找到,(并且联系人数量大于1)
			else {
				return SelCmd.createChoices(getContactNamesByName(findStr), from + " Select Contacts");
			}
		}
		return "Can't Find It";

	}

	// 通过查询号码获取联系人姓名.如果找不到返回原号码
	public static String getContactNameByNumber(String mNumber) {
		mNumber = mNumber.replace("+86", "");
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };
		Cursor cursor = MainService.mainservice.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + mNumber + "'", null, null);

		if (null == cursor) {
			System.out.println("cursor null");
			return mNumber;
		}
		// 这里由于使用同一个号码,而联系人姓名不同的可能性很小..所以找到第一个就返回了.
		else {
			cursor.moveToFirst();
			int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
			String name = cursor.getString(nameFieldColumnIndex);
			return name;
		}
	}

	// 通过查询名字取得联系人号码
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
				// 去除"-"符号
				phoneNumber = phoneNumber.replace("-", "");
				listPhoneNumber.add(phoneNumber);
			} while (cursor.moveToNext());
			return listPhoneNumber;
		}
		return listPhoneNumber;
	}

	// 通过查询名字返回多个包含传入字段的联系人名字
	// 以后会考虑修改实现查询号码邮箱来取得联系人名字
	// 只返回了含有电话号码的联系人的问题已解决,是查询时输入的第一个参数,即传入的URI导致的

	public static ArrayList<String> getContactNamesByName(String mName) {
		ArrayList<String> listContactNames = new ArrayList<String>();
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME };

		Cursor cursor = MainService.mainservice.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?", new String[] { "" + "%" + mName + "%" + "" }, null);

		// 找不到指定联系人
		if (null == cursor) {
			System.out.println("cursor null,找不到制定联系人");
			return listContactNames;
		}

		if (cursor.moveToFirst()) {
			String lastContactName = "";
			do {
				String ContactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
				// 这里需要判断前一个取得的联系人名字和新一个是否一致,只有不一致的时候才添加到ArrayList中
				if (!ContactName.equals(lastContactName)) {
					listContactNames.add(ContactName);
				}
				lastContactName = new String(ContactName);
			} while (cursor.moveToNext());
			return listContactNames;
		}
		return listContactNames;
	}

	// 通过查询名字取得联系人邮箱
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
