package fr.xgouchet.android.bttf.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import fr.xgouchet.android.bttf.R;
import fr.xgouchet.android.bttf.timecircuits.TimeCircuitsRenderer;
import fr.xgouchet.android.bttf.utils.WidgetUtils;

/**
 */
public class TimeCircuitsWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_FORCE_UPDATE = "fr.xgouchet.android.bttf.widget.action.FORCE_UPDATE";

    private Calendar mTime;
    private String mTimeZone;

    private boolean mStarted;

    private TimeCircuitsRenderer mTimeCircuitsRenderer;
    private Calendar mDestinationTime, mPresentTime, mDepartedTime;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        Log.d("TimeCircuitsWidgetProvider", "onEnabled");

        if (!mStarted) {
            createTime(null);
            registerReceiver(context);
        }

        onTimeChanged(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.d("TimeCircuitsWidgetProvider", "onUpdate");

        onTimeChanged(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        unregisterReceiver(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }


    private void onTimeChanged(Context context) {

        if (mTime == null) {
            createTime(null);
        }

        if (mTimeCircuitsRenderer == null) {
            mTimeCircuitsRenderer = new TimeCircuitsRenderer(context, "bttf_tc.ttf", true);
        }

        mTime.setTimeInMillis(System.currentTimeMillis());

        // TODO update other calendars
        mDestinationTime = new GregorianCalendar(1985, 10, 26, 01, 20, 00);
        mPresentTime = mTime;
        mDepartedTime = new GregorianCalendar(1955, 11, 12, 22, 04, 00);

        // Render the image
        mTimeCircuitsRenderer.renderDashboard(context, mDestinationTime, mPresentTime, mDepartedTime, false);
        Bitmap bitmap = mTimeCircuitsRenderer.getBitmap();

        // get the click intent
        PendingIntent pendingIntent = WidgetUtils.getAlarmClockIntent(context);

        // update all widgets
        updateWidgets(context, bitmap, pendingIntent);
    }

    private void updateWidgets(Context context, Bitmap bitmap, PendingIntent pendingIntent) {
        AppWidgetManager appWidgetMgr = AppWidgetManager.getInstance(context);

        // get all available widgets ids
        int[] ids = appWidgetMgr.getAppWidgetIds(new ComponentName(context, TimeCircuitsWidgetProvider.class));


        if (ids != null && ids.length > 0) {

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.time_circuits_widget);

            views.setOnClickPendingIntent(R.id.dashboard, pendingIntent);
            views.setImageViewBitmap(R.id.dashboard, bitmap);

            // update all widgets
            for (int i = 0; i < ids.length; i++) {
                appWidgetMgr.updateAppWidget(ids[i], views);
            }
        }
    }

    private void createTime(String timeZone) {
        if (timeZone != null) {
            mTime = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        } else {
            mTime = Calendar.getInstance();
        }
    }


    private void registerReceiver(Context context) {

        final IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(ACTION_FORCE_UPDATE);

        context.registerReceiver(mIntentReceiver, filter);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(mIntentReceiver);
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (mTime == null || Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                final String timeZone = intent.getStringExtra("time-zone");
                createTime(timeZone);
            }

            onTimeChanged(context);
        }
    };
}
