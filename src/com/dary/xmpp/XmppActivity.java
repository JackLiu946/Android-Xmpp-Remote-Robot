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
		// �������
		surfaceview = (SurfaceView) findViewById(R.id.sv);

		// ����AutoCompleteTextView��Adapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.auto_cmd_string_item, getResources().getStringArray(R.array.autoCmdString));
		autoCompleteTextViewSendMessage.setAdapter(adapter);

		// һ��ʼҪ�ص� �رշ���ͷ�����Ϣ �İ�ť
		buttonServiceStop.setEnabled(false);
		buttonSendMessage.setEnabled(false);
		// �ж�Activityδ����֮ǰ�����Ƿ�������.�����ж��Զ�����.
		if (MainService.isloginin == true) {
			// ��¼�ɹ��������������İ�ť
			buttonServiceStart.setEnabled(false);
			// �޸�TextView����Ϣȥ��ʾ�û�.
			loginStatus.setText(R.string.loginstatus_successful);
			loginStatus.setTextColor(Color.GREEN);
			// �����رշ���İ�ť
			buttonServiceStop.setEnabled(true);
			buttonSendMessage.setEnabled(true);
		}

		// ������ʾ�յ�����Ϣ
		MsgHandler = new Handler() {
			public void handleMessage(Message msg) {

				if (msg.what == LOGIN_SUCCESSFUL) {
					// ��¼�ɹ��������������İ�ť
					buttonServiceStart.setEnabled(false);
					// �޸�TextView����Ϣȥ��ʾ�û�.
					loginStatus.setText(R.string.loginstatus_successful);
					loginStatus.setTextColor(Color.GREEN);
					// �����رշ���İ�ť
					buttonServiceStop.setEnabled(true);
					buttonSendMessage.setEnabled(true);
				}

				// ��¼ʧ��
				if (msg.what == LOGIN_FAILED) {
					buttonServiceStart.setEnabled(true);
					// Tools.Vibrator(XmppActivity.this, 1000);
					loginStatus.setText(R.string.loginstatus_login_failure);
					loginStatus.setTextColor(Color.RED);
				}

				// ����ʧ��
				if (msg.what == CONNECTION_FAILED) {
					buttonServiceStart.setEnabled(true);
					// Tools.Vibrator(XmppActivity.this, 1000);
					loginStatus.setText(R.string.loginstatus_connection_failure);
					loginStatus.setTextColor(Color.RED);
				}

				// ��¼��
				if (msg.what == LOGGING) {
					loginStatus.setTextColor(Color.YELLOW);
					loginStatus.setText(R.string.loginstatus_logging);
				}

				// ���ܵ���Ϣ
				if (msg.what == RECEIVE_MESSAGE) {
					TextView receiveMessage = new TextView(XmppActivity.this);
					receiveMessage.setText(Tools.getTimeStr() + "\n" + msg.getData().getString("msg") + "\n");
					receiveMessage.setTextColor(Color.YELLOW);
					linearLayoutMessage.addView(receiveMessage);
					// ��ScrollView�������ײ�
					scrollToBottom(scrollViewMessage, linearLayoutMessage);
				}

				// �����Լ����ͳ�ȥ����Ϣ
				if (msg.what == SEND_MESSAGE) {
					TextView sendMessage = new TextView(XmppActivity.this);
					// �������ڷ��ͻ�ȥ����Ϣ�п����Ƕ���,ʹ�û���
					sendMessage.setText(Tools.getTimeStr() + "\n" + msg.getData().getString("msg") + "\n");
					sendMessage.setTextColor(Color.GREEN);
					linearLayoutMessage.addView(sendMessage);
					// ��ScrollView�������ײ�
					scrollToBottom(scrollViewMessage, linearLayoutMessage);
				}

				// ���ò�ȫ
				if (msg.what == SET_INCOMPLETE) {
					buttonServiceStart.setEnabled(true);
					loginStatus.setText(R.string.loginstatus_set_incomplete);
					loginStatus.setTextColor(Color.RED);
				}
			}

		};

		// ����������ť
		buttonServiceStart.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				buttonServiceStart.setEnabled(false);
				Tools.Vibrator(XmppActivity.this, 100);
				Intent mainserviceIntent = new Intent();
				mainserviceIntent.setClass(XmppActivity.this, MainService.class);
				startService(mainserviceIntent);
			}
		});
		// ����ֹͣ�İ�ť
		buttonServiceStop.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Tools.Vibrator(XmppActivity.this, 100);

				loginStatus.setText(R.string.loginstatus_not_logged_in);
				loginStatus.setTextColor(Color.GRAY);

				buttonSendMessage.setEnabled(false);
				// ������������
				Intent mainserviceIntent = new Intent();
				mainserviceIntent.setClass(XmppActivity.this, MainService.class);
				stopService(mainserviceIntent);
				Intent incallserviceIntent = new Intent();
				incallserviceIntent.setClass(XmppActivity.this, IncallService.class);
				stopService(incallserviceIntent);
				buttonServiceStart.setEnabled(true);
				buttonServiceStop.setEnabled(false);
				// �Ƴ�LinearLayout�ϵ�����TextView
				linearLayoutMessage.removeAllViewsInLayout();
			}
		});

		// ���Ͱ�ť
		buttonSendMessage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (autoCompleteTextViewSendMessage.getText().toString() != "") {
					SendMessageAndUpdateView.sendMessageAndUpdateView(MainService.chat, autoCompleteTextViewSendMessage.getText().toString());
					autoCompleteTextViewSendMessage.setText("");
				}
			}
		});

		// ���õ������ʾ�����б�
		// autoCompleteTextViewSendMessage.setOnClickListener(new
		// OnClickListener() {
		//
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// autoCompleteTextViewSendMessage.showDropDown();
		// }
		// });

		// ����ǲ���ģʽ,���Զ���¼
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
			// int���͵�����,����ֱ�����
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

	// �������ײ�
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

	// ��ȡshareText
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