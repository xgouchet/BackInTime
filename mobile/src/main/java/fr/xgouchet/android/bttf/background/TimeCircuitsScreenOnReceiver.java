package fr.xgouchet.android.bttf.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimeCircuitsScreenOnReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TimeCircuitsScreenOnReceiver ", "onReceive(" + intent.getAction() + ")");

        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            TimeCircuitsService.stopService();
        }

        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            TimeCircuitsService.startService(context);
        }
    }
}
