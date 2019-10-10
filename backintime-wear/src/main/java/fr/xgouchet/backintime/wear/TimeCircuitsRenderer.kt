package fr.xgouchet.backintime.wear

import android.content.Context
import android.content.res.Resources
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import java.util.Calendar
import java.util.Locale
import kotlin.math.min

class TimeCircuitsRenderer(context: Context) {

    companion object {
        const val FONT_PATH = "bttf_tc.ttf"
        private val MONTH_NAMES = arrayOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
    }

    private var width: Int = 0
    private var height: Int = 0
    private var mScreenTextSize: Int = 0
    private var mScreenTextOffsetY: Int = 0
    private var mScreenTextOffsetX: Int = 0
    private var mGlowRadius: Int = 0

    private var mMonthScreenPos: Int = 0
    private var mDayScreenPos: Int = 0
    private var mYearScreenPos: Int = 0
    private var mHourScreenPos: Int = 0
    private var mMinScreenPos: Int = 0
    private var mDestinationRowY: Int = 0
    private var mPresentRowY: Int = 0
    private var mDepartedRowY: Int = 0

    private var destinationPaint: Paint = Paint()
    private var presentPaint: Paint = Paint()
    private var departedPaint: Paint = Paint()
    private var blackPaint: Paint = Paint()
    private var whiteTextPaint: Paint = Paint()

    private var background: Drawable? = null
    private var lcdTypeface: Typeface? = null
    private var glowFilter: BlurMaskFilter = BlurMaskFilter(1f, BlurMaskFilter.Blur.NORMAL)

    init {
        //
        val res = context.resources

        loadFont(context)
        loadSizes(res)
        loadBackground(res)
        loadPaints(res)
    }

    // region loading

    private fun loadSizes(res: Resources) {

        // get sizes
        width = res.getDimensionPixelSize(R.dimen.dashboard_width)
        height = res.getDimensionPixelSize(R.dimen.dashboard_height)

        // glow radius
        mGlowRadius = res.getDimensionPixelSize(R.dimen.lcd_glow_radius)

        // load the text size
        mScreenTextSize = res.getDimensionPixelSize(R.dimen.lcd_screen_text_size)
        mScreenTextOffsetY = res.getDimensionPixelOffset(R.dimen.lcd_screen_text_offset_y)
        mScreenTextOffsetX = res.getDimensionPixelOffset(R.dimen.lcd_screen_text_offset_x)

        // load the X positions of texts
        mMonthScreenPos = res.getDimensionPixelOffset(R.dimen.lcd_month_screen_x_pos) + mScreenTextOffsetX
        mYearScreenPos = res.getDimensionPixelOffset(R.dimen.lcd_year_screen_x_pos) + mScreenTextOffsetX
        mDayScreenPos = res.getDimensionPixelOffset(R.dimen.lcd_day_screen_x_pos) + mScreenTextOffsetX
        mHourScreenPos = res.getDimensionPixelOffset(R.dimen.lcd_hour_screen_x_pos) + mScreenTextOffsetX
        mMinScreenPos = res.getDimensionPixelOffset(R.dimen.lcd_min_screen_x_pos) + mScreenTextOffsetX

        // load the Y positions of texts
        mDestinationRowY = res.getDimensionPixelOffset(R.dimen.lcd_destination_row_y) + mScreenTextOffsetY
        mPresentRowY = res.getDimensionPixelOffset(R.dimen.lcd_present_row_y) + mScreenTextOffsetY
        mDepartedRowY = res.getDimensionPixelOffset(R.dimen.lcd_last_departed_row_y) + mScreenTextOffsetY
    }

    private fun loadBackground(res: Resources) {
        background = res.getDrawable(R.drawable.timecircuits_dashboard).apply { setBounds(0, 0, width, height) }
    }

    private fun loadFont(context: Context) {
        lcdTypeface = Typeface.createFromAsset(context.assets, FONT_PATH)
    }

