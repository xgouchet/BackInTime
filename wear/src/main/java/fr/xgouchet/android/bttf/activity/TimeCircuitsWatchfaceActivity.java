package fr.xgouchet.android.bttf.activity;

import android.util.Log;

import com.twotoasters.watchface.gears.widget.IWatchface;

import java.nio.ByteBuffer;

import fr.xgouchet.android.bttf.R;
import fr.xgouchet.android.bttf.common.PreferencesUtils;
import fr.xgouchet.android.bttf.common.WearableUtils;
import fr.xgouchet.android.bttf.timecircuits.TimeCircuitsSource;
import fr.xgouchet.android.bttf.timecircuits.TimeCircuitsUtils;
import fr.xgouchet.android.bttf.widget.TimeCircuitsWatchface;

public class TimeCircuitsWatchfaceActivity extends BoundWatchfaceActivity {


    @Override
    protected int getLayoutResId() {
        return R.layout.timecircuits_watchface;
    }

    @Override
    protected IWatchface getWatchface() {
        return (IWatchface) findViewById(R.id.watchface);
    }


    @Override
    protected void onPreferencesChanged() {

        if (TimeCircuitsSource.needCalendarsEvents(this)) {
            WearableUtils.sendWearableMessage(getGoogleApiClient(), WearableUtils.PATH_CALENDAR_REQUEST);
        }

        ((TimeCircuitsWatchface) getWatchface()).onPreferenceChanged();
    }

    @Override
    protected void onMessageReceived(String path, byte[] data) {
        if (WearableUtils.PATH_CALENDAR.equals(path)) {
            Log.d("TimeCircuitsWatchfaceActivity", "onMessageReceived : Calendar values");
            ByteBuffer buffer = ByteBuffer.wrap(data);

            PreferencesUtils.setStringPreference(this, TimeCircuitsUtils.PREF_DEPARTED_CALENDAR, Long.toString(buffer.getLong()));
            PreferencesUtils.setStringPreference(this, TimeCircuitsUtils.PREF_DESTINATION_CALENDAR, Long.toString(buffer.getLong()));
        }
    }
}
