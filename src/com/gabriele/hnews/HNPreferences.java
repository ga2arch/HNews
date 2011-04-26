package com.gabriele.hnews;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class HNPreferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
