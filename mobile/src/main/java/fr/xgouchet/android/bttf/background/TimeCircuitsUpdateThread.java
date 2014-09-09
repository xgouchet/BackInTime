package fr.xgouchet.android.bttf.background;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import fr.xgouchet.android.bttf.R;
import fr.xgouchet.android.bttf.timecircuits.TimeCircuitsRenderer;
import fr.xgouchet.android.bttf.timecircuits.TimeCircuitsSource;
import fr.xgouchet.android.bttf.utils.WidgetUtils;
import fr.xgouchet.android.bttf.widget.TimeCircuitsWidgetProvider;

/**
 *
 */
public class TimeCircuitsUpdateThread extends Thread {

    private static final long MINUTE = 60 * 1000;

    private Context mContext;
    private boolean mRunning;

    private TimeCircuitsRenderer mTimeCircuitsRenderer;
    private AppWidgetManager mAppWidgetManager;
    private PowerManager mPowerManager;

    private final Calendar mPresentTime;
    private Object mDestinationTime, mDepartedTime;
    private String mLastTime;

    private DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private long mDelayBeforeNextUpdate;
    private long mDelayBetweenUpdates;
    private long mLastMinute;
    private boolean mForceUpdate;

    public TimeCircuitsUpdateThread(Context context) {
        super("FluxCapacitor");
        mContext = context;

        mTimeCircuitsRenderer = new TimeCircuitsRenderer(context, "bttf_tc.ttf", true);
        mAppWidgetManager = AppWidgetManager.getInstance(context);
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        mPresentTime = Calendar.getInstance();

        mRunning = true;
    }

    public void setRunning(boolean running) {
        mRunning = running;
    }

    public void run() {

        try {
            while (mRunning) {
                onRunIteration();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onRunIteration() {

        long now = System.currentTimeMillis();

        if (checkTimeNeedsUpdate(now) || mForceUpdate) {
            updateDashboard(now);
            mForceUpdate = false;
        }

        waitForNextUpdate(now);
    }


    private boolean checkTimeNeedsUpdate(long now) {

        boolean needsUpdate = false;

        // compute the time until next minute change
        long minute = now / MINUTE;
        mDelayBeforeNextUpdate = MINUTE - (now - (minute * MINUTE));
        Log.d("TimeCircuitsUpdateThread", "Minute : " + mLastMinute + " / " + minute + " / " + mDelayBeforeNextUpdate);

        if (mLastMinute != minute) {
            mLastMinute = minute;
            needsUpdate = true;
        }


        return needsUpdate;
    }

    private void waitForNextUpdate(long now) {
        if (mDelayBeforeNextUpdate > 0) {
            try {
                synchronized (this) {
                    if (mDelayBeforeNextUpdate > 0) {
                        wait(mDelayBeforeNextUpdate);
                    }
                }
            } catch (InterruptedException e) {
                // pass
            }
        }
    }

    public void forceUpdate() {
        mForceUpdate = true;
    }

    private void updateDashboard(long now) {
        updateTimes(now);

        String present = FORMAT.format(mPresentTime.getTime());
        if (mForceUpdate || (!TextUtils.equals(present, mLastTime))) {
            mLastTime = present;
            updateWidgets();
        }
    }

    private void updateTimes(long now) {
        mPresentTime.setTimeInMillis(now);
        mDestinationTime = TimeCircuitsSource.getDestinationTime(mContext);
        mDepartedTime = TimeCircuitsSource.getDepartedTime(mContext);
    }


    private void updateWidgets() {
        // Render the image
        mTimeCircuitsRenderer.renderDashboard(mContext, mDestinationTime, mPresentTime, mDepartedTime, false);
        Bitmap bitmap = mTimeCircuitsRenderer.getBitmap();

        // get the click intent
        PendingIntent pendingIntent = WidgetUtils.getAlarmClockIntent(mContext);

        // update all widgets
        updateWidgets(mContext, bitmap, pendingIntent);
    }

    /**
     * Actually update all widgets with the given bitmap and intent
     */
    private void updateWidgets(Context context, Bitmap bitmap, PendingIntent pendingIntent) {

        // get all available widgets ids
        int[] ids = mAppWidgetManager.getAppWidgetIds(new ComponentName(context, TimeCircuitsWidgetProvider.class));


        if (ids != null && ids.length > 0) {

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.time_circuits_widget);

            views.setOnClickPendingIntent(R.id.dashboard, pendingIntent);
            views.setImageViewBitmap(R.id.dashboard, bitmap);

            // update all widgets
            for (int i = 0; i < ids.length; i++) {
                mAppWidgetManager.updateAppWidget(ids[i], views);
            }
        }
    }


}
