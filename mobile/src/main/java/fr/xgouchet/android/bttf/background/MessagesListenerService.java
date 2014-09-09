package fr.xgouchet.android.bttf.background;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import fr.xgouchet.android.bttf.common.WearableConnectionHandler;
import fr.xgouchet.android.bttf.common.WearableUtils;
import fr.xgouchet.android.bttf.timecircuits.TimeSource;

/**
 * Service listening to the Messages from a connected Wearable
 */
public class MessagesListenerService extends WearableListenerService {

    private Queue<Runnable> mOnConnectActions = new LinkedList<>();
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        Log.i("MessagesListenerService", "onMessageReceived");


        if (messageEvent.getPath() == null) {
            Log.i("MessagesListenerService", "onMessageReceived with null path");
            return;
        }

        switch (messageEvent.getPath()) {
            case WearableUtils.PATH_UPDATE_REQUEST:
                Log.w("MessagesListenerService", "Request for preferences update");
                connectAndSendPreferences();
                break;
            case WearableUtils.PATH_CALENDAR_REQUEST:
                Log.w("MessagesListenerService", "Request for calendar update");
                connectAndSendCalendar();
                break;
            default:
                Log.w("MessagesListenerService", "onMessageReceived with unknown path : " + messageEvent.getPath());
                break;
        }

    }

    private void connectAndSendPreferences() {
        connectAndPerformAction(new Runnable() {
            @Override
            public void run() {
                WearableUtils.updateWearablePreferences(MessagesListenerService.this, mGoogleApiClient);
            }
        });
    }

    private void connectAndSendCalendar() {
        connectAndPerformAction(new Runnable() {
            @Override
            public void run() {

                ByteBuffer buffer = ByteBuffer.allocate(2 * Long.SIZE);
                buffer.putLong(TimeSource.getLastCalendarEvent(MessagesListenerService.this).getTimeInMillis());
                buffer.putLong(TimeSource.getNextCalendarEvent(MessagesListenerService.this).getTimeInMillis());

                WearableUtils.sendWearableMessage(mGoogleApiClient, WearableUtils.PATH_CALENDAR, buffer.array());
            }
        });
    }


    private void connectAndPerformAction(Runnable action) {
        // create client if needed
        if (mGoogleApiClient == null) {
            mGoogleApiClient = WearableUtils.buildWearableClient(this, mWearableConnectionHandler);
        }

        // connect if needed
        if (mGoogleApiClient.isConnected()) {
            Log.d("MessagesListenerService", "Already connected, update prefs");
            action.run();
        } else if (!mGoogleApiClient.isConnecting()) {
            mOnConnectActions.add(action);
            mGoogleApiClient.connect();
        }
    }

    private WearableConnectionHandler mWearableConnectionHandler = new WearableConnectionHandler() {
        @Override
        public void onConnected(Bundle bundle) {
            super.onConnected(bundle);

            Iterator<Runnable> actions = mOnConnectActions.iterator();

            while (actions.hasNext()) {
                Runnable action = actions.next();
                action.run();
                actions.remove();
            }
        }
    };


}
