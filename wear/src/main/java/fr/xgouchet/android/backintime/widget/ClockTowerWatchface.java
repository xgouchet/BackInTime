package fr.xgouchet.android.backintime.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.twotoasters.watchface.gears.widget.IWatchface;
import com.twotoasters.watchface.gears.widget.Watch;

import java.util.Calendar;

import fr.xgouchet.android.backintime.R;

public class ClockTowerWatchface extends FrameLayout implements IWatchface {

    private ImageView mFrame;
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
        mWatch.setFormat24Hour("H:mm");
        mWatch.setFormat12Hour("h:mm a");

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mFrame = (ImageView) findViewById(R.id.frame);
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

        if (!mInflated) {
            return;
        }

        Log.d("ClockTowerWatchface", "onTimeChanged");

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
            int frame, hour, minute;

            if (mActive) {
                frame = R.drawable.clock_tower_background;
                hour = R.drawable.clock_tower_hand_hour_b;
                minute = R.drawable.clock_tower_hand_minute_b;
            } else {
                frame = R.drawable.clock_tower_background_d;
                hour = R.drawable.clock_tower_hand_hour_d;
                minute = R.drawable.clock_tower_hand_minute_d;
            }


            mFrame.setImageResource(frame);
            mHandHour.setImageResource(hour);
            mHandMinute.setImageResource(minute);
        }
    }


}
