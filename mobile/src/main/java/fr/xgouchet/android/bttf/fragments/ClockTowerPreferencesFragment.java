package fr.xgouchet.android.bttf.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import fr.xgouchet.android.bttf.R;
import fr.xgouchet.android.bttf.activity.PreferencesActivity;
import fr.xgouchet.android.bttf.clocktower.ClockTowerUtils;
import fr.xgouchet.android.bttf.common.WearableUtils;
import fr.xgouchet.android.bttf.widget.ClockTowerWidgetProvider;

/**
 * Fragment displaying preferences for the TimeCircuits widget / watchface
 */
public class ClockTowerPreferencesFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ListPreference mTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getActivity();


        addPreferencesFromResource(R.xml.pref_clocktower);
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);


        mTheme = (ListPreference) findPreference(ClockTowerUtils.PREF_CLOCK_THEME);
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePrefsUI();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // first, update the UI
        updatePrefsUI();

        PreferencesActivity activity = (PreferencesActivity) getActivity();

        // update all widgets (if any)
        ClockTowerWidgetProvider.triggerUpdate(getActivity());

        // send prefs to the wearable
        if (WearableUtils.updateWearablePreferences(activity, activity.getGoogleApiClient())) {
            // Warn the user we're sending data now
            Toast.makeText(activity, R.string.toast_updating_wearable_preferences, Toast.LENGTH_LONG).show();
        }
    }

    private void updatePrefsUI() {


        mTheme.setSummary(mTheme.getEntry());
    }

}
