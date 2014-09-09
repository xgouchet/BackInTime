package fr.xgouchet.android.bttf.timecircuits;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.TimeZone;

import fr.xgouchet.android.bttf.common.PreferencesUtils;

/**
 *
 */
public class TimeSource {

    private static final long MIN_MS = 60 * 1000;
    private static final long HOUR_MS = 60 * MIN_MS;
    private static final long DAY_MS = 24 * HOUR_MS;
    private static final long WEEK_MS = 7 * DAY_MS;
    private static final long MONTH_MS = 4 * WEEK_MS;

    public static final String[] CALENDAR_EVENTS_PROJECTION = new String[]{
            CalendarContract.Events._ID, // 0
            CalendarContract.Events.DTSTART, // 1
            CalendarContract.Events.DTEND, // 2
    };

    public static final String[] INSTANCE_PROJECTION = new String[]{
            CalendarContract.Instances.EVENT_ID, // 0
            CalendarContract.Instances.BEGIN, // 1
            CalendarContract.Instances.END // 2
    };

    // The indices for the projection array above.
    private static final int PROJ_DATE_START_INDEX = 1;
    private static final int PROJ_DATE_END_INDEX = 2;

    private static final int PROJ_INSTANCE_BEGIN_INDEX = 1;
    private static final int PROJ_INSTANCE_END_INDEX = 2;


    /**
     * @param context the current context
     * @return the destination time to display
     */
    public static Object getDestinationTime(Context context) {
        String source = PreferencesUtils.getStringPreference(context, PreferencesUtils.PREF_DESTINATION_SOURCE);
        if (source != null) {
            switch (source) {
                case PreferencesUtils.SOURCE_FREETEXT:
                    return PreferencesUtils.getStringPreference(context, PreferencesUtils.PREF_DESTINATION_FREETEXT);
                case PreferencesUtils.SOURCE_CALENDAR:
                    return TimeSource.getNextCalendarEvent(context);
                case PreferencesUtils.SOURCE_TIMEZONE:
                    return TimeSource.getTimeZoneTime(PreferencesUtils.getStringPreference(context, PreferencesUtils.PREF_DESTINATION_TIMEZONE));
                case PreferencesUtils.SOURCE_BATTERY:
                    return TimeSource.getExpectedShutdownTime(context);
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    /**
     * @param context the current context
     * @return the departed time to display
     */
    public static Object getDepartedTime(Context context) {
        String source = PreferencesUtils.getStringPreference(context, PreferencesUtils.PREF_DEPARTED_SOURCE);
        if (source != null) {
            switch (source) {
                case PreferencesUtils.SOURCE_BATTERY:
                    return TimeSource.getBootTime();
                case PreferencesUtils.SOURCE_CALENDAR:
                    return TimeSource.getLastCalendarEvent(context);
                case PreferencesUtils.SOURCE_FREETEXT:
                    return PreferencesUtils.getStringPreference(context, PreferencesUtils.PREF_DEPARTED_FREETEXT);
                case PreferencesUtils.SOURCE_TIMEZONE:
                    return TimeSource.getTimeZoneTime(PreferencesUtils.getStringPreference(context, PreferencesUtils.PREF_DEPARTED_TIMEZONE));
                case PreferencesUtils.SOURCE_FREETIME:
                    // TODO get free time from preferences
                default:
                    return null;
            }
        } else {
            return null;
        }
    }


    /**
     * @param context the current context
     * @return the start time of the next event in Google Calendar
     */
    @TargetApi(14)
    public static Calendar getNextCalendarEvent(Context context) {
        long nextEventTimestamp = Long.MAX_VALUE;
        long nowTimestamp = System.currentTimeMillis();
        long eventStartTimestamp;

        Cursor cursor;
        ContentResolver cr = context.getContentResolver();


        // fetch all available calendar events
        Uri uri = CalendarContract.Events.CONTENT_URI;
        cursor = cr.query(uri, CALENDAR_EVENTS_PROJECTION, null, null, null);

        // find the next single event
        if (cursor != null) {
            while (cursor.moveToNext()) {
                eventStartTimestamp = cursor.getLong(PROJ_DATE_START_INDEX);

                if ((eventStartTimestamp < nextEventTimestamp) && (eventStartTimestamp > nowTimestamp)) {
                    nextEventTimestamp = eventStartTimestamp;
                }
            }
            cursor.close();
        }

        // fetch all recursive event occurences from now until one month from now
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, nowTimestamp);
        ContentUris.appendId(builder, (nowTimestamp + MONTH_MS));
        cursor = cr.query(builder.build(), INSTANCE_PROJECTION, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                eventStartTimestamp = cursor.getLong(PROJ_INSTANCE_BEGIN_INDEX);

                if ((eventStartTimestamp < nextEventTimestamp) && (eventStartTimestamp > nowTimestamp)) {
                    nextEventTimestamp = eventStartTimestamp;
                }
            }
            cursor.close();
        }

        // fail safe if no event available
        if (nextEventTimestamp == Long.MAX_VALUE) {
            String nextEventPref = PreferencesUtils.getStringPreference(context, PreferencesUtils.PREF_DESTINATION_CALENDAR);
            try {
                nextEventTimestamp = Long.valueOf(nextEventPref);
            } catch (Exception e) {
                nextEventTimestamp = nowTimestamp;
            }
        }

        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(nextEventTimestamp);
        return result;
    }

    /**
     * @param context the current context
     * @return the start time of the next event in Google Calendar
     */
    @TargetApi(14)
    public static Calendar getLastCalendarEvent(Context context) {
        long lastEventTimestamp = Long.MIN_VALUE;
        long nowTimestamp = System.currentTimeMillis();
        long eventEndTimestamp;

        Cursor cursor;
        ContentResolver cr = context.getContentResolver();


        // fetch all available calendar events
        Uri uri = CalendarContract.Events.CONTENT_URI;
        cursor = cr.query(uri, CALENDAR_EVENTS_PROJECTION, null, null, null);

        // find the next single event
        if (cursor != null) {
            while (cursor.moveToNext()) {
                eventEndTimestamp = cursor.getLong(PROJ_DATE_END_INDEX);

                if ((eventEndTimestamp > lastEventTimestamp) && (eventEndTimestamp < nowTimestamp)) {
                    lastEventTimestamp = eventEndTimestamp;
                }
            }
            cursor.close();
        }

        // fetch all recursive event occurences from now until one month from now
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, nowTimestamp);
        ContentUris.appendId(builder, (nowTimestamp + MONTH_MS));
        cursor = cr.query(builder.build(), INSTANCE_PROJECTION, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                eventEndTimestamp = cursor.getLong(PROJ_INSTANCE_END_INDEX);

                if ((eventEndTimestamp > lastEventTimestamp) && (eventEndTimestamp < nowTimestamp)) {
                    lastEventTimestamp = eventEndTimestamp;
                }
            }
            cursor.close();
        }

