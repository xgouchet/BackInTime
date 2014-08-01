package fr.xgouchet.android.bttf.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twotoasters.watchface.gears.widget.IWatchface;
import com.twotoasters.watchface.gears.widget.Watch;

import java.util.Calendar;
import java.util.Locale;

import fr.xgouchet.android.bttf.R;


public class TimeCircuitsWatchface extends LinearLayout implements IWatchface {


    private static final String[] MONTH_NAMES = new String[]{"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};


    private static final int COLS_COUNT = 5;
    private static final int ROWS_COUNT = 3;

    private static final int IDX_COL_MONTH = 0;
    private static final int IDX_COL_DAY = 1;
    private static final int IDX_COL_YEAR = 2;
    private static final int IDX_COL_HOUR = 3;

    private static final int IDX_COL_MINUTE = 4;
    private static final int IDX_ROW_DEST = 0;
    private static final int IDX_ROW_PRESENT = IDX_ROW_DEST + COLS_COUNT;

    private static final int IDX_ROW_DEPART = IDX_ROW_PRESENT + COLS_COUNT;

    private final TextView mTextViews[] = new TextView[COLS_COUNT * ROWS_COUNT];
    private LinearLayout mRowDestination, mRowPresent, mRowDeparted;

    private Watch mWatch;

    private boolean mInflated;
    private boolean mActive;

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
        Log.d("Watchface", "init");
        if (isInEditMode()) {
            return;
        }
        mWatch = new Watch(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Log.d("Watchface", "onFinishInflate");

        mTextViews[IDX_ROW_DEST + IDX_COL_MONTH] = (TextView) findViewById(R.id.text_dest_month);
        mTextViews[IDX_ROW_DEST + IDX_COL_DAY] = (TextView) findViewById(R.id.text_dest_day);
        mTextViews[IDX_ROW_DEST + IDX_COL_YEAR] = (TextView) findViewById(R.id.text_dest_year);
        mTextViews[IDX_ROW_DEST + IDX_COL_HOUR] = (TextView) findViewById(R.id.text_dest_hour);
        mTextViews[IDX_ROW_DEST + IDX_COL_MINUTE] = (TextView) findViewById(R.id.text_dest_minute);

        mTextViews[IDX_ROW_PRESENT + IDX_COL_MONTH] = (TextView) findViewById(R.id.text_present_month);
        mTextViews[IDX_ROW_PRESENT + IDX_COL_DAY] = (TextView) findViewById(R.id.text_present_day);
        mTextViews[IDX_ROW_PRESENT + IDX_COL_YEAR] = (TextView) findViewById(R.id.text_present_year);
        mTextViews[IDX_ROW_PRESENT + IDX_COL_HOUR] = (TextView) findViewById(R.id.text_present_hour);
        mTextViews[IDX_ROW_PRESENT + IDX_COL_MINUTE] = (TextView) findViewById(R.id.text_present_minute);

        mTextViews[IDX_ROW_DEPART + IDX_COL_MONTH] = (TextView) findViewById(R.id.text_depart_month);
        mTextViews[IDX_ROW_DEPART + IDX_COL_DAY] = (TextView) findViewById(R.id.text_depart_day);
        mTextViews[IDX_ROW_DEPART + IDX_COL_YEAR] = (TextView) findViewById(R.id.text_depart_year);
        mTextViews[IDX_ROW_DEPART + IDX_COL_HOUR] = (TextView) findViewById(R.id.text_depart_hour);
        mTextViews[IDX_ROW_DEPART + IDX_COL_MINUTE] = (TextView) findViewById(R.id.text_depart_minute);

        mRowDestination = (LinearLayout) findViewById(R.id.row_destination);
        mRowPresent = (LinearLayout) findViewById(R.id.row_present);
        mRowDeparted = (LinearLayout) findViewById(R.id.row_departed);

        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "bttf_tc.ttf");

            for (TextView textView : mTextViews) {
                textView.setTypeface(tf);
            }
        }

        mInflated = true;
    }

    @Override
    public void onAttachedToWindow() {
        Log.d("Watchface", "onAttachedToWindow");

        super.onAttachedToWindow();

        if (isInEditMode()) {
            return;
        }
        mWatch.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {

        Log.d("Watchface", "onDetachedFromWindow");

        super.onDetachedFromWindow();

        if (isInEditMode()) {
            return;
        }
        mWatch.onDetachedFromWindow();
    }


    @Override
    public void onTimeChanged(Calendar time) {

        Log.d("Watchface", "onTimeChanged");

        displayTime(time, IDX_ROW_DEST);
        displayTime(time, IDX_ROW_PRESENT);
        displayTime(time, IDX_ROW_DEPART);

        invalidate();
    }

    private void displayTime(Calendar time, int rowIndex) {
        mTextViews[rowIndex + IDX_COL_MONTH].setText(MONTH_NAMES[time.get(Calendar.MONTH)]);
        mTextViews[rowIndex + IDX_COL_DAY].setText(String.format(Locale.US, "%02d", time.get(Calendar.DAY_OF_MONTH)));
        mTextViews[rowIndex + IDX_COL_YEAR].setText(String.format(Locale.US, "%04d", time.get(Calendar.YEAR)));
        mTextViews[rowIndex + IDX_COL_HOUR].setText(String.format(Locale.US, "%02d", time.get(Calendar.HOUR_OF_DAY)));
        mTextViews[rowIndex + IDX_COL_MINUTE].setText(String.format(Locale.US, "%02d", time.get(Calendar.MINUTE)));
    }

    @Override
    public void onActiveStateChanged(boolean active) {
        Log.d("Watchface", "onActiveStateChanged");
        mActive = active;
        setImageResources();
    }

    @Override
    public boolean handleSecondsInDimMode() {
        Log.d("Watchface", "handleSecondsInDimMode");
        return false;
    }

    // update images based on dimmed or not
    private void setImageResources() {
        if (mInflated) {
            if (mActive) {
                setBackgroundResource(R.drawable.background);
                mRowDestination.setBackgroundResource(R.drawable.plate_destination_labels);
                mRowPresent.setBackgroundResource(R.drawable.plate_present);
                mRowDeparted.setBackgroundResource(R.drawable.plate_departed);
            } else {
                setBackgroundResource(R.drawable.background_dimmed);
                mRowDestination.setBackgroundResource(R.drawable.plate_dimmed);
                mRowPresent.setBackgroundResource(R.drawable.plate_dimmed);
                mRowDeparted.setBackgroundResource(R.drawable.plate_dimmed);
            }
        }
    }


}
