package fr.xgouchet.android.bttf;

import android.content.Context;

import fr.xgouchet.android.bttf.common.PreferencesUtils;

/**
 *
 */
public class WatchfaceUtils {

    public static final String THEME_BACKGROUND_BLACK = "black";
    public static final String THEME_BACKGROUND_LEATHER = "leather";
    public static final String THEME_BACKGROUND_METAL = "metal";
    public static final String THEME_BACKGROUND_AUBURN = "auburn";
    public static final String THEME_BACKGROUND_RUSTED = "rusted";
    public static final String THEME_BACKGROUND_SCRATCHED = "scratched";

    public static int getWatchfaceBackgroundResource(Context context, String key) {
        int resource;

        switch (PreferencesUtils.getStringPreference(context, key, THEME_BACKGROUND_BLACK)) {
            case THEME_BACKGROUND_RUSTED:
                resource = R.drawable.background_rusted;
                break;
            case THEME_BACKGROUND_SCRATCHED:
                resource = R.drawable.background_scratched;
                break;
            case THEME_BACKGROUND_LEATHER:
                resource = R.drawable.background_leather;
                break;
            case THEME_BACKGROUND_METAL:
                resource = R.drawable.background_metal;
                break;
            case THEME_BACKGROUND_AUBURN:
                resource = R.drawable.background_auburn;
                break;
            case THEME_BACKGROUND_BLACK:
            default:
                resource = R.drawable.background_black;
                break;
        }

        return resource;
    }
}
