package com.dary.xmpp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.dary.xmpp.R;
import com.dary.xmpp.application.MyApp;
import com.dary.xmpp.cmd.CmdBase;
import com.dary.xmpp.databases.DatabaseHelper;
import com.dary.xmpp.service.MainService;
import com.dary.xmpp.tools.Tools;
import com.dary.xmpp.xmpp.MsgListener;

public class MainActivity extends Activity {

	@SuppressWarnings("deprecation")
	public static android.text.ClipboardManager clipboardManager;
	private TextView loginStatus;
	private AutoCompleteTextView autoCompleteTextViewSendMessage;
	private Button buttonServiceStart;
	private Button buttonServiceStop;
	private Button buttonSendMessage;
	private ScrollView scrollViewMessage;
	private LinearLayout linearLayoutMessage;
	public static Handler MsgHandler = null;
	public static SurfaceView surfaceview;

	public static final int DEBUG = -1;
	public static final int NOT_LOGGED_IN = 0;
	public static final int LOGGING = 1;
	public static final int LOGIN_SUCCESSFUL = 2;
	public static final int SET_INCOMPLETE = 3;
	public static final int CONNECTION_FAILED = 4;
	public static final int LOGIN_FAILED = 5;
	public static final int NOTIFIED_ADDRESS_IS_NOT_IN_FRIEND_LIST = 6;

