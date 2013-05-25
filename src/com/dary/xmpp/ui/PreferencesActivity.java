package com.dary.xmpp.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.dary.xmpp.R;
import com.dary.xmpp.application.MyApp;
import com.dary.xmpp.tools.Tools;

public class PreferencesActivity extends android.preference.PreferenceActivity {
	private ListPreference switchPreferences;
	private Preference switchPreferencesBetweenDifferentNetwork, saveCurrentPreferences;
	private MultiSelectListPreference delSavedPreferences, delSwitchPreferencesBetweenDifferentNetwork;
	private CheckBoxPreference autoSwitchPreferencesBetweenDifferentNetwork;
	public static final String SAVE_PREFERENCES_PATH = Tools.getSDPath() + File.separator + "Android XMPP Remote Robot" + File.separator + "Save Preferences";

	@SuppressWarnings("deprecation")
	@Override
	// TODO 修改了配置之后,未切换时的状态不应还为当前的状态,应随配置改变而及时的更新写入?
	// TODO 程序重装以后不能自动切换到之前最后所使用的配置文件,能否实现让其自动切换?
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 载入之前的配置

		File file = new File(SAVE_PREFERENCES_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		addPreferencesFromResource(R.xml.preferences);
		switchPreferences = (ListPreference) findPreference("switchPreferences");
		switchPreferencesBetweenDifferentNetwork = findPreference("switchPreferencesBetweenDifferentNetwork");
		delSavedPreferences = (MultiSelectListPreference) findPreference("delSavedPreferences");
		delSwitchPreferencesBetweenDifferentNetwork = (MultiSelectListPreference) findPreference("delSwitchPreferencesBetweenDifferentNetwork");
		setList();
		autoSwitchPreferencesBetweenDifferentNetwork = (CheckBoxPreference) findPreference("autoSwitchPreferencesBetweenDifferentNetwork");
		switchPreferences.setSummary("Current Preferences is " + switchPreferences.getValue());
		saveCurrentPreferences = findPreference("saveCurrentPreferences");

		saveCurrentPreferences.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@SuppressLint("NewApi")
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
								// 不能为Append模式
								Tools.writeFile(file, prefs.toString(), true);

								switchPreferences.setSummary("Current Preferences is " + prefsFileName);
								switchPreferences.setValue(prefsFileName);
								setList();
							}
						}).setNegativeButton("Cancel", null).setIcon(R.drawable.ic_launcher).show();
				return true;
			}
		});
		switchPreferences.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean result = switchPreferences(newValue);
				if (result) {
					onCreate(null);
				}
				return result;
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
				return true;
			}
		});

		delSwitchPreferencesBetweenDifferentNetwork.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// 删除的时候,如果这条自动切换条件已经写入到配置文件中,则应该依次读取删除?
				// 可能为多条
				String prefs[] = newValue.toString().split("\\|");
				String writeValue = "";
				for (int i = 0; i < prefs.length; i++) {
					SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
					Editor e = mPrefs.edit();
					String oldValue = mPrefs.getString(switchPreferencesBetweenDifferentNetwork.getKey(), "");
					// 替换掉|
					writeValue = oldValue.replace(prefs[i] + "|", "").replace(prefs[i], "");
					e.putString(switchPreferencesBetweenDifferentNetwork.getKey(), writeValue);
					e.commit();
				}
				writeToSavedPreferences(switchPreferencesBetweenDifferentNetwork.getKey(), writeValue);
				setList();
				return true;
			}

		});

		// TODO不同网络下,只能应用一套配置,必须加逻辑判断
		switchPreferencesBetweenDifferentNetwork.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				WifiManager wifiManager = (WifiManager) MyApp.getContext().getSystemService(Context.WIFI_SERVICE);
				// for (int i = 0; i <
				// wifiManager.getConfiguredNetworks().size(); i++) {
				// System.out.println(wifiManager.getConfiguredNetworks().get(i).SSID);
				// }
				if (wifiManager.getConfiguredNetworks() == null) {
					Toast.makeText(PreferencesActivity.this, "Please Turn On Wifi", Toast.LENGTH_LONG).show();
					return false;
				}
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
								Toast.makeText(PreferencesActivity.this, "When " + cs1[whichNetwork] + " Switch To " + cs2[whichPreferebces], Toast.LENGTH_LONG)
										.show();
								SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
								Editor e = mPrefs.edit();
								String oldValue = mPrefs.getString(switchPreferencesBetweenDifferentNetwork.getKey(), "");
								String newValue = cs1[whichNetwork] + "^" + cs2[whichPreferebces];
								String writeValue = "";
								if (oldValue != "") {
									writeValue = oldValue + "|" + newValue;
									e.putString(switchPreferencesBetweenDifferentNetwork.getKey(), writeValue);
								} else {
									writeValue = newValue;
									e.putString(switchPreferencesBetweenDifferentNetwork.getKey(), writeValue);
								}
								e.commit();
								setList();
								writeToSavedPreferences(switchPreferencesBetweenDifferentNetwork.getKey(), writeValue);
							}
						});
						b.setTitle("(2) Select Preferences");
						b.show();
					}
				});
				b.setTitle("(1) Select Network");
				b.show();

				return true;
			}
		});

		autoSwitchPreferencesBetweenDifferentNetwork.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference, Object newValue) {
				writeToSavedPreferences(autoSwitchPreferencesBetweenDifferentNetwork.getKey(), newValue.toString());
				return true;
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

	// TODO 如果采用PreferenceScreen,切换配置后子配置页不更新
	public static boolean switchPreferences(Object newValue) {
		// 读取配置文件
		// File file =
		// MyApp.getContext().getFileStreamPath(newValue.toString());
		File file = new File(SAVE_PREFERENCES_PATH + File.separator + newValue.toString());
		if (file.exists()) {
			String prefs = Tools.readFile(file);
			// 写入(切换)配置
			Map<String, String> prefsMap = converStringToMap(prefs);
			SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
			Editor e = mPrefs.edit();
			Map<String, ?> map = mPrefs.getAll();
			// 不全
			for (Map.Entry<String, String> entry : prefsMap.entrySet()) {
				String key = entry.getKey();
				// 判断原map的value类型
				// 将"switchPreferencesBetweenDifferentNetwork"项排除在外
				// if (!key.equals("switchPreferencesBetweenDifferentNetwork")
				// &&
				// !key.equals("autoSwitchPreferencesBetweenDifferentNetwork"))
				// {
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
				// }
			}
			// 配置切换这一项要修改为最新的值
			e.putString("switchPreferences", newValue.toString());
			e.commit();
			// File currentPrefs = new File(SAVE_PREFERENCES_PATH +
			// File.separator + "CurrentPrefs");
			// Tools.writeFile(currentPrefs, newValue.toString(),false);
			return true;
		} else {
			return false;
		}
	}

	private void writeToSavedPreferences(String key, String writeValue) {
		File[] subFiles = new File(SAVE_PREFERENCES_PATH).listFiles();
		for (int i = 0; i < subFiles.length; i++) {
			String prefs = Tools.readFile(subFiles[i]);
			Map<String, String> prefsMap = converStringToMap(prefs);
			prefsMap.put(key, writeValue);
			Tools.writeFile(subFiles[i], prefsMap.toString(), false);
		}
	}

}