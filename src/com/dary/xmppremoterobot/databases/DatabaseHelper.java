
package com.dary.xmppremoterobot.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.dary.xmppremoterobot.application.MyApp;
import com.dary.xmppremoterobot.ui.MainActivity;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int RECEIVE_MESSAGE = 0;
    public static final int SEND_MESSAGE = 1;
    public static final int LOG_MESSAGE = 2;

    private static final int MAX_MSG_NUM = 30;

    public static final String DATABASE = "database";
    public static final String TABLE = "messages";

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE
                + "(time long,fromaddress varchar(10),type int,msg varchar(10))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void insertMsgToDatabase(int type, String fromAddress, String message, long time) {
        DatabaseHelper dbHelper = new DatabaseHelper(MyApp.getContext(), DATABASE, null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor oldc = db.query(TABLE, null, null, null, null, null, null);
        // 删除数据库中N条数据
        int oldCount = oldc.getCount();
        if (oldCount >= MAX_MSG_NUM) {
            Cursor c;
            do {
                c = db.query(TABLE, null, null, null, null, null, null);
                long firsttime = 0;
                if (c.moveToFirst()) {
                    firsttime = c.getLong(c.getColumnIndex("time"));
                    db.delete(TABLE, "time = " + firsttime, null);

                    // 删除view
                    if (null != MainActivity.MsgHandler) {
                        MainActivity.MsgHandler.sendEmptyMessage(MainActivity.REMOVE_MESSAGE);
                    }

                }
            } while (c.getCount() != MAX_MSG_NUM);
            c.close();
        }
        oldc.close();
        ContentValues values = new ContentValues();
        values.put("time", time);
        values.put("fromaddress", fromAddress);
        values.put("type", type);
        values.put("msg", message);
        db.insert(TABLE, null, values);
        db.close();
    }
}
