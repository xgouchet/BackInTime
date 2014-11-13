package fr.xgouchet.android.backintime.renderer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import java.util.Calendar;
import java.util.Locale;

import fr.xgouchet.android.backintime.R;

/**
 * The Time Circuits Renderer is able to render the Time Circuits dashboard into a bitmap.
 * <p/>
 * The basic size of the image will be 200dp * 132dp, but can be rendered at twice ths size if setting the {@code large} parameter to true in the constructor
 */
public class TimeCircuitsRenderer extends AbstractRenderer {


    private static String FONT_PATH = "bttf_tc.ttf";

    private static final String[] MONTH_NAMES = new String[]{"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    private int mWidth, mHeight, mScreenTextSize, mScreenTextOffsetY, mScreenTextOffsetX, mGlowRadius;

    private int mMonthScreenPos, mDayScreenPos, mYearScreenPos, mHourScreenPos, mMinScreenPos;
    private int mDestinationRowY, mPresentRowY, mDepartedRowY;

    private Paint mDestinationPaint, mPresentPaint, mDepartedPaint, mBlackPaint, mWhiteTextPaint;

    private Drawable mBackground;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Typeface mTypeface;
    private BlurMaskFilter mGlowFilter;

    public TimeCircuitsRenderer(Context context) {
        //
        Resources res = context.getResources();

        // load everything
        loadSizes(res);
        loadBackground(res);
        loadFont(context);
        loadPaints(res);

        // create bitmap / canvas
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    public Bitmap render(Context context, Calendar time, boolean dimmed) {

        if (mCanvas == null) {
            return mBitmap;
        }

        // compute destination and departed time
        String departed = "DEC0320010344";
        String destination = "JUL1717541100";

        // draw background
        if (dimmed) {
            mCanvas.drawRect(0, 0, mWidth, mHeight, mBlackPaint);
        } else {
            mBackground.draw(mCanvas);
        }

        // draw Dest, Present, Departed
        if (dimmed) {
            renderRow(destination, mDestinationRowY, mWhiteTextPaint);
            renderRow(time, mPresentRowY, mWhiteTextPaint);
            renderRow(departed, mDepartedRowY, mWhiteTextPaint);
        } else {
            renderRow(destination, mDestinationRowY, mDestinationPaint);
            renderRow(time, mPresentRowY, mPresentPaint);
            renderRow(departed, mDepartedRowY, mDepartedPaint);
        }

        return mBitmap;
    }


    private void renderRow(Object value, int y, Paint paint) {
        if (value == null) {
            mCanvas.drawText("888", mMonthScreenPos, y, paint);
            mCanvas.drawText("88", mDayScreenPos, y, paint);
            mCanvas.drawText("8888", mYearScreenPos, y, paint);
            mCanvas.drawText("88", mHourScreenPos, y, paint);
            mCanvas.drawText("88", mMinScreenPos, y, paint);
        } else if (value instanceof Calendar) {
            Calendar time = (Calendar) value;

            mCanvas.drawText(MONTH_NAMES[time.get(Calendar.MONTH)], mMonthScreenPos, y, paint);
            mCanvas.drawText(String.format(Locale.US, "%02d", time.get(Calendar.DAY_OF_MONTH)), mDayScreenPos, y, paint);
            mCanvas.drawText(String.format(Locale.US, "%04d", time.get(Calendar.YEAR)), mYearScreenPos, y, paint);
            mCanvas.drawText(String.format(Locale.US, "%02d", time.get(Calendar.HOUR_OF_DAY)), mHourScreenPos, y, paint);
            mCanvas.drawText(String.format(Locale.US, "%02d", time.get(Calendar.MINUTE)), mMinScreenPos, y, paint);
        } else {
            String freetext = String.format("%s             ", value.toString()).toUpperCase();
            mCanvas.drawText(freetext.substring(0, 3), mMonthScreenPos, y, paint);
            mCanvas.drawText(freetext.substring(3, 5), mDayScreenPos, y, paint);
            mCanvas.drawText(freetext.substring(5, 9), mYearScreenPos, y, paint);
            mCanvas.drawText(freetext.substring(9, 11), mHourScreenPos, y, paint);
            mCanvas.drawText(freetext.substring(11, 13), mMinScreenPos, y, paint);
        }
    }


    private void loadBackground(Resources res) {
        mBackground = res.getDrawable(R.drawable.timecircuits_dashboard);
        mBackground.setBounds(0, 0, mWidth, mHeight);
    }

    private void loadSizes(Resources res) {

        // get sizes
        mWidth = res.getDimensionPixelSize(R.dimen.dashboard_width);
        mHeight = res.getDimensionPixelSize(R.dimen.dashboard_height);

        // glow radius
        mGlowRadius = res.getDimensionPixelSize(R.dimen.lcd_glow_radius);

        // load the text size
        mScreenTextSize = res.getDimensionPixelSize(R.dimen.lcd_screen_text_size);
        mScreenTextOffsetY = res.getDimensionPixelOffset(R.dimen.lcd_screen_text_offset_y);
        mScreenTextOffsetX = res.getDimensionPixelOffset(R.dimen.lcd_screen_text_offset_x);

        // load the X positions of texts
        mMonthScreenPos = (res.getDimensionPixelOffset(R.dimen.lcd_month_screen_x_pos) + mScreenTextOffsetX);
        mYearScreenPos = (res.getDimensionPixelOffset(R.dimen.lcd_year_screen_x_pos) + mScreenTextOffsetX);
        mDayScreenPos = (res.getDimensionPixelOffset(R.dimen.lcd_day_screen_x_pos) + mScreenTextOffsetX);
        mHourScreenPos = (res.getDimensionPixelOffset(R.dimen.lcd_hour_screen_x_pos) + mScreenTextOffsetX);
        mMinScreenPos = (res.getDimensionPixelOffset(R.dimen.lcd_min_screen_x_pos) + mScreenTextOffsetX);

        // load the Y positions of texts
        mDestinationRowY = (res.getDimensionPixelOffset(R.dimen.lcd_destination_row_y) + mScreenTextOffsetY);
        mPresentRowY = (res.getDimensionPixelOffset(R.dimen.lcd_present_row_y) + mScreenTextOffsetY);
        mDepartedRowY = (res.getDimensionPixelOffset(R.dimen.lcd_last_departed_row_y) + mScreenTextOffsetY);
    }

    private void loadFont(Context context) {
        mTypeface = Typeface.createFromAsset(context.getAssets(), FONT_PATH);
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
