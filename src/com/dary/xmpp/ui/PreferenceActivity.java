package com.dary.xmpp.ui;

import android.os.Bundle;

import com.dary.xmpp.R;

public class PreferenceActivity extends android.preference.PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
