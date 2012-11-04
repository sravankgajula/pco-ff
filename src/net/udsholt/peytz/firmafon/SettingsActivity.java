package net.udsholt.peytz.firmafon;

import android.os.Bundle;
import android.preference.PreferenceActivity;

// http://jetpad.org/2011/01/creating-a-preference-activity-in-android/
// http://www.javacodegeeks.com/2011/01/android-quick-preferences-tutorial.html

public class SettingsActivity extends PreferenceActivity 
{
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}