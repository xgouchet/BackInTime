package fr.xgouchet.android.bttf.common;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * The class handling connection with the wearable
 */
public class WearableConnectionHandler implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("WearableConnectionHandler", "onConnected w/");
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
