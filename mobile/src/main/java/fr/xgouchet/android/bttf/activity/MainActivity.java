package fr.xgouchet.android.bttf.activity;

import android.app.Activity;
import android.os.Bundle;

import fr.xgouchet.android.bttf.R;
import fr.xgouchet.android.bttf.utils.SettingsUtils;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (SettingsUtils.shouldShowTutorial(this)) {
            TutorialActivity.startTutorialActivity(this);
        }
    }


}
