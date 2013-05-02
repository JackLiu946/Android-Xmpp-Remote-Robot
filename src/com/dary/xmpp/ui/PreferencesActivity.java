package com.dary.xmpp.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.R.integer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import com.dary.xmpp.MyApp;
import com.dary.xmpp.R;

public class PreferencesActivity extends android.preference.PreferenceActivity {
	private ListPreference switchPreferences;
	private Preference saveCurrentPreferences;
	private MultiSelectListPreference delSavedPreferences;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		switchPreferences = (ListPreference) findPreference("switchPreferences");
		delSavedPreferences = (MultiSelectListPreference) findPreference("delSavedPreferences");
		setList();
		switchPreferences.setSummary("Current Preferences is " + switchPreferences.getValue());
		saveCurrentPreferences = findPreference("saveCurrentPreferences");
		saveCurrentPreferences.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				View view = View.inflate(PreferencesActivity.this, R.layout.save_preferences, null);
				final EditText ETsavepreferences = (EditText) view.findViewById(R.id.ETsavepreferences);
				new AlertDialog.Builder(PreferencesActivity.this).setTitle("Save Preferences").setView(view)
						.setPositiveButton(R.string.ok, new OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this);
								Map<String, String> prefs = (Map<String, String>) mPrefs.getAll();
								// TODO 文件名不能为空,不能含有分隔符
								String prefsFileName = ETsavepreferences.getText().toString();
								File file = MyApp.getContext().getFileStreamPath(prefsFileName);
								try {
									StringBuilder sb = new StringBuilder();
									sb.append(prefs);
									// 不能为Append模式
									FileOutputStream fos = new FileOutputStream(file, false);
									fos.write(sb.toString().getBytes());
									fos.close();

									switchPreferences.setSummary("Current Preferences is " + prefsFileName);
									switchPreferences.setValue(prefsFileName);
									setList();
								} catch (Exception e) {

								}
							}
						}).setNegativeButton("Cancel", null).setIcon(R.drawable.ic_launcher).show();
				return true;
			}
		});
		switchPreferences.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// 读取配置文件
				File file = MyApp.getContext().getFileStreamPath(newValue.toString());
				if (file.exists()) {

					StringBuilder sb = new StringBuilder();
					InputStream is;
					String prefs = "";
					try {
						is = new FileInputStream(file);

						byte[] buffer = new byte[200];
						int length = 0;
						while (-1 != (length = is.read(buffer))) {
							String str = new String(buffer, 0, length);
							sb.append(str);
						}
						is.close();
						prefs = sb.toString();
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 写入(切换)配置
					Map<String, String> prefsMap = converStringToMap(prefs);
					SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
					Editor e = mPrefs.edit();
					// 不全
					for (Map.Entry<String, String> entry : prefsMap.entrySet()) {
						String key = entry.getKey();
						Map map = mPrefs.getAll();
						// 判断原map的value类型
						boolean isString = map.get(key) instanceof String;
						boolean isInt = map.get(key) instanceof integer;
						boolean isBoolean = map.get(key) instanceof Boolean;
						String value = entry.getValue();
						if (isBoolean) {
							e.putBoolean(key, Boolean.valueOf(value));
						} else if (isInt) {
							e.putInt(key, Integer.parseInt(value));
						} else {
							e.putString(key, value);
						}
					}
					// 配置切换这一项要修改为最新的值
					e.putString("switchPreferences", newValue.toString());
					e.commit();
					onCreate(null);
					return true;
				} else {
					return false;
				}
			}
		});

		delSavedPreferences.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String prefs[] = newValue.toString().split("\\|");
				for (int i = 0; i < prefs.length; i++) {
					File file = MyApp.getContext().getFileStreamPath(prefs[i]);
					if (file.exists()) {
						file.delete();
					}
				}
				setList();
				return false;
			}
		});
	}

	private void setList() {

		String[] subFiles = getFilesDir().list();
		if (subFiles.length == 0) {
			switchPreferences.setEnabled(false);
			delSavedPreferences.setEnabled(false);
			CharSequence cs[] = { "Default" };
			switchPreferences.setEntries(cs);
			switchPreferences.setEntryValues(cs);
			switchPreferences.setValue("Default");
			delSavedPreferences.setEntries(cs);
			delSavedPreferences.setEntryValues(cs);
		} else if (subFiles.length == 1) {
			switchPreferences.setEnabled(false);
			delSavedPreferences.setEnabled(false);
			CharSequence cs[] = subFiles;
			switchPreferences.setEntries(cs);
			switchPreferences.setEntryValues(cs);
			switchPreferences.setValue(cs[0].toString());
			delSavedPreferences.setEntries(cs);
			delSavedPreferences.setEntryValues(cs);
		} else {
			switchPreferences.setEnabled(true);
			delSavedPreferences.setEnabled(true);
			CharSequence cs[] = subFiles;
			switchPreferences.setEntries(cs);
			switchPreferences.setEntryValues(cs);
			// TODO 只有一条时很难看
			CharSequence csToDel[] = new String[subFiles.length - 1];
			for (int i = 0, j = 0; i < subFiles.length; i++, j++) {
				if (!subFiles[i].equals(switchPreferences.getValue())) {
					csToDel[j] = subFiles[i];
				} else {
					j--;
				}
			}
			delSavedPreferences.setEntries(csToDel);
			delSavedPreferences.setEntryValues(csToDel);
		}
	}

	private static Map<String, String> converStringToMap(String str) {
		Map<String, String> map = new HashMap<String, String>();
		str = str.substring(1, str.length() - 1);
		String[] entry = str.split(",");
		for (int i = 0; i < entry.length; i++) {
			String[] keyvalue = entry[i].split("=");
			String key = keyvalue[0].trim();
			String value;
			if (keyvalue.length == 1) {
				value = "";
			} else {
				value = keyvalue[1].trim();
			}
			// System.out.println(key);
			// System.out.println(value);
			map.put(key, value);
		}

		// Map<String, String> map = converStringToMap(string);
		// for (Map.Entry<String, String> entry : map.entrySet()) {
		// System.out.println(entry.getKey() + "/" + entry.getValue());
		// }
		return map;
	}
}