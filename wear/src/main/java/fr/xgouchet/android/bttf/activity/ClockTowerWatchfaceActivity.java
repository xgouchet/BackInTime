package fr.xgouchet.android.bttf.activity;

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
    protected void onPreferencesChanged() {
    }

    @Override
    protected void onMessageReceived(String path, byte[] data) {
    }
}
