package fr.xgouchet.android.bttf.activity;

import com.google.android.gms.wearable.DataMap;
import com.twotoasters.watchface.gears.activity.GearsWatchfaceActivity;
import com.twotoasters.watchface.gears.widget.IWatchface;

import fr.xgouchet.android.bttf.R;

public class ClockTowerWatchfaceActivity extends BoundWatchfaceActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.clocktower_watchface;
    }

    @Override
    protected IWatchface getWatchface() {
        return (IWatchface) findViewById(R.id.watchface);
    }

    @Override
    protected void onPreferencesMapReceived(DataMap map) {

    }
}
