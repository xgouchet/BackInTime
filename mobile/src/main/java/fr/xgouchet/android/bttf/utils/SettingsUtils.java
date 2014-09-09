package fr.xgouchet.android.bttf.utils;

import android.content.Context;

import fr.xgouchet.android.bttf.common.PreferencesUtils;

/**
 */
public class SettingsUtils {

    public static final String PREF_TUTORIAL_SHOWN = "tutorial_was_shown";


    public static boolean shouldShowTutorial(Context context) {
        return PreferencesUtils.getPreferences(context).getBoolean(PREF_TUTORIAL_SHOWN, false);
    }


}

