package fr.xgouchet.android.backintime.activity;

import com.twotoasters.watchface.gears.activity.GearsWatchfaceActivity;
import com.twotoasters.watchface.gears.widget.IWatchface;

import fr.xgouchet.android.backintime.R;

public class TimeCircuitsWatchFaceActivity extends GearsWatchfaceActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.timecircuits_watchface;
    }

    @Override
    protected IWatchface getWatchface() {
        return (IWatchface) findViewById(R.id.watchface);
    }

}
