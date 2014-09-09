package fr.xgouchet.android.bttf.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.twotoasters.watchface.gears.activity.GearsWatchfaceActivity;

import java.util.List;

import fr.xgouchet.android.bttf.common.PreferencesUtils;
import fr.xgouchet.android.bttf.common.WearableConnectionHandler;
import fr.xgouchet.android.bttf.common.WearableUtils;

/**
 *
 */
public abstract class BoundWatchfaceActivity extends GearsWatchfaceActivity {

    private GoogleApiClient mGoogleApiClient;

    private boolean mRequestConfigWhenConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = WearableUtils.buildWearableClient(this, mWearableConnectionHandler);
        Log.d("BoundWatchfaceActivity", "Connecting");
        mGoogleApiClient.connect();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Log.d("BoundWatchfaceActivity", "Disconnecting");
            Wearable.DataApi.removeListener(mGoogleApiClient, mDataListener);
            mGoogleApiClient.disconnect();
        }
    }


    /**
     * Triggered when the preferences were updated on the linked device (called on UI thread)
     */
    protected abstract void onPreferencesChanged();

    /**
     * Triggered when a message is received
     *
     * @param path the message path
     * @param data the message payload
     */
    protected abstract void onMessageReceived(String path, byte[] data);

    /**
     * @return the google api client
     */
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    private DataApi.DataListener mDataListener = new DataApi.DataListener() {
        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {
            Log.d("BoundWatchfaceActivity", "onDataChanged");

            final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
            for (DataEvent event : events) {
                final Uri uri = event.getDataItem().getUri();
                final String path = uri != null ? uri.getPath() : null;

                if (WearableUtils.PATH_PREFERENCES.equals(path)) {
                    Log.d("BoundWatchfaceActivity", "got '/CONFIG'");
                    final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

                    PreferencesUtils.readDataMap(BoundWatchfaceActivity.this, map);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onPreferencesChanged();
                        }
                    });
                }
            }
        }
    };

    private MessageApi.MessageListener mMessageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            Log.d("BoundWatchfaceActivity", "onMessageReceived");
            BoundWatchfaceActivity.this.onMessageReceived(messageEvent.getPath(), messageEvent.getData());
        }
    };

    private final WearableConnectionHandler mWearableConnectionHandler = new WearableConnectionHandler() {
        @Override
        public void onConnected(Bundle bundle) {
            // add the listener
            Log.d("BoundWatchfaceActivity", "Add data listener");
            Wearable.DataApi.addListener(mGoogleApiClient, mDataListener);
            Wearable.MessageApi.addListener(mGoogleApiClient, mMessageListener);

            // get the list of nodes
            Log.d("BoundWatchfaceActivity", "Request Nodes");
            WearableUtils.sendWearableMessage(mGoogleApiClient, WearableUtils.PATH_UPDATE_REQUEST);
        }
    };


}
