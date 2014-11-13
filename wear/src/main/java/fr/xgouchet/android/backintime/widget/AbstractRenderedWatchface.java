package fr.xgouchet.android.backintime.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.twotoasters.watchface.gears.widget.IWatchface;
import com.twotoasters.watchface.gears.widget.Watch;

import java.util.Calendar;

import fr.xgouchet.android.backintime.R;
import fr.xgouchet.android.backintime.renderer.AbstractRenderer;

/**
 * @author Xavier
 */
public abstract class AbstractRenderedWatchface extends FrameLayout implements IWatchface {

    private static final String TAG = AbstractRenderedWatchface.class.getSimpleName();

    private Watch mWatch;
    private AbstractRenderer mRenderer;
    private boolean mInflated;
    private ImageView mRenderedImage;
    private Calendar mTime;
    private boolean mActive;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context the current context
     */
    public AbstractRenderedWatchface(Context context) {
        super(context);
        init(context, null, 0);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p/>
     * <p/>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public AbstractRenderedWatchface(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating. For example, a Button class's constructor would call
     * this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows
     * the theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource to apply to this view. If 0, no
     *                     default style will be applied.
     */
    public AbstractRenderedWatchface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Log.d(TAG, "onFinishInflate");

        mRenderedImage = (ImageView) findViewById(R.id.renderedImage);

        mInflated = true;
    }


    @Override
    public void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow");

        super.onAttachedToWindow();

        if (isInEditMode()) {
            return;
        }

        mWatch.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {

        Log.d(TAG, "onDetachedFromWindow");

        super.onDetachedFromWindow();

        if (isInEditMode()) {
            return;
        }

        mWatch.onDetachedFromWindow();
    }

    @Override
    public void onTimeChanged(Calendar time) {
        mTime = time;
        update();
    }


    @Override
    public void onActiveStateChanged(boolean active) {
        Log.d("GroundHogWatchFace", "onActiveStateChanged");
        mActive = active;

        update();
    }

    /**
     * Initialises the internal state of the watch face
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource to apply to this view. If 0, no
     *                     default style will be applied.
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        Log.d(TAG, "init");

        if (isInEditMode()) {
            return;
        }

        // Create Watch
        mWatch = new Watch(this);

        if (handleSecondsInActiveMode() || handleSecondsInDimMode()) {
            mWatch.setFormat24Hour("H:mm:ss");
            mWatch.setFormat12Hour("h:mm:ss a");
        } else {
            mWatch.setFormat24Hour("H:mm");
            mWatch.setFormat12Hour("h:mm a");
        }

        // Create Renderer
        mRenderer = createRenderer();
    }

    /**
     * Updates the watch face rendered image
     */
    private void update() {
        if (!mInflated) {
            return;
        }

        if (mRenderer == null) {
            return;
        }

        Log.d(TAG, "update");

        // Updates the rendered image
        Context context = getContext();
        mRenderedImage.setImageBitmap(mRenderer.render(context, mTime, !mActive));

        // make sure the view is drawn on screen
        invalidate();
    }

    /**
     * @return the renderer to use for this watch face
     */
    protected abstract AbstractRenderer createRenderer();

    /**
     * @return whether this watch face need to display seconds in active mode (returning true may have
     * adverse effect on battery life)
     */
    protected abstract boolean handleSecondsInActiveMode();

}
