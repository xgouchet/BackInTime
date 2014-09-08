package fr.xgouchet.android.bttf.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.twotoasters.watchface.gears.activity.GearsWatchfaceActivity;

import java.util.List;

/**
 *
 */
public abstract class BoundWatchfaceActivity extends GearsWatchfaceActivity implements DataApi.DataListener {

    private GoogleApiClient mGoogleApiClient;
    private WearableConnectionHandler mWearableConnectionHandler;
    private boolean mRequestConfigWhenConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWearableConnectionHandler = new WearableConnectionHandler();

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addConnectionCallbacks(mWearableConnectionHandler);
        builder.addOnConnectionFailedListener(mWearableConnectionHandler);
        builder.addApi(Wearable.API);

        mGoogleApiClient = builder.build();

        mRequestConfigWhenConnected = true;
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("BoundWatchfaceActivity", "onDataChanged");

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for (DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri != null ? uri.getPath() : null;

            if ("/CONFIG".equals(path)) {
                Log.d("BoundWatchfaceActivity", "got '/CONFIG'");
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                onPreferencesMapReceived(map);
            }
        }
    }


    protected abstract void onPreferencesMapReceived(DataMap map);

    /**
     * The class handling connection with the device
     */
    public class WearableConnectionHandler implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener, ResultCallback<NodeApi.GetConnectedNodesResult> {

        @Override
        public void onConnected(Bundle bundle) {
            Log.d("WearableConnectionHandler", "onConnected");


            // add the listener
            Wearable.DataApi.addListener(mGoogleApiClient, BoundWatchfaceActivity.this);

            // get the list of nodes
            if (mRequestConfigWhenConnected) {
                mRequestConfigWhenConnected = false;
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(this);
            }
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

        @Override
        public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
            requestWearablePreferences(getConnectedNodesResult.getNodes());
        }


        private void requestWearablePreferences(List<Node> nodes) {
            Log.d("BoundWatchfaceActivity", "requestWearablePreferences (" + nodes.size() + " nodes)");

            for (Node node : nodes) {
                Log.d("BoundWatchfaceActivity", "Contacting node " + node.getDisplayName() + " (" + node.getId() + ")");
                Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/REQUEST_CONFIG", null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        Log.d("BoundWatchfaceActivity", "SendMessageResult onResult (" + sendMessageResult.getStatus() + ")");
                    }
                });
            }
        }
    }
}
