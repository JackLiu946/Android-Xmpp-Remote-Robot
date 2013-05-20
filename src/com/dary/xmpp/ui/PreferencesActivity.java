package com.dary.xmpp.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.R.integer;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.dary.xmpp.MyApp;
import com.dary.xmpp.R;

public class PreferencesActivity extends android.preference.PreferenceActivity {
	private ListPreference switchPreferences;
	private Preference switchPreferencesBetweenDifferentNetwork, saveCurrentPreferences;
	private MultiSelectListPreference delSavedPreferences, delSwitchPreferencesBetweenDifferentNetwork;
	private static final String SDCARD = Environment.getExternalStorageDirectory().toString();
	private static final String SAVE_PREFERENCES_PATH = SDCARD + File.separator + "Android XMPP Remote Robot" + File.separator + "Save Preferences";

	// private static final String
	// SWITCH_PREFERENCES_BETWEEN_DIFFERENT_NETWORK_PATH = SDCARD +
	// File.separator + "Android XMPP Remote Robot" + File.separator
	// + "Swtich Preferences Between Different Netwrok";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File file1 = new File(SAVE_PREFERENCES_PATH);
		// File file2 = new
		// File(SWITCH_PREFERENCES_BETWEEN_DIFFERENT_NETWORK_PATH);
		if (!file1.exists()) {
			file1.mkdirs();
		}
		// if (!file2.exists()) {
		// file2.mkdirs();
		// }
		addPreferencesFromResource(R.xml.preferences);
		switchPreferences = (ListPreference) findPreference("switchPreferences");
		switchPreferencesBetweenDifferentNetwork = findPreference("switchPreferencesBetweenDifferentNetwork");
		delSavedPreferences = (MultiSelectListPreference) findPreference("delSavedPreferences");
		delSwitchPreferencesBetweenDifferentNetwork = (MultiSelectListPreference) findPreference("delSwitchPreferencesBetweenDifferentNetwork");
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
								Map<String, ?> prefs = mPrefs.getAll();
								// TODO 文件名不能为空,不能含有分隔符
								String prefsFileName = ETsavepreferences.getText().toString();
								// File file =
								// MyApp.getContext().getFileStreamPath(prefsFileName);
								File file = new File(SAVE_PREFERENCES_PATH + File.separator + prefsFileName);
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
				// File file =
				// MyApp.getContext().getFileStreamPath(newValue.toString());
				File file = new File(SAVE_PREFERENCES_PATH + File.separator + newValue.toString());
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
						Map<String, ?> map = mPrefs.getAll();
						// 判断原map的value类型
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
					File file = new File(SAVE_PREFERENCES_PATH + File.separator + prefs[i]);
					// File file =
					// MyApp.getContext().getFileStreamPath(prefs[i]);
					if (file.exists()) {
						file.delete();
					}
				}
				setList();
				return false;
			}
		});

		delSwitchPreferencesBetweenDifferentNetwork.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// 删除的时候,如果这条自动切换条件已经写入到配置文件中,则应该依次读取删除?
				// 可能为多条
				String prefs[] = newValue.toString().split("\\|");
				for (int i = 0; i < prefs.length; i++) {
					SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
					Editor e = mPrefs.edit();
					String oldValue = mPrefs.getString(switchPreferencesBetweenDifferentNetwork.getKey(), "");
					// 替换掉|
					e.putString(switchPreferencesBetweenDifferentNetwork.getKey(), oldValue.replace(prefs[i] + "|", "").replace(prefs[i], ""));
					e.commit();
				}
				setList();
				return false;
			}
		});
		//TODO不同网络下,只能应用一套配置,必须加逻辑判断
		switchPreferencesBetweenDifferentNetwork.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				WifiManager wifiManager = (WifiManager) MyApp.getContext().getSystemService(Context.WIFI_SERVICE);
				// for (int i = 0; i <
				// wifiManager.getConfiguredNetworks().size(); i++) {
				// System.out.println(wifiManager.getConfiguredNetworks().get(i).SSID);
				// }

				final String cs1[] = new String[wifiManager.getConfiguredNetworks().size() + 1];
				cs1[0] = "Mobile";
				for (int i = 1, j = 0; j < wifiManager.getConfiguredNetworks().size(); i++, j++) {
					cs1[i] = wifiManager.getConfiguredNetworks().get(j).SSID;
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(PreferencesActivity.this, android.R.layout.simple_list_item_1, cs1);
				AlertDialog.Builder b = new Builder(PreferencesActivity.this);

				b.setAdapter(adapter, new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// String[] subFiles = getFilesDir().list();
						final int whichNetwork = which;
						String[] subFiles = new File(SAVE_PREFERENCES_PATH).list();
						final String cs2[] = new String[subFiles.length + 1];
						cs2[0] = "Nothing";
						for (int i = 1, j = 0; j < subFiles.length; i++, j++) {
							cs2[i] = subFiles[j];
						}
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(PreferencesActivity.this, android.R.layout.simple_list_item_1, cs2);
						AlertDialog.Builder b = new Builder(PreferencesActivity.this);
						b.setAdapter(adapter, new OnClickListener() {

							public void onClick(DialogInterface dialog, int whichPreferebces) {
								Toast.makeText(PreferencesActivity.this, cs1[whichNetwork] + " " + cs2[whichPreferebces], Toast.LENGTH_LONG).show();
								SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
								Editor e = mPrefs.edit();
								String oldValue = mPrefs.getString(switchPreferencesBetweenDifferentNetwork.getKey(), "");
								String newValue = cs1[whichNetwork] + "-" + cs2[whichPreferebces];
								if (oldValue != "") {
									e.putString(switchPreferencesBetweenDifferentNetwork.getKey(), oldValue + "|" + newValue);
									e.commit();
									setList();
								} else {
									e.putString(switchPreferencesBetweenDifferentNetwork.getKey(), newValue);
									e.commit();
									setList();
								}
							}
						});
						b.setTitle("(2) Select Preferences");
						b.show();
					}
				});
				b.setTitle("(1) Select Network");
				b.show();

				return false;
			}
		});
	}

	private void setList() {
		// String[] subFiles = getFilesDir().list();
		String[] subFiles = new File(SAVE_PREFERENCES_PATH).list();
		if (subFiles.length == 0) {
			switchPreferences.setEnabled(false);
			delSavedPreferences.setEnabled(false);
			CharSequence cs[] = { "Default" };
			switchPreferences.setEntries(cs);
			switchPreferences.setEntryValues(cs);
			delSavedPreferences.setEntries(cs);
			delSavedPreferences.setEntryValues(cs);
		} else if (subFiles.length == 1) {
			switchPreferences.setEnabled(true);
			delSavedPreferences.setEnabled(false);
			CharSequence cs[] = subFiles;
			switchPreferences.setEntries(cs);
			switchPreferences.setEntryValues(cs);
			delSavedPreferences.setEntries(cs);
			delSavedPreferences.setEntryValues(cs);
		} else {
			switchPreferences.setEnabled(true);
			delSavedPreferences.setEnabled(true);
			CharSequence cs[] = subFiles;
			switchPreferences.setEntries(cs);
			switchPreferences.setEntryValues(cs);
			// TODO 只有一条时很难看...
			CharSequence csToDel[] = null;
			// 当不为默认配置时,要减掉当前使用那一套配置
			if (!switchPreferences.getValue().equals("Default")) {
				csToDel = new String[subFiles.length - 1];
				for (int i = 0, j = 0; i < subFiles.length; i++, j++) {
					if (!subFiles[i].equals(switchPreferences.getValue())) {
						csToDel[j] = subFiles[i];
					} else {
						j--;
					}
				}
			} else {
				csToDel = new String[subFiles.length];
				for (int i = 0; i < subFiles.length; i++) {
					csToDel[i] = subFiles[i];
				}
			}
			delSavedPreferences.setEntries(csToDel);
			delSavedPreferences.setEntryValues(csToDel);
		}
		if (switchPreferences.getValue() == "") {
			switchPreferences.setValue("Default");
		}

		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
		Editor e = mPrefs.edit();
		String value = mPrefs.getString(switchPreferencesBetweenDifferentNetwork.getKey(), "");
		if (value != "") {
			CharSequence csToDel[] = null;
			csToDel = value.split("\\|");
			delSwitchPreferencesBetweenDifferentNetwork.setEnabled(true);
			delSwitchPreferencesBetweenDifferentNetwork.setEntries(csToDel);
			delSwitchPreferencesBetweenDifferentNetwork.setEntryValues(csToDel);
		} else {
			delSwitchPreferencesBetweenDifferentNetwork.setEnabled(false);
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