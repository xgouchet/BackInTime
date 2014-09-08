package fr.xgouchet.android.bttf.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.wearable.DataMap;

import java.util.Map;

/**
 */
public class SettingsUtils {

    public static final String PREF_TUTORIAL_SHOWN = "tutorial_was_shown";

    public static final String PREF_DESTINATION_SOURCE = "pref_destination_source";
    public static final String PREF_DESTINATION_TIMEZONE = "pref_destination_timezone";
    public static final String PREF_DESTINATION_FREETEXT = "pref_destination_freetext";
    public static final String PREF_DESTINATION_FREETIME = "pref_destination_freetime";
    public static final String PREF_DESTINATION_OPTION = "pref_destination_option";

    public static final String PREF_DEPARTED_SOURCE = "pref_departed_source";
    public static final String PREF_DEPARTED_TIMEZONE = "pref_departed_timezone";
    public static final String PREF_DEPARTED_FREETEXT = "pref_departed_freetext";
    public static final String PREF_DEPARTED_FREETIME = "pref_departed_freetime";
    public static final String PREF_DEPARTED_OPTION = "pref_departed_option";


    public static final String SOURCE_CALENDAR = "calendar";
    public static final String SOURCE_BATTERY = "battery";
    public static final String SOURCE_TIMEZONE = "timezone";
    public static final String SOURCE_FREETIME = "freetime";
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

    public static void setStringPreference(Context context, String key, String value) {
        getPreferences(context).edit().putString(key, value).apply();
    }

    /**
     * Filles a wearable DataMap with everything available in the SharedPreferences.
     * Really usefull to send the prefs to the wearable all at once. Ok It may not be the most
     * efficient way but this way nothing gets forgotten
     *
     * @param context the current context
     * @param map     the map to fill
     */
    public static void fillDataMap(Context context, DataMap map) {

        SharedPreferences sharedPreferences = getPreferences(context);
        Map<String, ?> preferencesMap = sharedPreferences.getAll();

        for (String key : preferencesMap.keySet()) {
            map.putString(key, preferencesMap.get(key).toString());
        }

    }
}

