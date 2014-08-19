package fr.xgouchet.android.bttf.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import fr.xgouchet.android.bttf.R;
import fr.xgouchet.android.bttf.utils.WidgetUtils;

/**
 */
public class ClockTowerWidgetProvider extends AppWidgetProvider {

    /**
     * @see android.appwidget.AppWidgetProvider#onUpdate(android.content.Context,
     *      android.appwidget.AppWidgetManager, int[])
     */
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        AppWidgetManager manager;
        ComponentName widgetName;

        manager = AppWidgetManager.getInstance(context);
        widgetName = new ComponentName(context, ClockTowerWidgetProvider.class);
        int[] widgetIds = manager.getAppWidgetIds(widgetName);

        PendingIntent pendingIntent = WidgetUtils.getAlarmClockIntent(context);

        for (int i = 0; i < widgetIds.length; i++) {

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.clock_tower_widget);
            views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

            appWidgetManager.updateAppWidget(widgetIds[i], views);
        }
    }


}

