package fr.xgouchet.android.bttf.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.twotoasters.watchface.gears.widget.IWatchface;
import com.twotoasters.watchface.gears.widget.Watch;

import java.util.Calendar;

import fr.xgouchet.android.bttf.R;
import fr.xgouchet.android.bttf.WatchfaceUtils;
import fr.xgouchet.android.bttf.timecircuits.TimeCircuitsRenderer;
import fr.xgouchet.android.bttf.timecircuits.TimeCircuitsSource;
import fr.xgouchet.android.bttf.timecircuits.TimeCircuitsUtils;


public class TimeCircuitsWatchface extends FrameLayout implements IWatchface {


    private ImageView mDashboard;

    private Watch mWatch;

    private boolean mInflated;
    private boolean mActive;
    private TimeCircuitsRenderer mRenderer;

    private Calendar mPresentTime;
    private Object mDestinationTime, mDepartedTime;


    public TimeCircuitsWatchface(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TimeCircuitsWatchface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TimeCircuitsWatchface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        Log.d("TimeCircuitsWatchface", "init");

        if (isInEditMode()) {
            return;
        }

        // Create Watch
        mWatch = new Watch(this);
        mWatch.setFormat24Hour("H:mm");
        mWatch.setFormat12Hour("h:mm a");

        // Create Renderer
        mRenderer = new TimeCircuitsRenderer(context, "bttf_tc.ttf", false);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Log.d("TimeCircuitsWatchface", "onFinishInflate");

        mDashboard = (ImageView) findViewById(R.id.dashboard);

        mInflated = true;
    }

    @Override
    public void onAttachedToWindow() {
        Log.d("TimeCircuitsWatchface", "onAttachedToWindow");

        super.onAttachedToWindow();

        if (isInEditMode()) {
            return;
        }

        mWatch.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {

        Log.d("TimeCircuitsWatchface", "onDetachedFromWindow");

        super.onDetachedFromWindow();

        if (isInEditMode()) {
            return;
        }

        mWatch.onDetachedFromWindow();
    }


    @Override
    public void onTimeChanged(Calendar time) {
        mPresentTime = time;
        update();
    }

    public void onPreferenceChanged() {
        update();
    }

    @Override
    public void onActiveStateChanged(boolean active) {
        Log.d("onActiveStateChanged", "onTimeChanged");
        mActive = active;

        // setBackgroundResource(mActive ? R.drawable.background : R.drawable.background_dimmed);

        updateDashboard();
    }

    private void update() {
        if (!mInflated) {
            return;
        }

        Log.d("TimeCircuitsWatchface", "onTimeChanged");

        mDestinationTime = TimeCircuitsSource.getDestinationTime(getContext());
        mDepartedTime = TimeCircuitsSource.getDepartedTime(getContext());

        updateDashboard();
    }

    private void updateDashboard() {
        Context context = getContext();


        mRenderer.renderDashboard(context, mDestinationTime, mPresentTime, mDepartedTime, !mActive);
        mDashboard.setImageBitmap(mRenderer.getBitmap());

        int background = WatchfaceUtils.getWatchfaceBackgroundResource(context, TimeCircuitsUtils.PREF_WATCHFACE_BACKGROUND);
        setBackgroundResource(mActive ? background : R.drawable.background_black);

        invalidate();
    }


    @Override
    public boolean handleSecondsInDimMode() {
        return false;
    }


}
