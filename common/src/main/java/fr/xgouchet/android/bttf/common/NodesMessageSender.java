package fr.xgouchet.android.bttf.common;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

/**
 * Created by xgouchet on 9/9/14.
 */
public class NodesMessageSender implements ResultCallback<NodeApi.GetConnectedNodesResult> {

    String mMessagePath;
    byte[] mData;
    private GoogleApiClient mGoogleApiClient;

    public NodesMessageSender(GoogleApiClient googleApiClient, String messagePath) {
        this(googleApiClient, messagePath, null);
    }

    public NodesMessageSender(GoogleApiClient googleApiClient, String messagePath, byte[] data) {
        mMessagePath = messagePath;
        mData = data;
        mGoogleApiClient = googleApiClient;
    }

    @Override
    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
        List<Node> nodes = getConnectedNodesResult.getNodes();

        Log.d("BoundWatchfaceActivity", "requestWearablePreferences (" + nodes.size() + " nodes)");

        for (Node node : nodes) {
            Log.d("BoundWatchfaceActivity", "Contacting node " + node.getDisplayName() + " (" + node.getId() + ")");
            Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), mMessagePath, mData)
                    .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Log.d("BoundWatchfaceActivity", "SendMessageResult onResult (" + sendMessageResult.getStatus() + ")");
                        }
                    });
        }
    }
}
