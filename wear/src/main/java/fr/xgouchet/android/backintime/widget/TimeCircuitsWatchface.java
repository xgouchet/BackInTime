package fr.xgouchet.android.backintime.widget;

import android.content.Context;
import android.util.AttributeSet;

import fr.xgouchet.android.backintime.renderer.AbstractRenderer;
import fr.xgouchet.android.backintime.renderer.TimeCircuitsRenderer;

/**
 * Created by xgouchet on 11/13/14.
 */
public class TimeCircuitsWatchface extends AbstractRenderedWatchface {

    public TimeCircuitsWatchface(Context context) {
        super(context);
    }

    public TimeCircuitsWatchface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeCircuitsWatchface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected AbstractRenderer createRenderer() {
        return new TimeCircuitsRenderer(getContext());
    }

    @Override
    public boolean handleSecondsInDimMode() {
        return false;
    }

    @Override
    protected boolean handleSecondsInActiveMode() {
        return false;
    }
}