	public static final int SHOW_MESSAGE = 7;
	public static final int CLEAR_MSG = 8;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		clipboardManager = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
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
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.auto_cmd_string_item, getResources().getStringArray(R.array.autoSendCmdString));
		autoCompleteTextViewSendMessage.setAdapter(adapter);

		autoCompleteTextViewSendMessage.setOnEditorActionListener(new OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				sendMsg();
				return false;
			}
		});

		// 更新显示收到的消息
		MsgHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 要展示的消息
				if (msg.what == SHOW_MESSAGE) {
					MsgView mv = new MsgView(MainActivity.this, msg.getData().getInt("type"), msg.getData().getString("fromaddress"), msg.getData().getString("time"), msg.getData().getString("msg"));
					linearLayoutMessage.addView(mv);
					// 将ScrollView滚动到底部
					scrollToBottom(scrollViewMessage, linearLayoutMessage);
				} else if (msg.what == CLEAR_MSG) {
					clearMsg();
				}
				// 除此之外,仅须改变设置View状态
				else {
					setViewByStatus(msg.what);
				}
			}
		};

		// 服务启动按钮
		buttonServiceStart.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Tools.Vibrator(MainActivity.this, 100);
				Intent mainserviceIntent = new Intent();
				mainserviceIntent.setClass(MainActivity.this, MainService.class);
				startService(mainserviceIntent);
			}
		});
		// 长按按钮,进入Debug模式
		buttonServiceStart.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View v) {
				Tools.Vibrator(MainActivity.this, 500);
				MyApp myApp = (MyApp) MyApp.getContext();
				myApp.setStatus(MainActivity.DEBUG);
				Message msg = new Message();
				msg.what = MainActivity.DEBUG;
				MainActivity.MsgHandler.sendMessage(msg);
				return false;
			}
		});
		// 服务停止的按钮
		buttonServiceStop.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Tools.Vibrator(MainActivity.this, 100);
				MyApp myApp = (MyApp) MyApp.getContext();

				if (myApp.getStatus() == DEBUG) {
					MainService.sendMsg(MainActivity.NOT_LOGGED_IN);
				}
				// 正在登录,登录成功时则停止掉服务,连接/登录失败的时候,也应该可以去停止程序以后的自动登录.
				else {
					// 停止服务
					Intent mainserviceIntent = new Intent();
					mainserviceIntent.setClass(MainActivity.this, MainService.class);
					stopService(mainserviceIntent);
				}
			}
		});

		// 发送按钮
		buttonSendMessage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendMsg();
			}
		});

		// 设置点击即显示下拉列表
		// autoCompleteTextViewSendMessage.setOnClickListener(new
		// OnClickListener() {
		// public void onClick(View v) {
		// autoCompleteTextViewSendMessage.showDropDown();
		// }
		// });

		// 如果是测试模式,则自动登录
		boolean isDebugMode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isDebugMode", false);
		if (isDebugMode) {
			Intent mainserviceIntent = new Intent();
			mainserviceIntent.setClass(MainActivity.this, MainService.class);
			startService(mainserviceIntent);
		}

		// 读取数据库,创建MsgView
		readDatabaseAndCreateMsgView();
	}

	// 这里是指发送xmpp消息
	public void sendMsg() {
		if (autoCompleteTextViewSendMessage.getText().toString().trim().length() != 0) {
			MyApp myApp = (MyApp) getApplication();
			if (myApp.getStatus() == MainActivity.DEBUG) {
				MsgListener.handleMessage(null, autoCompleteTextViewSendMessage.getText().toString());
			} else if (myApp.getStatus() == MainActivity.LOGIN_SUCCESSFUL) {
				CmdBase.sendMessageAndUpdateView(MainService.chat, autoCompleteTextViewSendMessage.getText().toString());
			}
			autoCompleteTextViewSendMessage.setText("");
		}
	}

	@Override
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
			preferenceIntent.setClass(MainActivity.this, PreferencesActivity.class);
			startActivity(preferenceIntent);
			break;
		case 1:
			View view = View.inflate(MainActivity.this, R.layout.about, null);
			TextView tv = (TextView) view.findViewById(R.id.text_about);
			tv.setText(getResources().getString(R.string.author) + getResources().getString(R.string.author_value) + "\n" + getResources().getString(R.string.email) + getResources().getString(R.string.email_value) + "\n" + getResources().getString(R.string.version) + Tools.getAppVersionName(MainActivity.this) + "\n" + getResources().getString(R.string.find_more) + "\n" + getResources().getString(R.string.github));
			new AlertDialog.Builder(MainActivity.this).setTitle(R.string.app_name).setView(view).setPositiveButton(R.string.ok, null).setIcon(R.drawable.ic_launcher).show();
			break;
		case 2:
			clearMsg();
			break;
		case 3:
			Intent logIntent = new Intent();
			logIntent.setClass(MainActivity.this, LogActivity.class);
			startActivity(logIntent);
		}
		return super.onOptionsItemSelected(item);
	}

	// 移除所有MsgView并清空数据库
	public void clearMsg() {
		// 移除LinearLayout上的所有TextView
		linearLayoutMessage.removeAllViews();
		// 清空表
		DatabaseHelper dbHelper = new DatabaseHelper(MyApp.getContext(), "database", null, 1);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.delete("messages", null, null);
		db.close();
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
			if (myApp.getStatus() == MainActivity.LOGIN_SUCCESSFUL) {
				CmdBase.sendMessageAndUpdateView(MainService.chat, shareText);
			} else {
				autoCompleteTextViewSendMessage.setText(shareText);
			}
		}
		super.onResume();
	}

	private void setViewByStatus(int s) {
		switch (s) {
		case DEBUG:
			buttonServiceStart.setEnabled(false);
			buttonServiceStop.setEnabled(true);
			buttonSendMessage.setEnabled(true);
			loginStatus.setTextColor(Color.BLUE);
			loginStatus.setText(R.string.loginstats_debug);
			// 对于Debug模式,要修改autoCompleteTextViewSendMessage的adapter
			ArrayAdapter<String> autoDebugCmdStringAdapter = new ArrayAdapter<String>(this, R.layout.auto_cmd_string_item, getResources().getStringArray(R.array.autoDebugCmdString));
			autoCompleteTextViewSendMessage.setAdapter(autoDebugCmdStringAdapter);
			break;
		case NOT_LOGGED_IN:
			buttonServiceStart.setEnabled(true);
			buttonServiceStop.setEnabled(false);
			buttonSendMessage.setEnabled(false);
			loginStatus.setTextColor(Color.GRAY);
			loginStatus.setText(R.string.loginstatus_not_logged_in);
			break;
		case LOGGING:
			buttonServiceStart.setEnabled(false);
			buttonServiceStop.setEnabled(true);
			buttonSendMessage.setEnabled(false);
			loginStatus.setTextColor(Color.YELLOW);
			loginStatus.setText(R.string.loginstatus_logging);
			// 对于Debug模式,要修改autoCompleteTextViewSendMessage的adapter
			ArrayAdapter<String> autoSendCmdStringAdapter = new ArrayAdapter<String>(this, R.layout.auto_cmd_string_item, getResources().getStringArray(R.array.autoSendCmdString));
			autoCompleteTextViewSendMessage.setAdapter(autoSendCmdStringAdapter);
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
		case NOTIFIED_ADDRESS_IS_NOT_IN_FRIEND_LIST:
			buttonServiceStart.setEnabled(true);
			buttonServiceStop.setEnabled(false);
			buttonSendMessage.setEnabled(false);
			loginStatus.setText(R.string.loginstatus_notified_address_is_not_in_friend_list);
			loginStatus.setTextColor(Color.RED);
			break;
		case CONNECTION_FAILED:
			buttonServiceStart.setEnabled(true);
			buttonServiceStop.setEnabled(true);
			buttonSendMessage.setEnabled(false);
			// Tools.Vibrator(XmppActivity.this, 1000);
			loginStatus.setText(R.string.loginstatus_connection_failure);
			loginStatus.setTextColor(Color.RED);
			break;
		case LOGIN_FAILED:
			buttonServiceStart.setEnabled(true);
			buttonServiceStop.setEnabled(true);
			buttonSendMessage.setEnabled(false);
			// Tools.Vibrator(XmppActivity.this, 1000);
			loginStatus.setText(R.string.loginstatus_login_failure);
			loginStatus.setTextColor(Color.RED);
			break;
		}
	}

	// 读取数据库,创建MsgView
	private void readDatabaseAndCreateMsgView() {
		DatabaseHelper dbHelper = new DatabaseHelper(MyApp.getContext(), "database", null, 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("messages", new String[] { "time", "fromaddress", "type", "msg" }, null, null, null, null, null);
		while (cursor.moveToNext()) {
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String fromaddress = cursor.getString(cursor.getColumnIndex("fromaddress"));
			int type = cursor.getInt(cursor.getColumnIndex("type"));
			String msg = cursor.getString(cursor.getColumnIndex("msg"));

			sendHandlerMessageToAddMsgView(type, fromaddress, msg, time);
		}
		db.close();
		cursor.close();
	}

	// 发送Handler,创建MsgView
	public static void sendHandlerMessageToAddMsgView(int type, String fromAddress, String message, String time) {
		if (null != MainActivity.MsgHandler) {
			android.os.Message msg = new android.os.Message();
			msg.what = MainActivity.SHOW_MESSAGE;
			Bundle bundle = new Bundle();
			bundle.putInt("type", type);
			bundle.putString("fromaddress", fromAddress);
			bundle.putString("msg", message);
			bundle.putString("time", time);
			msg.setData(bundle);
			MainActivity.MsgHandler.sendMessage(msg);
		}
	}
}