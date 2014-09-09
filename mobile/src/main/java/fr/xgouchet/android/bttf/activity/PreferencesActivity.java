package fr.xgouchet.android.bttf.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

import fr.xgouchet.android.bttf.R;
import fr.xgouchet.android.bttf.common.WearableUtils;
import fr.xgouchet.android.bttf.fragments.ClockTowerPreferencesFragment;
import fr.xgouchet.android.bttf.fragments.TimeCircuitsPreferencesFragment;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = WearableUtils.buildWearableClient(this, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if ((mGoogleApiClient != null) && !(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if ((mGoogleApiClient != null) && mGoogleApiClient.isConnected()) {
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

        if (ClockTowerPreferencesFragment.class.getName().equals(fragmentName))
            return true;

        return false;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }


}
