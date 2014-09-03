package fr.xgouchet.android.bttf.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by xgouchet on 8/20/14.
 */
public class SettingsUtils {

    public static final String PREF_TUTORIAL_SHOWN = "tutorial_was_shown";

    public static final String PREF_DESTINATION_SOURCE = "pref_destination_source";
    public static final String PREF_DESTINATION_TIMEZONE = "pref_destination_timezone";
    public static final String PREF_DESTINATION_FREETEXT = "pref_destination_freetext";
    public static final String PREF_DEPARTED_SOURCE = "pref_departed_source";
    public static final String PREF_DEPARTED_TIMEZONE = "pref_departed_timezone";
    public static final String PREF_DEPARTED_FREETEXT = "pref_departed_freetext";


    public static final String SOURCE_CALENDAR = "calendar";
    public static final String SOURCE_BATTERY = "battery";
    public static final String SOURCE_TIMEZONE = "timezone";
    public static final String SOURCE_FREETEXT = "freetext";

    public static boolean shouldShowTutorial(Context context) {
        return !getPreferences(context).getBoolean(PREF_TUTORIAL_SHOWN, false);
    }


    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getStringPreference(Context context, String key) {
        return getStringPreference(context, key, null);
    }

    public static String getStringPreference(Context context, String key, String def) {
        return getPreferences(context).getString(key, def);
    }
}

