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
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

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

    private GoogleApiClient mGoogleApiClient;
    private WearableConnectionHandler mWearableConnectionHandler;
    private boolean mConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWearableConnectionHandler = new WearableConnectionHandler();

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addConnectionCallbacks(mWearableConnectionHandler);
        builder.addOnConnectionFailedListener(mWearableConnectionHandler);
        builder.addApi(Wearable.API);

        mGoogleApiClient = builder.build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);

        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (TimeCircuitsPreferencesFragment.class.getName().equals(fragmentName))
            return true;
        return false;
    }

    /**
     * If a wearable is connected, send the current preferences to it
     */
    private void updateWearablePreferences() {
        if (!mConnected) {
            return;
        }

        // Warn the user we're sending data now
        Toast.makeText(this, R.string.toast_updating_wearable_preferences, Toast.LENGTH_LONG);

        // Create the request
        final PutDataMapRequest putRequest = PutDataMapRequest.create("/CONFIG");
        // Fill it with the current values
        SettingsUtils.fillDataMap(this, putRequest.getDataMap());

        // send to the wearable
        Wearable.DataApi.putDataItem(mGoogleApiClient, putRequest.asPutDataRequest());
    }



    /**
     * The class handling connection with the wearable
     */
    public class WearableConnectionHandler implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnected(Bundle bundle) {
            Log.d("WearableConnectionHandler", "onConnected");
            mConnected = true;
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.w("WearableConnectionHandler", "onConnectionSuspended ");
            mConnected = false;
            switch (cause) {
                case CAUSE_NETWORK_LOST:
                    Log.i("WearableConnectionHandler", "Network Lost");
                    break;
                case CAUSE_SERVICE_DISCONNECTED:
                    Log.i("WearableConnectionHandler", "Service Disconnected");
                    break;
                default:
                    Log.i("WearableConnectionHandler", "Cause unknown");
                    break;
            }
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            mConnected = false;
            Log.w("WearableConnectionHandler", "onConnectionFailed " + connectionResult.getErrorCode());
        }
    }

    /**
     * Fragment displaying preferences for the TimeCircuits widget / watchface
     */
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
            // first, update the UI
            updatePrefsUI();

            PreferencesActivity activity = (PreferencesActivity) getActivity();

            // update all widgets (if any)
            TimeCircuitsService.startService(activity);
            activity.updateWearablePreferences();
        }

        private void updatePrefsUI() {

            mDestTimeSource.setSummary(mDestTimeSource.getEntry());
            mDestTimezone.setSummary(mDestTimezone.getEntry());

            String freeTextSummary = mDestFreeText.getText();
            if (freeTextSummary != null) {
                freeTextSummary = freeTextSummary.toUpperCase();
                mDestFreeText.setSummary(freeTextSummary.substring(0, 3) + " "
                        + freeTextSummary.substring(3, 5) + " " + freeTextSummary.substring(5, 9)
                        + " " + freeTextSummary.substring(9, 11) + " "
                        + freeTextSummary.substring(11, 13));
            }

            mDestTimezone.setEnabled(TextUtils.equals(mDestTimeSource.getValue(), SettingsUtils.SOURCE_TIMEZONE));
            mDestFreeText.setEnabled(TextUtils.equals(mDestTimeSource.getValue(), SettingsUtils.SOURCE_FREETEXT));


            mDepartedTimeSource.setSummary(mDepartedTimeSource.getEntry());
            mDepartedTimezone.setSummary(mDepartedTimezone.getEntry());

            freeTextSummary = mDepartedFreeText.getText();
            if (freeTextSummary != null) {
                freeTextSummary = freeTextSummary.toUpperCase();
                mDepartedFreeText.setSummary(freeTextSummary.substring(0, 3) + " "
                        + freeTextSummary.substring(3, 5) + " " + freeTextSummary.substring(5, 9)
                        + " " + freeTextSummary.substring(9, 11) + " "
                        + freeTextSummary.substring(11, 13));
            }

            mDepartedTimezone.setEnabled(TextUtils.equals(mDepartedTimeSource.getValue(), SettingsUtils.SOURCE_TIMEZONE));
            mDepartedFreeText.setEnabled(TextUtils.equals(mDepartedTimeSource.getValue(), SettingsUtils.SOURCE_FREETEXT));
        }
    }


}
