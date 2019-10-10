package fr.xgouchet.backintime.wear

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import java.util.Calendar

class ClockTowerRenderer(context: Context) {

    private var background: Drawable? = null
    private var backgroundDimmed: Drawable? = null
    private var minuteHand: Drawable? = null
    private var minuteHandDimmed: Drawable? = null
    private var hourHand: Drawable? = null
    private var hourHandDimmed: Drawable? = null

    init {
        //
        val res = context.resources

        loadDrawables(res)
    }

    // region Loading

    private fun loadDrawables(res: Resources) {
        background = res.getDrawable(R.drawable.clock_tower_background)
        backgroundDimmed = res.getDrawable(R.drawable.clock_tower_background_d)
        minuteHand = res.getDrawable(R.drawable.clock_tower_hand_minute_b)
        minuteHandDimmed = res.getDrawable(R.drawable.clock_tower_hand_minute_d)
        hourHand = res.getDrawable(R.drawable.clock_tower_hand_hour_b)
        hourHandDimmed = res.getDrawable(R.drawable.clock_tower_hand_hour_d)

    }

    // endregion

    // region Rendering
    fun render(canvas: Canvas, calendar: Calendar, dimmed: Boolean, lowBitAmbient: Boolean) {
        canvas.drawColor(Color.BLACK)

        if (dimmed) {
            render(canvas, calendar, backgroundDimmed, minuteHandDimmed, hourHandDimmed)
        } else {
            render(canvas, calendar, background, minuteHand, hourHand)
        }
    }

    fun render(canvas: Canvas,
               calendar: Calendar,
               background: Drawable?,
               minute: Drawable?,
               hour: Drawable?) {

        val centerX = canvas.width / 2f
        val centerY = canvas.height / 2f
        val minutesRotation = calendar.get(Calendar.MINUTE) * 6f
        val hourHandOffset = calendar.get(Calendar.MINUTE) / 2f
        val hoursRotation = calendar.get(Calendar.HOUR) * 30 + hourHandOffset

        background?.let {
            it.bounds = canvas.clipBounds
            it.draw(canvas)
        }

        canvas.save()

        canvas.rotate(hoursRotation, centerX, centerY)
        hour?.let {
            it.bounds = canvas.clipBounds
            it.draw(canvas)
        }

        canvas.rotate(minutesRotation - hoursRotation, centerX, centerY)
        minute?.let {
            it.bounds = canvas.clipBounds
            it.draw(canvas)
        }

        canvas.restore()
    }
    // endregion
}