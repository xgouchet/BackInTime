package fr.xgouchet.android.bttf.background;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import fr.xgouchet.android.bttf.utils.SettingsUtils;

/**
 * Service listening to the Messages from a connected Wearable
 */
public class MessagesListenerService extends WearableListenerService {


    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        if (messageEvent.getPath().equals("/REQUEST_CONFIG")) {

            Log.d("MessagesListenerService", "Node requested the current config !");
            connectAndSendPreferences();

        }
    }

    private void connectAndSendPreferences() {
        if (mGoogleApiClient == null) {
            WearableConnectionHandler mWearableConnectionHandler = new WearableConnectionHandler();

            GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
            builder.addConnectionCallbacks(mWearableConnectionHandler);
            builder.addOnConnectionFailedListener(mWearableConnectionHandler);
            builder.addApi(Wearable.API);

            mGoogleApiClient = builder.build();
        }

        if (mGoogleApiClient.isConnected()) {
            updateWearablePreferences();
        } else if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * If a wearable is connected, send the current preferences to it
     */
    private void updateWearablePreferences() {

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
            updateWearablePreferences();
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
