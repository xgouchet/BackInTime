package fr.xgouchet.android.backintime.renderer;


import android.content.Context;
import android.graphics.Bitmap;

import java.util.Calendar;

public abstract class AbstractRenderer {

    /**
     * Renders the watch face into a bitmap.
     *
     * @param context the current context
     * @param time    the time to render
     * @param dimmed  true if the watch face is in dimmed mode
     */
    public abstract Bitmap render(Context context, Calendar time, boolean dimmed);
}
