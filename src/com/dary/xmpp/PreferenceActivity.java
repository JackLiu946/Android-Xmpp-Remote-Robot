package com.dary.xmpp;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

public class PreferenceActivity extends android.preference.PreferenceActivity implements OnSharedPreferenceChangeListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

	}
}
