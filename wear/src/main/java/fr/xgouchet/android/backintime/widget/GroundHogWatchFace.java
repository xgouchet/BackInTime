package fr.xgouchet.android.backintime.widget;


import android.content.Context;
import android.util.AttributeSet;

import fr.xgouchet.android.backintime.renderer.AbstractRenderer;
import fr.xgouchet.android.backintime.renderer.GroundHogRenderer;

public class GroundHogWatchFace extends AbstractRenderedWatchface {


    public GroundHogWatchFace(Context context) {
        super(context);
    }

    public GroundHogWatchFace(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GroundHogWatchFace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected AbstractRenderer createRenderer() {
        return new GroundHogRenderer(getContext());
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