        // fail safe if no event available
        if (lastEventTimestamp == Long.MIN_VALUE) {
            String lastEventPref = PreferencesUtils.getStringPreference(context, PreferencesUtils.PREF_DEPARTED_CALENDAR);
            try {
                lastEventTimestamp = Long.valueOf(lastEventPref);
            } catch (Exception e) {
                lastEventTimestamp = nowTimestamp;
            }
        }

        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(lastEventTimestamp);
        return result;
    }

    /**
     * @param timeZoneId a timezone id
     * @return the current time in the given timezone
     */
    private static Calendar getTimeZoneTime(String timeZoneId) {

        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        Calendar result = Calendar.getInstance(timeZone);

        return result;
    }

    /**
     * @return the time the device booted
     */
    private static Calendar getBootTime() {

        long bootTimestamp = System.currentTimeMillis() - SystemClock.elapsedRealtime();
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(bootTimestamp);

        return result;
    }

    /**
     * @param context the current context
     * @return the time at which the battery should hit 0%
     */
    private static Calendar getExpectedShutdownTime(Context context) {
        long upTime = SystemClock.elapsedRealtime();
        long remainingTime = upTime;
        double battery = getBatteryLevel(context);

        if (battery >= 0) {
            double batteryUsed = 1 - battery;
            if (batteryUsed >= 0.01) {
                remainingTime = (long) ((battery * upTime) / batteryUsed);
            }
        }

        long shutdownTimestamp = System.currentTimeMillis() + remainingTime;
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(shutdownTimestamp);

        return result;
    }

    /**
     * @param context the current context
     * @return the current battery level as a double between 0.0 and 1.0
     */
    private static double getBatteryLevel(Context context) {

        IntentFilter batteryBroadcast = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryIntent = context.registerReceiver(null, batteryBroadcast);

        double rawLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        double scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        double level = -1;

        if (rawLevel >= 0 && scale > 0) {
            level = rawLevel / scale;
        }

        return level;
    }

    public static boolean needCalendarsEvents(Context context) {
        String destinationSource = PreferencesUtils.getStringPreference(context, PreferencesUtils.PREF_DESTINATION_SOURCE);
        String departedSource = PreferencesUtils.getStringPreference(context, PreferencesUtils.PREF_DEPARTED_SOURCE);

        return (PreferencesUtils.SOURCE_CALENDAR.equals(destinationSource) || PreferencesUtils.SOURCE_CALENDAR.equals(departedSource));
    }
}
