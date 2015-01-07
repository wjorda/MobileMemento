package org.mitre.mobilememento;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author wes
 *
 * Presents the user preferences that apply to the whole application. These are loaded from
 * <code>xml/preferences.xml</code> using <code>addPreferencesFromResource()</code>.Yes, I know that
 * this is deprecated.*/
public class SettingsActivity extends PreferenceActivity {

    /**
     * Loads the preferences from XML.
     * @see <code>xml/preferences.xml</code>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Settings");
        addPreferencesFromResource(R.xml.preferences);
    }

}