    private fun loadPaints(res: Resources) {

        glowFilter = BlurMaskFilter(mGlowRadius.toFloat(), BlurMaskFilter.Blur.SOLID)

        whiteTextPaint = Paint().apply {
            isAntiAlias = false
            isSubpixelText = false
            typeface = lcdTypeface
            style = Paint.Style.FILL
            color = Color.WHITE
            textSize = mScreenTextSize.toFloat()
        }

        destinationPaint = Paint().apply {
            isAntiAlias = true
            isSubpixelText = true
            typeface = lcdTypeface
            style = Paint.Style.FILL
            color = res.getColor(R.color.accent_destination)
            textSize = mScreenTextSize.toFloat()
            maskFilter = glowFilter
        }

        presentPaint = Paint().apply {
            isAntiAlias = true
            isSubpixelText = true
            typeface = lcdTypeface
            style = Paint.Style.FILL
            color = res.getColor(R.color.accent_present)
            textSize = mScreenTextSize.toFloat()
            maskFilter = glowFilter
        }

        departedPaint = Paint().apply {
            isAntiAlias = true
            isSubpixelText = true
            typeface = lcdTypeface
            style = Paint.Style.FILL
            color = res.getColor(R.color.accent_departed)
            textSize = mScreenTextSize.toFloat()
            maskFilter = glowFilter
        }

        blackPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }
    }

    // endregion

    // region Rendering

    fun render(canvas: Canvas, calendar: Calendar, dimmed: Boolean, lowBitAmbient: Boolean) {

        whiteTextPaint.isAntiAlias = !lowBitAmbient

        canvas.save()

        val scale = min(canvas.width.toFloat() / width.toFloat(), canvas.height.toFloat() / height.toFloat())
        val offsetX = ((canvas.width / scale) - width.toFloat()) / 2f
        val offsetY = ((canvas.height / scale) - height.toFloat()) / 2f

        canvas.scale(scale, scale)
        canvas.translate(offsetX, offsetY)


        drawBackground(canvas, dimmed)
        drawTime(canvas, calendar, dimmed)

        canvas.restore()
    }

    private fun drawBackground(canvas: Canvas, dimmed: Boolean) {
        if (dimmed) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), blackPaint)
        } else {
            background?.draw(canvas)
        }
    }

    private fun drawTime(canvas: Canvas, calendar: Calendar, dimmed: Boolean) {
        // compute destination and departed time
        val departed    = "APR2019841808"
        val destination = "JUL0520530829"

        // draw Dest, Present, Departed
        if (dimmed) {
            renderRow(canvas, destination, mDestinationRowY, whiteTextPaint)
            renderRow(canvas, calendar, mPresentRowY, whiteTextPaint)
            renderRow(canvas, departed, mDepartedRowY, whiteTextPaint)
        } else {
            renderRow(canvas, destination, mDestinationRowY, destinationPaint)
            renderRow(canvas, calendar, mPresentRowY, presentPaint)
            renderRow(canvas, departed, mDepartedRowY, departedPaint)
        }
    }


    private fun renderRow(canvas: Canvas, value: Any?, y: Int, paint: Paint) {
        if (value == null) {
            canvas.drawText("888", mMonthScreenPos.toFloat(), y.toFloat(), paint)
            canvas.drawText("88", mDayScreenPos.toFloat(), y.toFloat(), paint)
            canvas.drawText("8888", mYearScreenPos.toFloat(), y.toFloat(), paint)
            canvas.drawText("88", mHourScreenPos.toFloat(), y.toFloat(), paint)
            canvas.drawText("88", mMinScreenPos.toFloat(), y.toFloat(), paint)
        } else if (value is Calendar) {
            val time = value as Calendar?

            canvas.drawText(MONTH_NAMES[time!!.get(Calendar.MONTH)], mMonthScreenPos.toFloat(), y.toFloat(), paint)
            canvas.drawText(String.format(Locale.US, "%02d", time.get(Calendar.DAY_OF_MONTH)), mDayScreenPos.toFloat(), y.toFloat(), paint)
            canvas.drawText(String.format(Locale.US, "%04d", time.get(Calendar.YEAR)), mYearScreenPos.toFloat(), y.toFloat(), paint)
            canvas.drawText(String.format(Locale.US, "%02d", time.get(Calendar.HOUR_OF_DAY)), mHourScreenPos.toFloat(), y.toFloat(), paint)
            canvas.drawText(String.format(Locale.US, "%02d", time.get(Calendar.MINUTE)), mMinScreenPos.toFloat(), y.toFloat(), paint)
        } else {
            val freetext = String.format("%s             ", value.toString()).toUpperCase()
            canvas.drawText(freetext.substring(0, 3), mMonthScreenPos.toFloat(), y.toFloat(), paint)
            canvas.drawText(freetext.substring(3, 5), mDayScreenPos.toFloat(), y.toFloat(), paint)
            canvas.drawText(freetext.substring(5, 9), mYearScreenPos.toFloat(), y.toFloat(), paint)
            canvas.drawText(freetext.substring(9, 11), mHourScreenPos.toFloat(), y.toFloat(), paint)
            canvas.drawText(freetext.substring(11, 13), mMinScreenPos.toFloat(), y.toFloat(), paint)
        }
    }

    // endregion
}