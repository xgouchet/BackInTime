package fr.xgouchet.backintime.wear

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import java.util.Calendar
import java.util.Locale
import kotlin.math.min

class GroundhogRenderer(context: Context) {
    companion object {
        const val FONT_PATH = "groundhog.ttf"
    }


    private var width: Int = 0
    private var height: Int = 0

    private var background: BitmapDrawable? = null
    private var highlights: Bitmap? = null
    private var shadows: Bitmap? = null
    private var shadowsDimmed: Bitmap? = null

    private var digitsTextSize: Int = 0
    private var textWidth: Int = 0
    private var textHourX: Int = 0
    private var textMinX: Int = 0
    private var textY: Int = 0
    private var offsetScale: Float = 0f

    private var digitsTypeface: Typeface? = null

    private var whiteTextPaint: Paint = Paint()
    private var greyTextPaint: Paint = Paint()
    private var blackPaint: Paint = Paint()
    private var overlayPaint: Paint = Paint()
    private var multiplyPaint: Paint = Paint()

    init {
        val res = context.resources

        // load everything
        loadSizes(res)
        loadFont(context)
        loadPaints(res)


        highlights = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
        shadows = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
        shadowsDimmed = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
        loadDrawables(res)
    }


    // region Loading

    private fun loadSizes(res: Resources) {

        // get sizes
        width = res.getDimensionPixelSize(R.dimen.groundhog_width)
        height = res.getDimensionPixelSize(R.dimen.groundhog_height)

        // load the text size
        digitsTextSize = res.getDimensionPixelSize(R.dimen.groundhog_text_size)
        textWidth = res.getDimensionPixelOffset(R.dimen.groundhog_text_width)

        // load the text position
        textHourX = res.getDimensionPixelOffset(R.dimen.groundhog_text_hour_x)
        textMinX = res.getDimensionPixelOffset(R.dimen.groundhog_text_min_x)
        textY = res.getDimensionPixelOffset(R.dimen.groundhog_text_y)

        offsetScale = res.getDimensionPixelOffset(R.dimen.groundhog_offset_scale).toFloat()
    }

    private fun loadDrawables(res: Resources) {
        // load images
        background = res.getDrawable(R.drawable.groundhog_background).apply {
            setBounds(0, 0, width, height)
        } as BitmapDrawable

        // load images into bitmaps
        res.getDrawable(R.drawable.groundhog_highlights).apply {
            setBounds(0, 0, width, height)
            draw(Canvas(highlights))
        }

        res.getDrawable(R.drawable.groundhog_shadows).apply {
            setBounds(0, 0, width, height)
            draw(Canvas(shadows))
        }

        res.getDrawable(R.drawable.groundhog_shadows_dimmed).apply {
            setBounds(0, 0, width, height)
            draw(Canvas(shadowsDimmed))
        }
    }

    private fun loadFont(context: Context) {
        digitsTypeface = Typeface.createFromAsset(context.assets, FONT_PATH)
    }

    private fun loadPaints(res: Resources) {

        whiteTextPaint = Paint().apply {
            isAntiAlias = false
            isSubpixelText = false
            typeface = digitsTypeface
            style = Paint.Style.FILL
            color = Color.WHITE
            textSize = digitsTextSize.toFloat()
        }

        greyTextPaint = Paint().apply {
            isAntiAlias = true
            isSubpixelText = false
            typeface = digitsTypeface
            style = Paint.Style.FILL
            color = res.getColor(R.color.groundhog_text_color)
            textSize = digitsTextSize.toFloat()
        }

        blackPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }

        overlayPaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
        }

        multiplyPaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
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

        drawHighlightsAndShadows(dimmed, canvas)

        canvas.restore()
    }

    private fun drawHighlightsAndShadows(dimmed: Boolean, canvas: Canvas) {
        if (dimmed) {
            // only draw shadow
            canvas.drawBitmap(shadowsDimmed, 0f, 0f, multiplyPaint)
        } else {
            // draw highlight
            canvas.drawBitmap(highlights, 0f, 0f, overlayPaint)
            // draw shadow
            canvas.drawBitmap(shadows, 0f, 0f, multiplyPaint)
        }
    }

    private fun drawBackground(canvas: Canvas, dimmed: Boolean) {
        if (dimmed) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), blackPaint)
        } else {
            background?.draw(canvas)
        }
    }

    private fun drawTime(canvas: Canvas, calendar: Calendar, dimmed: Boolean) {
        // draw text
        val textPaint = if (dimmed) whiteTextPaint else greyTextPaint
        drawTextCentered(canvas, String.format(Locale.US, "%d", calendar.get(Calendar.HOUR_OF_DAY)), textHourX, textY, textPaint)
        drawTextCentered(canvas, String.format(Locale.US, "%02d", calendar.get(Calendar.MINUTE)), textMinX, textY, textPaint)
    }

    private fun drawTextCentered(canvas: Canvas, text: String, x: Int, y: Int, paint: Paint) {
        val measuredWidth = paint.measureText(text)
        val oversize = (textWidth - measuredWidth) / 2

        canvas.save()

        canvas.clipRect(0, 0, width, height / 2)
        canvas.drawText(text, x + oversize, y - offsetScale, paint)

        canvas.restore()

        canvas.save()

        canvas.clipRect(0, height / 2, width, height)
        canvas.drawText(text, x + oversize + offsetScale, y.toFloat(), paint)

        canvas.restore()

    }

    // endregion
}