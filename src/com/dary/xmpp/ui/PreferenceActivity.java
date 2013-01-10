package com.dary.xmpp.ui;

import com.dary.xmpp.R;
import com.dary.xmpp.R.xml;

import android.os.Bundle;

public class PreferenceActivity extends android.preference.PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
