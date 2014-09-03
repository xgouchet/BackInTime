package fr.xgouchet.android.bttf.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 *
 */
public class TimeCircuitsService extends Service {

    private static TimeCircuitsUpdateThread sThread;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("TimeCircuitsService", "onStartCommand");

        if (sThread == null) {
            Log.d("TimeCircuitsService", "start background thread");
            sThread = new TimeCircuitsUpdateThread(getBaseContext());
            sThread.start();
        } else {
            Log.d("TimeCircuitsService", "awake background thread");
            sThread.forceUpdate();
            sThread.interrupt();
        }

        return START_STICKY;
    }


    /**
     * @param context the current application context
     */
    public static void startService(Context context) {
        if (sThread == null) {
            Intent i = new Intent(context, TimeCircuitsService.class);
            context.startService(i);
        } else {
            sThread.forceUpdate();
            sThread.interrupt();
        }
    }

    /**
     *
     */
    public static void stopService() {
        if (sThread != null) {
            sThread.setRunning(false);
            try {
                sThread.join();
            } catch (InterruptedException e) {
                // pass
            }
            sThread = null;
        }
    }
}
