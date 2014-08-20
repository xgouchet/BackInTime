package fr.xgouchet.android.bttf.timecircuits;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

/**
 * The Time Circuits Renderer is able to render the Time Circuits dashboard into a bitmap.
 * <p/>
 * The basic size of the image will be 200dp * 132dp, but can be rendered at twice ths size if setting the {@code large} parameter to true in the constructor
 */
public class TimeCircuitsRenderer {

    private static final String[] MONTH_NAMES = new String[]{"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    private int mScale, mWidth, mHeight, mScreenTextSize, mScreenTextOffsetY, mScreenTextOffsetX, mGlowRadius;

    private int mMonthScreenPos, mDayScreenPos, mYearScreenPos, mHourScreenPos, mMinScreenPos;
    private int mDestinationRowY, mPresentRowY, mDepartedRowY;

    private Paint mDestinationPaint, mPresentPaint, mDepartedPaint, mBlackPaint, mWhiteTextPaint;

    private Drawable mBackground;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Typeface mTypeface;
    private BlurMaskFilter mGlowFilter;

    /**
     * @param context the current application context
     */
    public TimeCircuitsRenderer(Context context, String fontPathInAssets, boolean large) {
        mScale = large ? 2 : 1;

        //
        Resources res = context.getResources();

        // load everything
        loadSizes(res);
        loadBackground(res);
        loadFont(context, fontPathInAssets);
        loadPaints(res);

        // create bitmap / canvas
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBitmap);
    }

    /**
     * @return the last bitmap generated
     */
    public Bitmap getBitmap() {
        return mBitmap;
    }


    /**
     * @param context     the current application context
     * @param destination the destination time
     * @param present     the present time
     * @param departed    the last departed time
     * @param dimmed      if the dashboard should be rendered dimmed
     */
    public void renderDashboard(Context context, Calendar destination, Calendar present, Calendar departed, boolean dimmed) {

        if (mCanvas == null) {
            Log.w("TimeCircuitsRenderer", "renderDashboard : null canvas");
            return;
        }

        // draw background
        if (dimmed) {
            mCanvas.drawRect(0, 0, mWidth, mHeight, mBlackPaint);
        } else {
            mBackground.draw(mCanvas);
        }

        // draw Dest, Present, Departed
        if (destination != null) {
            if (dimmed) {
                renderRow(destination, mDestinationRowY, mWhiteTextPaint);
            } else {
                renderRow(destination, mDestinationRowY, mDestinationPaint);
            }
        }
        if (present != null) {
            if (dimmed) {
                renderRow(present, mPresentRowY, mWhiteTextPaint);
            } else {
                renderRow(present, mPresentRowY, mPresentPaint);
            }
        }
        if (departed != null) {
            if (dimmed) {
                renderRow(departed, mDepartedRowY, mWhiteTextPaint);
            } else {
                renderRow(departed, mDepartedRowY, mDepartedPaint);
            }
        }

    }

    private void renderRow(Calendar time, int y, Paint paint) {
        mCanvas.drawText(MONTH_NAMES[time.get(Calendar.MONTH)], mMonthScreenPos, y, paint);
        mCanvas.drawText(String.format(Locale.US, "%02d", time.get(Calendar.DAY_OF_MONTH)), mDayScreenPos, y, paint);
        mCanvas.drawText(String.format(Locale.US, "%04d", time.get(Calendar.YEAR)), mYearScreenPos, y, paint);
        mCanvas.drawText(String.format(Locale.US, "%02d", time.get(Calendar.HOUR)), mHourScreenPos, y, paint);
        mCanvas.drawText(String.format(Locale.US, "%02d", time.get(Calendar.MINUTE)), mMinScreenPos, y, paint);
    }

    private void loadBackground(Resources res) {
        mBackground = res.getDrawable(R.drawable.dashboard);
        mBackground.setBounds(0, 0, mWidth, mHeight);
    }

