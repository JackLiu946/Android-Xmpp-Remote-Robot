package com.dary.xmpp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final int RECEIVE_MESSAGE = 0;
	public static final int SEND_MESSAGE = 1;
	public static final int LOG_MESSAGE = 2;

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE messages(time varchar(10),fromaddress varchar(10),type int,msg varchar(10))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public static void insertMsgToDatabase(int type, String fromAddress, String message, String time) {
		DatabaseHelper dbHelper = new DatabaseHelper(MyApp.getContext(), "database", null, 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("time", time);
		values.put("fromaddress", fromAddress);
		values.put("type", type);
		values.put("msg", message);
		db.insert("messages", null, values);
		db.close();
	}
}