package com.tvi910.android.core;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;

public class PreferenceActivityUpdateSummary 
    extends PreferenceActivity 
    implements OnSharedPreferenceChangeListener {
       
   protected void onCreate(Bundle savedInstanceState, int resourceId) {
       super.onCreate(savedInstanceState);

    this.addPreferencesFromResource(resourceId);

    this.initSummaries(this.getPreferenceScreen());

    this.getPreferenceScreen().getSharedPreferences()
      .registerOnSharedPreferenceChangeListener(this);
    }

  /**
    * Set the summaries of all preferences
    */
  private void initSummaries(PreferenceGroup pg) {
    for (int i = 0; i < pg.getPreferenceCount(); ++i) {
    Preference p = pg.getPreference(i);
    if (p instanceof PreferenceGroup)
      this.initSummaries((PreferenceGroup) p); // recursion
    else
      this.setSummary(p);
    }
  }

  /**
    * Set the summaries of the given preference
    */
  private void setSummary(Preference pref) {
    // react on type or key
      if (pref instanceof ListPreference) {
      ListPreference listPref = (ListPreference) pref;
      pref.setSummary(listPref.getEntry());
      }
        else if (pref instanceof KeymapPreference) {
            KeymapPreference kp = (KeymapPreference) pref;
            pref.setSummary(kp.getText());
        }
  }

  /**
    * used to change the summary of a preference
    */
  public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
    Preference pref = findPreference(key);
    this.setSummary(pref);
  }
}

