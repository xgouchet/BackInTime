package fr.xgouchet.android.backintime.activity;

import com.twotoasters.watchface.gears.activity.GearsWatchfaceActivity;
import com.twotoasters.watchface.gears.widget.IWatchface;

import fr.xgouchet.android.backintime.R;

public class GroundHogWatchFaceActivity extends GearsWatchfaceActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.groundhog_watchface;
    }

    @Override
    protected IWatchface getWatchface() {
        return (IWatchface) findViewById(R.id.watchface);
    }

}
