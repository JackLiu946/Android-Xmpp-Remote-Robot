package com.dary.xmpp.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dary.xmpp.R;

public class LogActivity extends Activity {

	private TextView textViewLog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log);
		textViewLog = (TextView) findViewById(R.id.textviewlog);
		textViewLog.setTypeface(Typeface.MONOSPACE);
		textViewLog.setTextSize(12);
	}

	@Override
	protected void onStart() {

		StringBuilder sb = new StringBuilder();
		File file = new File("/data/data/com.dary.xmpp/files/Log");
		try {
			InputStream is = new FileInputStream(file);
			byte[] buffer = new byte[200];
			int length = 0;
			while (-1 != (length = is.read(buffer))) {
				String str = new String(buffer, 0, length);
				sb.append(str);
			}
			is.close();
			textViewLog.setText(sb.toString());

		} catch (Exception e) {
			textViewLog.setText("Log dose not exist");
		}
		super.onStart();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.clear_log);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0:
			File file = new File("/data/data/com.dary.xmpp/files/Log");
			if (file.exists())
				file.delete();
			textViewLog.setText("Log dose not exist");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
