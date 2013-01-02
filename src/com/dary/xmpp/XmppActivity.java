package com.dary.xmpp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dary.xmpp.cmd.CmdBase;

public class XmppActivity extends Activity {

	public TextView loginStatus;
	private AutoCompleteTextView autoCompleteTextViewSendMessage;
	private Button buttonServiceStart;
	private Button buttonServiceStop;
	private Button buttonSendMessage;
	private ScrollView scrollViewMessage;
	private LinearLayout linearLayoutMessage;
	public static Handler MsgHandler = null;
	public static TextView TVmessage;
	public static SurfaceView surfaceview;

	public static final int NOT_LOGGED_IN = 0;
	public static final int LOGGING = 1;
	public static final int LOGIN_SUCCESSFUL = 2;
	public static final int SET_INCOMPLETE = 3;
	public static final int CONNECTION_FAILED = 4;
	public static final int LOGIN_FAILED = 5;
	public static final int RECEIVE_MESSAGE = 6;
	public static final int SEND_MESSAGE = 7;

	public static final int RECEIVE_MESSAGE_DATABASE = 0;
	public static final int SEND_MESSAGE_DATABASE = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buttonServiceStart = (Button) findViewById(R.id.servicestart);
		buttonServiceStop = (Button) findViewById(R.id.servicestop);
		loginStatus = (TextView) findViewById(R.id.loginstatus);
		scrollViewMessage = (ScrollView) findViewById(R.id.scrollviewmessage);
		linearLayoutMessage = (LinearLayout) findViewById(R.id.linearlayoutmessage);
		buttonSendMessage = (Button) findViewById(R.id.buttonsendmessage);
		autoCompleteTextViewSendMessage = (AutoCompleteTextView) findViewById(R.id.autocompletetextviewsendmessage);
		// 拍照相关
		surfaceview = (SurfaceView) findViewById(R.id.sv);
		// 设置AutoCompleteTextView的Adapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.auto_cmd_string_item, getResources().getStringArray(R.array.autoCmdString));
		autoCompleteTextViewSendMessage.setAdapter(adapter);

		// 更新显示收到的消息
		MsgHandler = new Handler() {
			public void handleMessage(Message msg) {

				if (msg.what == LOGIN_SUCCESSFUL) {
					setViewByStatus(LOGIN_SUCCESSFUL);
				} else if (msg.what == SET_INCOMPLETE) {
					setViewByStatus(SET_INCOMPLETE);
				} else if (msg.what == LOGIN_FAILED) {
					setViewByStatus(LOGIN_FAILED);
				} else if (msg.what == CONNECTION_FAILED) {
					setViewByStatus(CONNECTION_FAILED);
				} else if (msg.what == LOGGING) {
					setViewByStatus(LOGGING);
				} else if (msg.what == NOT_LOGGED_IN) {
					setViewByStatus(NOT_LOGGED_IN);
				}

				// 接受的消息
				else if (msg.what == RECEIVE_MESSAGE) {
					MsgView mv = new MsgView(XmppActivity.this, MsgView.RECEIVE, msg.getData().getString("fromaddress"), msg.getData().getString("time"), msg.getData().getString("msg"));
					linearLayoutMessage.addView(mv);
					// 将ScrollView滚动到底部
					scrollToBottom(scrollViewMessage, linearLayoutMessage);
				}

				// 程序自己发送出去的消息
				else if (msg.what == SEND_MESSAGE) {
					MsgView mv = new MsgView(XmppActivity.this, MsgView.SEND, msg.getData().getString("fromaddress"), msg.getData().getString("time"), msg.getData().getString("msg"));
					linearLayoutMessage.addView(mv);
					// 将ScrollView滚动到底部
					scrollToBottom(scrollViewMessage, linearLayoutMessage);
				}
			}
		};

		// 读取数据库,创建MsgView
		createMsgView();

