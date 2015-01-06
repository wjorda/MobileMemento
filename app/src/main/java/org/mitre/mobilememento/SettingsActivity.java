package org.mitre.mobilememento;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;


import java.util.List;

/**
 * @author wes
 *
 * Presents the user preferences that apply to the whole application. These are loaded from
 * <code>xml/preferences.xml</code> using <code>addPreferencesFromResource()</code>, much against
 * Google's wishes (because I hate and don't really understand <code>Fragment</code>s
 */
public class SettingsActivity extends PreferenceActivity {

    /**
     * Loads the preferences from XML.
     * @see <code>xml/preferences.xml</code>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        getActionBar().setTitle("Settings");
        addPreferencesFromResource(R.xml.preferences);
    }

}
