package fr.xgouchet.android.bttf.common;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

/**
 *
 */
public class WearableUtils {

    public static final String PATH_PREFERENCES = "/PREFERENCES";
    public static final String PATH_CALENDAR = "/CALENDAR";
    public static final String PATH_UPDATE_REQUEST = "/REQUEST/UPDATE";
    public static final String PATH_CALENDAR_REQUEST = "/REQUEST/CALENDAR";


    /**
     * Builds a client for Wearable API data exchange.
     *
     * @param handler the handler to use, if any
     * @return
     */
    public static GoogleApiClient buildWearableClient(Context context, WearableConnectionHandler handler) {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context);

        if (handler != null) {
            Log.d("WearableUtils", "Adding Handler");
            builder.addConnectionCallbacks(handler);
            builder.addOnConnectionFailedListener(handler);
        }

        builder.addApi(Wearable.API);
        builder.addApi(Wearable.API);

        return builder.build();
    }

    /**
     * Sends the current preferences to the connected wearable (if any)
     *
     * @param context the current context
     * @param client  the google API client
     */
    public static boolean updateWearablePreferences(Context context, GoogleApiClient client) {

        // safe check
        if ((client == null) || !client.isConnected()) {
            Log.w("WearableUtils", "Wearable not connected");
            return false;
        }


        // Create the request
        final PutDataMapRequest putRequest = PutDataMapRequest.create(PATH_PREFERENCES);

        // Fill it with the current values
        Log.w("WearableUtils", "creating data");
        PreferencesUtils.writeDataMap(context, putRequest.getDataMap());

        // send to the wearable
        Log.w("WearableUtils", "sending data");
        Wearable.DataApi.putDataItem(client, putRequest.asPutDataRequest());

        return true;
    }


    public static void sendWearableMessage(GoogleApiClient googleApiClient, String messagePath) {
        sendWearableMessage(googleApiClient, messagePath, null);
    }

    public static void sendWearableMessage(GoogleApiClient googleApiClient, String messagePath, byte[] data) {
        Log.d("BoundWatchfaceActivity", "sendWearableMessage");
        Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(new NodesMessageSender(googleApiClient, messagePath, data));
    }


}
