package fr.xgouchet.android.bttf.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.List;

import fr.xgouchet.android.bttf.R;
import fr.xgouchet.android.bttf.background.TimeCircuitsService;
import fr.xgouchet.android.bttf.utils.SettingsUtils;
import fr.xgouchet.android.bttf.utils.TimezoneUtils;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class PreferencesActivity extends PreferenceActivity {


    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);

        loadHeadersFromResource(R.xml.pref_headers, target);
    }


    public static class TimeCircuitsPreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private ListPreference mDestTimeSource;
        private ListPreference mDestTimezone;
        private EditTextPreference mDestFreeText;

        private ListPreference mDepartedTimeSource;
        private ListPreference mDepartedTimezone;
        private EditTextPreference mDepartedFreeText;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Context context = getActivity();


            addPreferencesFromResource(R.xml.pref_timecircuits);
            PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);


            mDestTimeSource = (ListPreference) findPreference(SettingsUtils.PREF_DESTINATION_SOURCE);
            mDestTimezone = (ListPreference) findPreference(SettingsUtils.PREF_DESTINATION_TIMEZONE);
            TimezoneUtils.setupTimezonPreference(mDestTimezone);
            mDestFreeText = (EditTextPreference) findPreference(SettingsUtils.PREF_DESTINATION_FREETEXT);


            mDepartedTimeSource = (ListPreference) findPreference(SettingsUtils.PREF_DEPARTED_SOURCE);
            mDepartedTimezone = (ListPreference) findPreference(SettingsUtils.PREF_DEPARTED_TIMEZONE);
            TimezoneUtils.setupTimezonPreference(mDepartedTimezone);
            mDepartedFreeText = (EditTextPreference) findPreference(SettingsUtils.PREF_DEPARTED_FREETEXT);
        }

        @Override
        public void onResume() {
            super.onResume();
            updatePrefsUI();
        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePrefsUI();
            TimeCircuitsService.startService(getActivity());
        }

        private void updatePrefsUI() {

            mDestTimeSource.setSummary(mDestTimeSource.getEntry());
            mDestTimezone.setSummary(mDestTimezone.getEntry());

            String freeTextSummary = mDestFreeText.getText().toUpperCase();
            mDestFreeText.setSummary(freeTextSummary.substring(0, 3) + " "
                    + freeTextSummary.substring(3, 5) + " " + freeTextSummary.substring(5, 9)
                    + " " + freeTextSummary.substring(9, 11) + " "
                    + freeTextSummary.substring(11, 13));

            mDestTimezone.setEnabled(TextUtils.equals(mDestTimeSource.getValue(), SettingsUtils.SOURCE_TIMEZONE));
            mDestFreeText.setEnabled(TextUtils.equals(mDestTimeSource.getValue(), SettingsUtils.SOURCE_FREETEXT));


            mDepartedTimeSource.setSummary(mDepartedTimeSource.getEntry());
            mDepartedTimezone.setSummary(mDepartedTimezone.getEntry());
            freeTextSummary = mDepartedFreeText.getText().toUpperCase();
            mDepartedFreeText.setSummary(freeTextSummary.substring(0, 3) + " "
                    + freeTextSummary.substring(3, 5) + " " + freeTextSummary.substring(5, 9)
                    + " " + freeTextSummary.substring(9, 11) + " "
                    + freeTextSummary.substring(11, 13));

            mDepartedTimezone.setEnabled(TextUtils.equals(mDepartedTimeSource.getValue(), SettingsUtils.SOURCE_TIMEZONE));
            mDepartedFreeText.setEnabled(TextUtils.equals(mDepartedTimeSource.getValue(), SettingsUtils.SOURCE_FREETEXT));
        }
    }
}
