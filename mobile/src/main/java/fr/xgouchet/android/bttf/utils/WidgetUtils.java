package fr.xgouchet.android.bttf.utils;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 *
 */
public class WidgetUtils {

    /**
     * A list of clock implementations (Title, Package, Activity)
     */
    private static final String CLOCK_IMPLS[][] = {
            {"Standard Alarm Clock", "com.android.deskclock",
                    "com.android.deskclock.AlarmClock"},
            {"Standard Alarm Clock", "com.google.android.deskclock",
                    "com.android.deskclock.DeskClock"},
            {"HTC Alarm Clock", "com.htc.android.worldclock",
                    "com.htc.android.worldclock.WorldClockTabControl"},
            {"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",
                    "com.motorola.blur.alarmclock.AlarmClock"},
            {"Samsung Galaxy Clock", "com.sec.android.app.clockpackage",
                    "com.sec.android.app.clockpackage.ClockPackage"}};

    /**
     * Returns a pending to launch the alarm clock app installed on this device
     *
     * @param context the current context
     * @return the pending intent or null if no clock app was found
     */
    public static PendingIntent getAlarmClockIntent(Context context) {
        PendingIntent pendingIntent = null;
        PackageManager packageManager = context.getPackageManager();
        Intent alarmClockIntent = new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER);


        boolean foundClockImpl = false;

        for (int i = 0; i < CLOCK_IMPLS.length; i++) {
            String vendor = CLOCK_IMPLS[i][0];
            String packageName = CLOCK_IMPLS[i][1];
            String className = CLOCK_IMPLS[i][2];
            try {
                ComponentName cn = new ComponentName(packageName, className);
                packageManager
                        .getActivityInfo(cn, PackageManager.GET_META_DATA);
                alarmClockIntent.setComponent(cn);
                foundClockImpl = true;
                break;
            } catch (PackageManager.NameNotFoundException e) {
                Log.i("WidgetUtils", vendor + " does not exists");
            }
        }

        if (foundClockImpl) {
            pendingIntent = PendingIntent.getActivity(context, 0,
                    alarmClockIntent, 0);
        }

        return pendingIntent;
    }

}
