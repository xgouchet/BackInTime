package fr.xgouchet.android.backintime.renderer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;

import java.util.Calendar;
import java.util.Locale;

import fr.xgouchet.android.backintime.R;

public class GroundHogRenderer extends AbstractRenderer {


    private static String FONT_PATH = "groundhog.ttf";

    private final Bitmap mBitmap;
    private final Canvas mCanvas;
    private BitmapDrawable mBackground;
    private Bitmap mHighlights, mShadows, mShadowsDimmed;

    private int mWidth, mHeight;
    private int mTextHourX, mTextMinX, mTextY;
    private float mTextSize, mTextWidth;

    private Typeface mTypeface;
    private Paint mWhiteTextPaint, mGreyTextPaint, mBlackPaint, mOverlay, mMultiply;

    public GroundHogRenderer(Context context) {

        //
        Resources res = context.getResources();

        // load everything
        loadSizes(res);
        loadFont(context);
        loadPaints(res);

        // create main bitmap
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBitmap);

        // create compositing bitmaps
        mHighlights = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
        mShadows = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
        mShadowsDimmed = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
        loadImages(res);
    }


    @Override
    public Bitmap render(Context context, Calendar time, boolean dimmed) {

        // draw background
        if (dimmed) {
            mCanvas.drawRect(0, 0, mWidth, mHeight, mBlackPaint);
        } else {
            mBackground.draw(mCanvas);
        }

        // draw text
        Paint textPaint = dimmed ? mWhiteTextPaint : mGreyTextPaint;
        drawTextCentered(String.format(Locale.US, "%02d", time.get(Calendar.HOUR_OF_DAY)), mTextHourX, mTextY, textPaint);
        drawTextCentered(String.format(Locale.US, "%02d", time.get(Calendar.MINUTE)), mTextMinX, mTextY, textPaint);

        if (dimmed) {
            // only draw shadow
            mCanvas.drawBitmap(mShadowsDimmed, 0, 0, mMultiply);
        } else {
            // draw highlight
            mCanvas.drawBitmap(mHighlights, 0, 0, mOverlay);
            // draw shadow
            mCanvas.drawBitmap(mShadows, 0, 0, mMultiply);
        }

        return mBitmap;
    }

    private void drawTextCentered(String text, int x, int y, Paint paint) {
        float measuredWidth = paint.measureText(text);
        float oversize = (mTextWidth - measuredWidth) / 2;

        mCanvas.drawText(text, x + oversize, y, paint);
    }

    private void loadPaints(Resources res) {
        mWhiteTextPaint = new Paint();
        mWhiteTextPaint.setAntiAlias(false);
        mWhiteTextPaint.setSubpixelText(false);
        mWhiteTextPaint.setTypeface(mTypeface);
        mWhiteTextPaint.setStyle(Paint.Style.FILL);
        mWhiteTextPaint.setColor(Color.WHITE);
        mWhiteTextPaint.setTextSize(mTextSize);

        mGreyTextPaint = new Paint();
        mGreyTextPaint.setAntiAlias(true);
        mGreyTextPaint.setSubpixelText(false);
        mGreyTextPaint.setTypeface(mTypeface);
        mGreyTextPaint.setStyle(Paint.Style.FILL);
        mGreyTextPaint.setColor(res.getColor(R.color.groundhog_text_color));
        mGreyTextPaint.setTextSize(mTextSize);


        mBlackPaint = new Paint();
        mBlackPaint.setColor(Color.BLACK);
        mBlackPaint.setStyle(Paint.Style.FILL);

        mOverlay = new Paint();
        mOverlay.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));

        mMultiply = new Paint();
        mMultiply.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
    }

    private void loadFont(Context context) {
        mTypeface = Typeface.createFromAsset(context.getAssets(), FONT_PATH);
    }

    private void loadImages(Resources res) {
        // load images
        mBackground = (BitmapDrawable) res.getDrawable(R.drawable.groundhog_background);
        mBackground.setBounds(0, 0, mWidth, mHeight);

        // load images into bitmaps
        BitmapDrawable temp = (BitmapDrawable) res.getDrawable(R.drawable.groundhog_highlights);
        temp.setBounds(0, 0, mWidth, mHeight);
        temp.draw(new Canvas(mHighlights));

        temp = (BitmapDrawable) res.getDrawable(R.drawable.groundhog_shadows);
        temp.setBounds(0, 0, mWidth, mHeight);
        temp.draw(new Canvas(mShadows));

        temp = (BitmapDrawable) res.getDrawable(R.drawable.groundhog_shadows_dimmed);
        temp.setBounds(0, 0, mWidth, mHeight);
        temp.draw(new Canvas(mShadowsDimmed));
    }

    private void loadSizes(Resources res) {

        // get the image size
        mWidth = res.getDimensionPixelSize(R.dimen.groundhog_width);
        mHeight = res.getDimensionPixelSize(R.dimen.groundhog_height);

        // load the text size
        mTextSize = res.getDimensionPixelSize(R.dimen.groundhog_text_size);
        mTextWidth = res.getDimensionPixelSize(R.dimen.groundhog_text_width);

        // load the text position
        mTextHourX = res.getDimensionPixelOffset(R.dimen.groundhog_text_hour_x);
        mTextMinX = res.getDimensionPixelOffset(R.dimen.groundhog_text_min_x);
        mTextY = res.getDimensionPixelOffset(R.dimen.groundhog_text_y);
    }


}
