package fr.xgouchet.android.bttf.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.wearable.DataMap;

import java.util.Map;

/**
 *
 */
public class PreferencesUtils {


    public static SharedPreferences getPreferences(Context context) {
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
     * Fills a wearable DataMap with everything available in the SharedPreferences.
     * Really usefull to send the prefs to the wearable all at once. Ok It may not be the most
     * efficient way but this way nothing gets forgotten
     *
     * @param context the current context
     * @param map     the data map
     */
    public static void writeDataMap(Context context, DataMap map) {
        SharedPreferences sharedPreferences = getPreferences(context);
        Map<String, ?> preferencesMap = sharedPreferences.getAll();

        for (String key : preferencesMap.keySet()) {
            map.putString(key, preferencesMap.get(key).toString());
        }
    }

    /**
     * Read the data from the map and store it locally in the shared preferences
     *
     * @param context the current context
     * @param map     the data map
     */
    public static void readDataMap(Context context, DataMap map) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (String key : map.keySet()) {
            editor.putString(key, map.getString(key));
        }

        editor.apply();
    }


}
