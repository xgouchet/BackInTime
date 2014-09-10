package fr.xgouchet.android.bttf.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import fr.xgouchet.android.bttf.R;
import fr.xgouchet.android.bttf.clocktower.ClockTowerUtils;
import fr.xgouchet.android.bttf.common.PreferencesUtils;
import fr.xgouchet.android.bttf.utils.WidgetUtils;

/**
 */
public class ClockTowerWidgetProvider extends AppWidgetProvider {

    /**
     * Triggers an update on all clock widgets
     *
     * @param context the current context
     */
    public static void triggerUpdate(Context context) {
        if (context == null) {
            return;
        }

        AppWidgetManager awm = AppWidgetManager.getInstance(context.getApplicationContext());
        int ids[] = awm.getAppWidgetIds(new ComponentName(context, ClockTowerWidgetProvider.class));

        Intent intent = new Intent(context, ClockTowerWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

        context.sendBroadcast(intent);
    }

    /**
     * @see android.appwidget.AppWidgetProvider#onUpdate(android.content.Context,
     * android.appwidget.AppWidgetManager, int[])
     */
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        AppWidgetManager manager;
        ComponentName widgetName;

        manager = AppWidgetManager.getInstance(context);
        widgetName = new ComponentName(context, ClockTowerWidgetProvider.class);
        int[] widgetIds = manager.getAppWidgetIds(widgetName);

        PendingIntent pendingIntent = WidgetUtils.getAlarmClockIntent(context);

        int layout;
        switch (PreferencesUtils.getStringPreference(context, ClockTowerUtils.PREF_CLOCK_FRAME_THEME, ClockTowerUtils.THEME_FRAME_BOW)) {
            case ClockTowerUtils.THEME_FRAME_BOT:
                layout = R.layout.clock_tower_widget_bot;
                break;
            case ClockTowerUtils.THEME_FRAME_WOB:
                layout = R.layout.clock_tower_widget_wob;
                break;
            case ClockTowerUtils.THEME_FRAME_WOT:
                layout = R.layout.clock_tower_widget_wot;
                break;
            case ClockTowerUtils.THEME_FRAME_BOW:
            default:
                layout = R.layout.clock_tower_widget_bow;
                break;
        }

        for (int i = 0; i < widgetIds.length; i++) {

            RemoteViews views = new RemoteViews(context.getPackageName(), layout);
            views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

            appWidgetManager.updateAppWidget(widgetIds[i], views);
        }
    }


}

