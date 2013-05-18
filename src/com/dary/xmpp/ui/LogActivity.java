package com.dary.xmpp.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;

import com.dary.xmpp.MyApp;
import com.dary.xmpp.R;

public class LogActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SimpleAdapter adapter = new SimpleAdapter(LogActivity.this, getData(), R.layout.log, new String[] { "log", "time" }, new int[] { R.id.TVlog,
				R.id.TVtime });
		setListAdapter(adapter);
	}

	private List<Map<String, String>> getData() {
		StringBuilder sb = new StringBuilder();
		File cacheDir = MyApp.getContext().getCacheDir();
		File logFile = new File(cacheDir, "Log");
		InputStream is;
		try {
			is = new FileInputStream(logFile);

			byte[] buffer = new byte[200];
			int length = 0;
			while (-1 != (length = is.read(buffer))) {
				String str = new String(buffer, 0, length);
				sb.append(str);
			}
			is.close();
			String logAll = sb.toString();
			String lines[] = logAll.split("\n");

			List<Map<String, String>> list = new ArrayList<Map<String, String>>();

			for (int i = 0; i < lines.length; i++) {
				Map<String, String> map = new HashMap<String, String>();
				String[] line = lines[i].split("\t");
				String log = line[0];
				String time = line[1];
				map.put("log", log);
				map.put("time", time);
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			Map<String, String> map = new HashMap<String, String>();
			map.put("log", "Log dose not exist");
			list.add(map);
			return list;
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.clear_log);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0:
			File cacheDir = MyApp.getContext().getCacheDir();
			File logFile = new File(cacheDir, "Log");
			if (logFile.exists()) {
				if (logFile.delete()) {
					SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.log, new String[] { "log", "time" }, new int[] { R.id.TVlog,
							R.id.TVtime });
					setListAdapter(adapter);
				}
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