		// 服务启动按钮
		buttonServiceStart.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Tools.Vibrator(XmppActivity.this, 100);
				Intent mainserviceIntent = new Intent();
				mainserviceIntent.setClass(XmppActivity.this, MainService.class);
				startService(mainserviceIntent);
				MainService.myApp.setIsShouldRunning(true);
			}
		});
		// 服务停止的按钮
		buttonServiceStop.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Tools.Vibrator(XmppActivity.this, 100);

				// 启动两个服务
				Intent mainserviceIntent = new Intent();
				mainserviceIntent.setClass(XmppActivity.this, MainService.class);
				stopService(mainserviceIntent);
				Intent incallserviceIntent = new Intent();
				incallserviceIntent.setClass(XmppActivity.this, IncallService.class);
				stopService(incallserviceIntent);
				MainService.myApp.setIsShouldRunning(false);
			}
		});

		// 发送按钮
		buttonSendMessage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (autoCompleteTextViewSendMessage.getText().toString() != "") {
					CmdBase.sendMessageAndUpdateView(MainService.chat, autoCompleteTextViewSendMessage.getText().toString());
					autoCompleteTextViewSendMessage.setText("");
				}
			}
		});

		// 设置点击即显示下拉列表
		// autoCompleteTextViewSendMessage.setOnClickListener(new
		// OnClickListener() {
		//
		// public void onClick(View v) {
		// autoCompleteTextViewSendMessage.showDropDown();
		// }
		// });

		// 如果是测试模式,则自动登录
		boolean isDebugMode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isDebugMode", false);
		if (isDebugMode) {
			Intent mainserviceIntent = new Intent();
			mainserviceIntent.setClass(XmppActivity.this, MainService.class);
			startService(mainserviceIntent);
		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.settings);
		menu.add(0, 1, 1, R.string.about);
		menu.add(0, 2, 2, R.string.clear_msg);
		menu.add(0, 3, 3, R.string.log);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0:
			Intent preferenceIntent = new Intent();
			preferenceIntent.setClass(XmppActivity.this, PreferenceActivity.class);
			startActivity(preferenceIntent);
			break;
		case 1:
			View view = View.inflate(XmppActivity.this, R.layout.about, null);
			TextView tv = (TextView) view.findViewById(R.id.text_about);
			tv.setText(getResources().getString(R.string.author) + getResources().getString(R.string.author_value) + "\n" + getResources().getString(R.string.email) + getResources().getString(R.string.email_value) + "\n" + getResources().getString(R.string.version) + Tools.getAppVersionName(XmppActivity.this) + "\n" + getResources().getString(R.string.find_more) + "\n" + getResources().getString(R.string.github));
			new AlertDialog.Builder(XmppActivity.this).setTitle(R.string.app_name).setView(view).setPositiveButton(R.string.ok, null).setIcon(R.drawable.ic_launcher).show();
			break;
		case 2:
			// 移除LinearLayout上的所有TextView
			linearLayoutMessage.removeAllViews();
			// 清空表
			DatabaseHelper dbHelper = new DatabaseHelper(MyApp.getContext(), "database", null, 1);
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			db.delete("messages", null, null);
			db.close();
			break;
		case 3:
			Intent logIntent = new Intent();
			logIntent.setClass(XmppActivity.this, LogActivity.class);
			startActivity(logIntent);
		}
		return super.onOptionsItemSelected(item);
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// moveTaskToBack(true);
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	// 滚动到底部
	private static void scrollToBottom(final View scroll, final View inner) {
		Handler mHandler = new Handler();
		mHandler.post(new Runnable() {
			public void run() {
				if (scroll == null || inner == null) {
					return;
				}
				int offset = inner.getMeasuredHeight() - scroll.getHeight();
				if (offset < 0) {
					offset = 0;
				}
				scroll.scrollTo(0, offset);
			}
		});
	}

	@Override
	protected void onResume() {

		autoCompleteTextViewSendMessage.clearFocus();
		MyApp myApp = (MyApp) getApplication();
		setViewByStatus(myApp.getStatus());

		// 获取shareText
		Intent intent = getIntent();
		String shareText = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (shareText != null) {
			// 如果登录成功则直接发送消息
			if (myApp.getStatus() == XmppActivity.LOGIN_SUCCESSFUL) {
				CmdBase.sendMessageAndUpdateView(MainService.chat, shareText);
			} else {
				autoCompleteTextViewSendMessage.setText(shareText);
			}
		}
		super.onResume();
	}

	private void setViewByStatus(int s) {
		switch (s) {
		case NOT_LOGGED_IN:
			buttonServiceStart.setEnabled(true);
			buttonServiceStop.setEnabled(false);
			buttonSendMessage.setEnabled(false);
			loginStatus.setTextColor(Color.GRAY);
			loginStatus.setText(R.string.loginstatus_not_logged_in);
			break;
		case LOGGING:
			buttonServiceStart.setEnabled(false);
			buttonServiceStop.setEnabled(false);
			buttonSendMessage.setEnabled(false);
			loginStatus.setTextColor(Color.YELLOW);
			loginStatus.setText(R.string.loginstatus_logging);
			break;
		case LOGIN_SUCCESSFUL:
			buttonServiceStart.setEnabled(false);
			buttonServiceStop.setEnabled(true);
			buttonSendMessage.setEnabled(true);
			loginStatus.setText(R.string.loginstatus_successful);
			loginStatus.setTextColor(Color.GREEN);
			break;
		case SET_INCOMPLETE:
			buttonServiceStart.setEnabled(true);
			buttonServiceStop.setEnabled(false);
			buttonSendMessage.setEnabled(false);
			loginStatus.setText(R.string.loginstatus_set_incomplete);
			loginStatus.setTextColor(Color.RED);
			break;
		case CONNECTION_FAILED:
			buttonServiceStart.setEnabled(true);
			buttonServiceStop.setEnabled(false);
			buttonSendMessage.setEnabled(false);
			// Tools.Vibrator(XmppActivity.this, 1000);
			loginStatus.setText(R.string.loginstatus_connection_failure);
			loginStatus.setTextColor(Color.RED);
			break;
		case LOGIN_FAILED:
			buttonServiceStart.setEnabled(true);
			buttonSendMessage.setEnabled(false);
			buttonServiceStop.setEnabled(false);
			// Tools.Vibrator(XmppActivity.this, 1000);
			loginStatus.setText(R.string.loginstatus_login_failure);
			loginStatus.setTextColor(Color.RED);
			break;
		}
	}

	// 读取数据库,创建MsgView
	private void createMsgView() {
		DatabaseHelper dbHelper = new DatabaseHelper(MyApp.getContext(), "database", null, 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("messages", new String[] { "time", "fromaddress", "type", "msg" }, null, null, null, null, null);
		while (cursor.moveToNext()) {
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String fromaddress = cursor.getString(cursor.getColumnIndex("fromaddress"));
			int type = cursor.getInt(cursor.getColumnIndex("type"));
			String msg = cursor.getString(cursor.getColumnIndex("msg"));

			android.os.Message handleMsg = new android.os.Message();
			handleMsg.what = type == XmppActivity.RECEIVE_MESSAGE_DATABASE ? XmppActivity.RECEIVE_MESSAGE : XmppActivity.SEND_MESSAGE;
			Bundle bundle = new Bundle();
			bundle.putString("time", time);
			bundle.putString("fromaddress", fromaddress);
			bundle.putString("msg", msg);
			handleMsg.setData(bundle);
			XmppActivity.MsgHandler.sendMessage(handleMsg);
		}
		db.close();
	}
}