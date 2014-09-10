package fr.xgouchet.android.bttf.clocktower;

import android.content.Context;

import fr.xgouchet.android.bttf.common.PreferencesUtils;

/**
 *
 */
public class ClockTowerUtils {

    public static final String PREF_CLOCK_FRAME_THEME = "pref_clock_frame_theme";
    public static final String PREF_CLOCK_WATCHFACE_BACKGROUND = "pref_clock_watchface_background";

    public static final String THEME_FRAME_BOW = "bow";
    public static final String THEME_FRAME_BOT = "bot";
    public static final String THEME_FRAME_WOB = "wob";
    public static final String THEME_FRAME_WOT = "wot";


    public static int getFrameResource(Context context) {
        int resource;

        switch (PreferencesUtils.getStringPreference(context, PREF_CLOCK_FRAME_THEME, THEME_FRAME_BOW)) {
            case THEME_FRAME_WOT:
                resource = R.drawable.clock_tower_background_wot;
                break;
            case THEME_FRAME_WOB:
                resource = R.drawable.clock_tower_background_wob;
                break;
            case THEME_FRAME_BOT:
                resource = R.drawable.clock_tower_background_bot;
                break;
            case THEME_FRAME_BOW:
            default:
                resource = R.drawable.clock_tower_background_bow;
                break;
        }

        return resource;
    }

    public static int getHourHandResource(Context context) {
        int resource;

        switch (PreferencesUtils.getStringPreference(context, PREF_CLOCK_FRAME_THEME, THEME_FRAME_BOW)) {
            case THEME_FRAME_WOB:
            case THEME_FRAME_WOT:
                resource = R.drawable.clock_tower_hand_hour_w;
                break;
            case THEME_FRAME_BOW:
            case THEME_FRAME_BOT:
            default:
                resource = R.drawable.clock_tower_hand_hour_b;
                break;
        }

        return resource;
    }

    public static int getMinuteHandResource(Context context) {
        int resource;

        switch (PreferencesUtils.getStringPreference(context, PREF_CLOCK_FRAME_THEME, THEME_FRAME_BOW)) {
            case THEME_FRAME_WOB:
            case THEME_FRAME_WOT:
                resource = R.drawable.clock_tower_hand_minute_w;
                break;
            case THEME_FRAME_BOW:
            case THEME_FRAME_BOT:
            default:
                resource = R.drawable.clock_tower_hand_minute_b;
                break;
        }

        return resource;
    }


}
