package fr.xgouchet.android.bttf.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import fr.xgouchet.android.bttf.R;
import fr.xgouchet.android.bttf.activity.PreferencesActivity;
import fr.xgouchet.android.bttf.background.TimeCircuitsService;
import fr.xgouchet.android.bttf.utils.SettingsUtils;

/**
 * Fragment displaying preferences for the TimeCircuits widget / watchface
 */
public class TimeCircuitsPreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private ListPreference mDestTimeSource, mDepartedTimeSource;


    private Preference mDestOption, mDepartedOption;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getActivity();


        addPreferencesFromResource(R.xml.pref_timecircuits);
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);


        mDestTimeSource = (ListPreference) findPreference(SettingsUtils.PREF_DESTINATION_SOURCE);
        mDepartedTimeSource = (ListPreference) findPreference(SettingsUtils.PREF_DEPARTED_SOURCE);

        mDestOption = findPreference(SettingsUtils.PREF_DESTINATION_OPTION);
        mDestOption.setOnPreferenceClickListener(this);
        mDepartedOption = findPreference(SettingsUtils.PREF_DEPARTED_OPTION);
        mDepartedOption.setOnPreferenceClickListener(this);
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
        TimeCircuitsService.startService(activity);
        activity.updateWearablePreferences();
    }

    private void updatePrefsUI() {


        mDestTimeSource.setSummary(mDestTimeSource.getEntry());
        mDepartedTimeSource.setSummary(mDepartedTimeSource.getEntry());

        updateUI("pref_destination_", mDestTimeSource, mDestOption);
        updateUI("pref_departed_", mDepartedTimeSource, mDepartedOption);

    }

    private void updateUI(String base, ListPreference source, Preference preference) {
        Context context = getActivity();

        boolean optionsEnabled = false;
        int titleResId = 0;
        String summary = "";

        switch (source.getValue()) {
            case SettingsUtils.SOURCE_BATTERY:
                titleResId = R.string.pref_battery;
                break;
            case SettingsUtils.SOURCE_CALENDAR:
                titleResId = R.string.pref_calendar;
                break;
            case SettingsUtils.SOURCE_FREETEXT:
                optionsEnabled = true;
                titleResId = R.string.pref_free_text;
                summary = SettingsUtils.getStringPreference(context, base + SettingsUtils.SOURCE_FREETEXT, "");

                summary = String.format("%s             ", summary).toUpperCase();
                summary = (summary.substring(0, 3) + " " + summary.substring(3, 5) + " "
                        + summary.substring(5, 9) + " " + summary.substring(9, 11) + " "
                        + summary.substring(11, 13));
                break;
            case SettingsUtils.SOURCE_FREETIME:
                optionsEnabled = true;
                titleResId = R.string.pref_free_time;
                summary = "?";
                break;
            case SettingsUtils.SOURCE_TIMEZONE:
                optionsEnabled = true;
                titleResId = R.string.pref_time_zone;
                summary = SettingsUtils.getStringPreference(context, base + SettingsUtils.SOURCE_TIMEZONE);
                break;
        }

        preference.setEnabled(optionsEnabled);
        preference.setTitle(titleResId);
        preference.setSummary(summary);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        String optionsKey = preference.getKey();
        String base = optionsKey.substring(0, optionsKey.lastIndexOf("_"));
        String sourceKey = base + "_source";
        String source = SettingsUtils.getStringPreference(getActivity(), sourceKey);
        String valueKey = base + "_" + source;

        Log.d("TimeCircuitsPreferencesFragment", sourceKey + " : " + source);

        switch (source) {
            case SettingsUtils.SOURCE_FREETEXT:
                promptFreeText(valueKey);
                break;
            case SettingsUtils.SOURCE_FREETIME:
                // TODO prompt for free time and date
                break;
            case SettingsUtils.SOURCE_TIMEZONE:
                promptTimeZone(valueKey);
                break;
        }

        return false;
    }

    public void promptFreeText(final String preferenceKey) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.pref_free_text);

        // setup the EditText
        final EditText input = new EditText(getActivity());
        input.setHint(R.string.pref_free_text_hint);
        input.setText(SettingsUtils.getStringPreference(getActivity(), preferenceKey, ""));
        builder.setView(input);

        // setup callbacks
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SettingsUtils.setStringPreference(getActivity(), preferenceKey, input.getText().toString());
            }
        });
        builder.setNeutralButton(android.R.string.cancel, null);

        // display the alert
        builder.show();
    }


    public void promptTimeZone(final String preferenceKey) {
        TimezonePickerDialogFragment zonePickerDialogFragment = new TimezonePickerDialogFragment();
        zonePickerDialogFragment.setOnTimeZonePickedListener(new TimezonePickerDialogFragment.OnTimeZonePickedListener() {
            @Override
            public void onTimeZonePicked(String timezoneId) {
                SettingsUtils.setStringPreference(getActivity(), preferenceKey, timezoneId);
            }
        });
        zonePickerDialogFragment.show(getFragmentManager(), "timezone");
    }
}
