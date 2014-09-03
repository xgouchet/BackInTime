package fr.xgouchet.android.bttf.timecircuits;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;

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


    @TargetApi(14)
    public static Calendar getNextCalendarEvent(Context context) {
        long nextEventTimestamp = Long.MAX_VALUE;
        long nowTimestamp = System.currentTimeMillis();
        long eventStartTimestamp = Long.MAX_VALUE;

        Cursor cur;
        ContentResolver cr = context.getContentResolver();


        // fetch all available calendar events
        Uri uri = CalendarContract.Events.CONTENT_URI;
        cur = cr.query(uri, CALENDAR_EVENTS_PROJECTION, null, null, null);

        // find the next single event
        if (cur != null) {
            while (cur.moveToNext()) {
                eventStartTimestamp = cur.getLong(PROJ_DATE_START_INDEX);

                if ((eventStartTimestamp < nextEventTimestamp) && (eventStartTimestamp > nowTimestamp)) {
                    nextEventTimestamp = eventStartTimestamp;
                }
            }
        }

        // fetch all recursive event occurences from now until one month from now
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, nowTimestamp);
        ContentUris.appendId(builder, (nowTimestamp + MONTH_MS));
        cur = cr.query(builder.build(), INSTANCE_PROJECTION, null, null, null);

        if (cur != null) {
            while (cur.moveToNext()) {
                eventStartTimestamp = cur.getLong(PROJ_INSTANCE_BEGIN_INDEX);

                if ((eventStartTimestamp < nextEventTimestamp) && (eventStartTimestamp > nowTimestamp)) {
                    nextEventTimestamp = eventStartTimestamp;
                }
            }
        }


        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(nextEventTimestamp);
        return result;
    }
}
