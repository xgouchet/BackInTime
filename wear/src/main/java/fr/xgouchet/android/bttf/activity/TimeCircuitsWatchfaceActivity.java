package fr.xgouchet.android.bttf.activity;

import com.google.android.gms.wearable.DataMap;
import com.twotoasters.watchface.gears.widget.IWatchface;

import fr.xgouchet.android.bttf.R;
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
    protected void onPreferencesMapReceived(DataMap map) {
        TimeCircuitsWatchface watchface = (TimeCircuitsWatchface) getWatchface();

    }
}
