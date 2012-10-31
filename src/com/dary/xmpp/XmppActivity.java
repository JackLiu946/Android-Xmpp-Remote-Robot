package com.dary.xmpp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class XmppActivity extends Activity {

	public TextView loginStatus;
	private AutoCompleteTextView autoCompleteTextViewSendMessage;
	public static Handler MsgHandler = null;
	public static TextView TVmessage;
	public static XmppActivity xmppactivity;
	public static SurfaceView surfaceview;

	public static final int LOGIN_SUCCESSFUL = 0;
	public static final int LOGIN_FAILED = 1;
	public static final int CONNECTION_FAILED = 6;
	public static final int LOGGING = 2;
	public static final int RECEIVE_MESSAGE = 3;
	public static final int SEND_MESSAGE = 4;
	public static final int SET_INCOMPLETE = 5;

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		xmppactivity = XmppActivity.this;
		final Button buttonServiceStart = (Button) findViewById(R.id.servicestart);
		final Button buttonServiceStop = (Button) findViewById(R.id.servicestop);
		final ScrollView scrollViewMessage = (ScrollView) findViewById(R.id.scrollviewmessage);
		loginStatus = (TextView) findViewById(R.id.loginstatus);
		final LinearLayout linearLayoutMessage = (LinearLayout) findViewById(R.id.linearlayoutmessage);
		final Button buttonSendMessage = (Button) findViewById(R.id.buttonsendmessage);
		autoCompleteTextViewSendMessage = (AutoCompleteTextView) findViewById(R.id.autocompletetextviewsendmessage);
		// 拍照相关
		surfaceview = (SurfaceView) findViewById(R.id.sv);

		// 设置AutoCompleteTextView的Adapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.auto_cmd_string_item, getResources().getStringArray(R.array.autoCmdString));
		autoCompleteTextViewSendMessage.setAdapter(adapter);

		// 一开始要关掉 关闭服务和发送消息 的按钮
		buttonServiceStop.setEnabled(false);
		buttonSendMessage.setEnabled(false);
		// 判断Activity未创建之前服务是否已启动.用于判断自动启动.
		if (MainService.isloginin == true) {
			// 登录成功后禁用启动服务的按钮
			buttonServiceStart.setEnabled(false);
			// 修改TextView的信息去提示用户.
			loginStatus.setText(R.string.loginstatus_successful);
			loginStatus.setTextColor(Color.GREEN);
			// 启动关闭服务的按钮
			buttonServiceStop.setEnabled(true);
			buttonSendMessage.setEnabled(true);
		}

		// 更新显示收到的消息
		MsgHandler = new Handler() {
			public void handleMessage(Message msg) {

				if (msg.what == LOGIN_SUCCESSFUL) {
					// 登录成功后禁用启动服务的按钮
					buttonServiceStart.setEnabled(false);
					// 修改TextView的信息去提示用户.
					loginStatus.setText(R.string.loginstatus_successful);
					loginStatus.setTextColor(Color.GREEN);
					// 启动关闭服务的按钮
					buttonServiceStop.setEnabled(true);
					buttonSendMessage.setEnabled(true);
				}

				// 登录失败
				if (msg.what == LOGIN_FAILED) {
					buttonServiceStart.setEnabled(true);
					// Tools.Vibrator(XmppActivity.this, 1000);
					loginStatus.setText(R.string.loginstatus_login_failure);
					loginStatus.setTextColor(Color.RED);
				}

				// 连接失败
				if (msg.what == CONNECTION_FAILED) {
					buttonServiceStart.setEnabled(true);
					// Tools.Vibrator(XmppActivity.this, 1000);
					loginStatus.setText(R.string.loginstatus_connection_failure);
					loginStatus.setTextColor(Color.RED);
				}

				// 登录中
				if (msg.what == LOGGING) {
					loginStatus.setTextColor(Color.YELLOW);
					loginStatus.setText(R.string.loginstatus_logging);
				}

				// 接受的消息
				if (msg.what == RECEIVE_MESSAGE) {
					TextView receiveMessage = new TextView(XmppActivity.this);
					receiveMessage.setText(Tools.getTimeStr() + "\n" + msg.getData().getString("msg") + "\n");
					receiveMessage.setTextColor(Color.YELLOW);
					linearLayoutMessage.addView(receiveMessage);
					// 将ScrollView滚动到底部
					scrollToBottom(scrollViewMessage, linearLayoutMessage);
				}

				// 程序自己发送出去的消息
				if (msg.what == SEND_MESSAGE) {
					TextView sendMessage = new TextView(XmppActivity.this);
					// 这里由于发送回去的消息有可能是多行,使用换行
					sendMessage.setText(Tools.getTimeStr() + "\n" + msg.getData().getString("msg") + "\n");
					sendMessage.setTextColor(Color.GREEN);
					linearLayoutMessage.addView(sendMessage);
					// 将ScrollView滚动到底部
					scrollToBottom(scrollViewMessage, linearLayoutMessage);
				}

				// 配置不全
				if (msg.what == SET_INCOMPLETE) {
					buttonServiceStart.setEnabled(true);
					loginStatus.setText(R.string.loginstatus_set_incomplete);
					loginStatus.setTextColor(Color.RED);
				}
			}

		};

		// 服务启动按钮
		buttonServiceStart.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				buttonServiceStart.setEnabled(false);
				Tools.Vibrator(XmppActivity.this, 100);
				Intent mainserviceIntent = new Intent();
				mainserviceIntent.setClass(XmppActivity.this, MainService.class);
				startService(mainserviceIntent);
			}
		});
		// 服务停止的按钮
		buttonServiceStop.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Tools.Vibrator(XmppActivity.this, 100);

				loginStatus.setText(R.string.loginstatus_not_logged_in);
				loginStatus.setTextColor(Color.GRAY);

				buttonSendMessage.setEnabled(false);
				// 启动两个服务
				Intent mainserviceIntent = new Intent();
				mainserviceIntent.setClass(XmppActivity.this, MainService.class);
				stopService(mainserviceIntent);
				Intent incallserviceIntent = new Intent();
				incallserviceIntent.setClass(XmppActivity.this, IncallService.class);
				stopService(incallserviceIntent);
				buttonServiceStart.setEnabled(true);
				buttonServiceStop.setEnabled(false);
				// 移除LinearLayout上的所有TextView
				linearLayoutMessage.removeAllViewsInLayout();
			}
		});

		// 发送按钮
		buttonSendMessage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (autoCompleteTextViewSendMessage.getText().toString() != "") {
					SendMessageAndUpdateView.sendMessageAndUpdateView(MainService.chat, autoCompleteTextViewSendMessage.getText().toString());
					autoCompleteTextViewSendMessage.setText("");
				}
			}
		});

		// 设置点击即显示下拉列表
		// autoCompleteTextViewSendMessage.setOnClickListener(new
		// OnClickListener() {
		//
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// autoCompleteTextViewSendMessage.showDropDown();
		// }
		// });

		// 如果是测试模式,则自动登录
		boolean isDebugMode = getApplicationContext().getSharedPreferences("com.dary.xmpp_preferences", 1).getBoolean("isDebugMode", false);
		if (isDebugMode && MainService.isloginin != true) {
			Intent mainserviceIntent = new Intent();
			mainserviceIntent.setClass(XmppActivity.this, MainService.class);
			startService(mainserviceIntent);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.settings);
		menu.add(0, 1, 1, R.string.about);
		menu.add(0, 2, 2, R.string.exit);
		menu.add(0, 3, 3, R.string.log);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0:
			Intent intent = new Intent();
			intent.setClass(XmppActivity.this, PreferenceActivity.class);
			startActivity(intent);
			break;
		case 1:
			// int类型的引用,不能直接相加
			new AlertDialog.Builder(XmppActivity.this).setTitle(R.string.about).setMessage(getResources().getString(R.string.app_name) + "\n\n" + getResources().getString(R.string.google_code)).setPositiveButton("OK", null).show();
			break;
		case 2:
			finish();
			break;
		case 3:
			Intent intent2 = new Intent();
			intent2.setClass(XmppActivity.this, LogActivity.class);
			startActivity(intent2);
		}
		return super.onOptionsItemSelected(item);
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// XmppActivity.this.finish();
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

	// 获取shareText
	@Override
	protected void onResume() {
		autoCompleteTextViewSendMessage.clearFocus();
		Intent intent = getIntent();
		String shareText = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (shareText != "") {
			autoCompleteTextViewSendMessage.setText(shareText);
		}
		super.onResume();
	}

}