    private void loadSizes(Resources res) {

        // get sizes
        mWidth = res.getDimensionPixelSize(R.dimen.dashboard_width) * mScale;
        mHeight = res.getDimensionPixelSize(R.dimen.dashboard_height) * mScale;

        // glow radius
        mGlowRadius = res.getDimensionPixelSize(R.dimen.lcd_glow_radius) * mScale;

        // load the text size
        mScreenTextSize = res.getDimensionPixelSize(R.dimen.lcd_screen_text_size) * mScale;
        mScreenTextOffsetY = res.getDimensionPixelOffset(R.dimen.lcd_screen_text_offset_y);
        mScreenTextOffsetX = res.getDimensionPixelOffset(R.dimen.lcd_screen_text_offset_x);

        // load the X positions of texts
        mMonthScreenPos = (res.getDimensionPixelOffset(R.dimen.lcd_month_screen_x_pos) + mScreenTextOffsetX) * mScale;
        mYearScreenPos = (res.getDimensionPixelOffset(R.dimen.lcd_year_screen_x_pos) + mScreenTextOffsetX) * mScale;
        mDayScreenPos = (res.getDimensionPixelOffset(R.dimen.lcd_day_screen_x_pos) + mScreenTextOffsetX) * mScale;
        mHourScreenPos = (res.getDimensionPixelOffset(R.dimen.lcd_hour_screen_x_pos) + mScreenTextOffsetX) * mScale;
        mMinScreenPos = (res.getDimensionPixelOffset(R.dimen.lcd_min_screen_x_pos) + mScreenTextOffsetX) * mScale;

        // load the Y positions of texts
        mDestinationRowY = (res.getDimensionPixelOffset(R.dimen.lcd_destination_row_y) + mScreenTextOffsetY) * mScale;
        mPresentRowY = (res.getDimensionPixelOffset(R.dimen.lcd_present_row_y) + mScreenTextOffsetY) * mScale;
        mDepartedRowY = (res.getDimensionPixelOffset(R.dimen.lcd_last_departed_row_y) + mScreenTextOffsetY) * mScale;
    }

    private void loadFont(Context context, String fontPathInAssets) {
        mTypeface = Typeface.createFromAsset(context.getAssets(), fontPathInAssets);
    }

    private void loadPaints(Resources res) {

        mGlowFilter = new BlurMaskFilter(mGlowRadius, BlurMaskFilter.Blur.SOLID);

        mWhiteTextPaint = new Paint();
        mWhiteTextPaint.setAntiAlias(false);
        mWhiteTextPaint.setSubpixelText(false);
        mWhiteTextPaint.setTypeface(mTypeface);
        mWhiteTextPaint.setStyle(Paint.Style.FILL);
        mWhiteTextPaint.setColor(Color.WHITE);
        mWhiteTextPaint.setTextSize(mScreenTextSize);

        mDestinationPaint = new Paint();
        mDestinationPaint.setAntiAlias(true);
        mDestinationPaint.setSubpixelText(true);
        mDestinationPaint.setTypeface(mTypeface);
        mDestinationPaint.setStyle(Paint.Style.FILL);
        mDestinationPaint.setColor(res.getColor(R.color.accent_destination));
        mDestinationPaint.setTextSize(mScreenTextSize);
        mDestinationPaint.setMaskFilter(mGlowFilter);

        mPresentPaint = new Paint();
        mPresentPaint.setAntiAlias(true);
        mPresentPaint.setSubpixelText(true);
        mPresentPaint.setTypeface(mTypeface);
        mPresentPaint.setStyle(Paint.Style.FILL);
        mPresentPaint.setColor(res.getColor(R.color.accent_present));
        mPresentPaint.setTextSize(mScreenTextSize);
        mPresentPaint.setMaskFilter(mGlowFilter);

        mDepartedPaint = new Paint();
        mDepartedPaint.setAntiAlias(true);
        mDepartedPaint.setSubpixelText(true);
        mDepartedPaint.setTypeface(mTypeface);
        mDepartedPaint.setStyle(Paint.Style.FILL);
        mDepartedPaint.setColor(res.getColor(R.color.accent_departed));
        mDepartedPaint.setTextSize(mScreenTextSize);
        mDepartedPaint.setMaskFilter(mGlowFilter);

        mBlackPaint = new Paint();
        mBlackPaint.setColor(Color.BLACK);
        mBlackPaint.setStyle(Paint.Style.FILL);
    }
}
