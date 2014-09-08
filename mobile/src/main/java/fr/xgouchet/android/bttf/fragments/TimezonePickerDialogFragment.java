package fr.xgouchet.android.bttf.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.xmlpull.v1.XmlPullParserException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import fr.xgouchet.android.bttf.R;

/**
 * A dialog fragment displaying timezones
 */
public class TimezonePickerDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {


    private static final String KEY_ID = "id";  // value: String
    private static final String KEY_DISPLAYNAME = "name";  // value: String
    private static final String KEY_GMT = "gmt";  // value: String
    private static final String KEY_OFFSET = "offset";  // value: int (Integer)
    private static final String XMLTAG_TIMEZONE = "timezone";

    private static final SimpleDateFormat mTimeZoneNameFormatter = new SimpleDateFormat("zzzz");
    private static final SimpleDateFormat mTimeZoneGMTFormatter = new SimpleDateFormat("ZZZZ");


    /**
     * Constructs an adapter with TimeZone list, sorted by TimeZone.
     */
    public static SimpleAdapter buildTimeZoneAdapter(Context context) {
        return buildTimeZoneAdapter(context, R.layout.item_timezone);
    }

    /**
     * Constructs an adapter with TimeZone list, sorted by TimeZone.
     *
     * @param layoutId the layout to use to display the timezone. It should contains at least two
     *                 TextViews with ids `android.R.id.text1` and `android.R.id.text2`
     */
    public static SimpleAdapter buildTimeZoneAdapter(Context context, int layoutId) {

        // create the keys
        final String[] from = new String[]{KEY_DISPLAYNAME, KEY_GMT};
        final int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        final String sortKey = KEY_OFFSET;

        // create the time zones list
        final List<HashMap<String, Object>> sortedList = getTimeZones(context);


        // TODO final MyComparator comparator = new MyComparator(sortKey);
        // TODO Collections.sort(sortedList, comparator);
        final SimpleAdapter adapter = new SimpleAdapter(context, sortedList, layoutId, from, to);

        return adapter;
    }


    private static List<HashMap<String, Object>> getTimeZones(Context context) {

        Resources resources = context.getResources();
        String packageName = context.getPackageName();

        List<HashMap<String, Object>> timezones = new ArrayList<HashMap<String, Object>>();

        try {
            XmlResourceParser xrp = context.getResources().getXml(R.xml.timezones);

            // root element start
            while (xrp.next() != XmlResourceParser.START_TAG) {
                continue;
            }
            xrp.next();

            // loop on all children of root
            while (xrp.getEventType() != XmlResourceParser.END_TAG) {

                // loop till the next start
                while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                    if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                        return timezones;
                    }
                    xrp.next();
                }

                // check we're on a <timezone> entry
                if (xrp.getName().equals(XMLTAG_TIMEZONE)) {

                    String olsonId = xrp.getAttributeValue(0);
                    addTimeZone(timezones, olsonId, resources, packageName);
                }
                while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                    xrp.next();
                }
                xrp.next();
            }
            xrp.close();
        } catch (XmlPullParserException xppe) {
            Log.e("ZonePickerDialogFragment", "Ill-formatted timezones.xml file");
        } catch (java.io.IOException ioe) {
            Log.e("ZonePickerDialogFragment", "Unable to read timezones.xml file");
        }

        return timezones;
    }

    private static void addTimeZone(List<HashMap<String, Object>> timezones, String olsonId, Resources resources, String packageName) {
        // We always need the "GMT-07:00" string.
        final TimeZone timezone = TimeZone.getTimeZone(olsonId);

        // get the gmt name (GMT+00) and offset
        long now = System.currentTimeMillis();
        mTimeZoneNameFormatter.setTimeZone(timezone);
        mTimeZoneGMTFormatter.setTimeZone(timezone);


        final HashMap<String, Object> map = new HashMap<>();
        map.put(KEY_ID, olsonId);
        map.put(KEY_DISPLAYNAME, mTimeZoneNameFormatter.format(now));
        map.put(KEY_GMT, mTimeZoneGMTFormatter.format(now));
        map.put(KEY_OFFSET, timezone.getOffset(now));

        timezones.add(map);
    }


    public static interface OnTimeZonePickedListener {
        void onTimeZonePicked(String timezoneId);
    }

    private ListView mTimeZonesListView;
    private SimpleAdapter mTimeZonesAdapter;

    private OnTimeZonePickedListener mOnTimeZonePickedListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pref_time_zone);

        builder.setNeutralButton(android.R.string.cancel, null);

        mTimeZonesAdapter = buildTimeZoneAdapter(getActivity());

        mTimeZonesListView = new ListView(getActivity());
        mTimeZonesListView.setAdapter(mTimeZonesAdapter);
        mTimeZonesListView.setOnItemClickListener(this);

        builder.setView(mTimeZonesListView);

        return builder.create();
    }

    public void setOnTimeZonePickedListener(OnTimeZonePickedListener listener) {
        mOnTimeZonePickedListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> item = (HashMap<String, String>) mTimeZonesAdapter.getItem(position);

        if (mOnTimeZonePickedListener != null) {
            mOnTimeZonePickedListener.onTimeZonePicked(item.get(KEY_ID));
        }

        dismiss();
    }
}
