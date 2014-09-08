package fr.xgouchet.android.bttf.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import fr.xgouchet.android.bttf.R;
import fr.xgouchet.android.bttf.fragments.TimeCircuitsPreferencesFragment;
import fr.xgouchet.android.bttf.utils.SettingsUtils;

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

        WearableConnectionHandler mWearableConnectionHandler = new WearableConnectionHandler();

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
    public void updateWearablePreferences() {
        if ((mGoogleApiClient == null) || !mGoogleApiClient.isConnected()) {
            Log.w("PreferenceActivity", "Wearable not connected");
            return;
        }

        // Warn the user we're sending data now
        Toast.makeText(this, R.string.toast_updating_wearable_preferences, Toast.LENGTH_LONG).show();

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
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.w("WearableConnectionHandler", "onConnectionSuspended ");

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
            Log.w("WearableConnectionHandler", "onConnectionFailed " + connectionResult.getErrorCode());
        }
    }


}
