
package com.droid2600;

import com.tvi910.android.core.PreferenceActivityUpdateSummary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class Droid2600Preferences extends PreferenceActivityUpdateSummary {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.xml.preferences);
    }

}
