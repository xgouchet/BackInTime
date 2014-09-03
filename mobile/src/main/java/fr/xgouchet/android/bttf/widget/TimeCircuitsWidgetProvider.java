package fr.xgouchet.android.bttf.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;

import java.util.Calendar;

import fr.xgouchet.android.bttf.background.TimeCircuitsService;

/**
 */
public class TimeCircuitsWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_FORCE_UPDATE = "fr.xgouchet.android.bttf.widget.action.FORCE_UPDATE";

    private Calendar mTime;
    private String mTimeZone;

    private boolean mStarted;


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        Log.d("TimeCircuitsWidgetProvider", "onEnabled");
        TimeCircuitsService.startService(context);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.d("TimeCircuitsWidgetProvider", "onUpdate");
        TimeCircuitsService.startService(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        Log.d("TimeCircuitsWidgetProvider", "onDisabled");
        TimeCircuitsService.stopService();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }


}
