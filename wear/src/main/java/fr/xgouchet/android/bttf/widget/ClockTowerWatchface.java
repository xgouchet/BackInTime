package fr.xgouchet.android.bttf.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.twotoasters.watchface.gears.widget.IWatchface;
import com.twotoasters.watchface.gears.widget.Watch;

import java.util.Calendar;

import fr.xgouchet.android.bttf.R;

public class ClockTowerWatchface extends FrameLayout implements IWatchface {

    private ImageView mBackground;
    private ImageView mHandHour;
    private ImageView mHandMinute;

    private Watch mWatch;

    private boolean mInflated;
    private boolean mActive;

    public ClockTowerWatchface(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ClockTowerWatchface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ClockTowerWatchface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        if (isInEditMode()) {
            return;
        }
        mWatch = new Watch(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mBackground = (ImageView) findViewById(R.id.background);
        mHandHour = (ImageView) findViewById(R.id.hand_hour);
        mHandMinute = (ImageView) findViewById(R.id.hand_minute);


        mInflated = true;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isInEditMode()) {
            return;
        }
        mWatch.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isInEditMode()) {
            return;
        }
        mWatch.onDetachedFromWindow();
    }

    private void rotateHands(int hour, int minute) {
        int rotHr = (int) (30 * hour + 0.5f * minute);
        int rotMin = 6 * minute;

        mHandHour.setRotation(rotHr);
        mHandMinute.setRotation(rotMin);
    }

    @Override
    public void onTimeChanged(Calendar time) {

        int hr = time.get(Calendar.HOUR_OF_DAY) % 12;
        int min = time.get(Calendar.MINUTE);

        rotateHands(hr, min);
        invalidate();
    }

    @Override
    public void onActiveStateChanged(boolean active) {
        this.mActive = active;
        setImageResources();
    }

    @Override
    public boolean handleSecondsInDimMode() {
        return false;
    }

    private void setImageResources() {
        if (mInflated) {
            mBackground.setImageResource(mActive ? R.drawable.clock_tower_background : R.drawable.clock_tower_background_dimmed);
            mHandHour.setImageResource(mActive ? R.drawable.clock_tower_hand_hour : R.drawable.clock_tower_hand_hour_dimmed);
            mHandMinute.setImageResource(mActive ? R.drawable.clock_tower_hand_minute : R.drawable.clock_tower_hand_minute_dimmed);
        }
    }

}
