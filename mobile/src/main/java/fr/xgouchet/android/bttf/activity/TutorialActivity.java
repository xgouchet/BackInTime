package fr.xgouchet.android.bttf.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;

import fr.xgouchet.android.bttf.R;
import fr.xgouchet.android.bttf.fragments.CreditsFragment;
import fr.xgouchet.android.bttf.fragments.HomeWidgetsTutorialFragment;
import fr.xgouchet.android.bttf.fragments.IntroductionFragment;
import fr.xgouchet.android.bttf.fragments.LockWidgetsTutorialFragment;
import fr.xgouchet.android.bttf.fragments.WatchfaceTutorialFragment;

public class TutorialActivity extends FragmentActivity {

    private ViewPager mPager;
    private TutorialPagerAdapter mPagerAdapter;

    /**
     * Starts the given Activity
     *
     * @param context
     */
    public static void startTutorialActivity(Context context) {
        Intent intent = new Intent(context, TutorialActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // Set pager adapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // set title indicator
        CirclePageIndicator pagerIndicator = (CirclePageIndicator) findViewById(R.id.titles);
        pagerIndicator.setViewPager(mPager);
    }

    private static final int PAGE_INTRODUCTION = 0;
    private static final int PAGE_HOME_WIDGETS = 1;
    private static final int PAGE_LOCK_WIDGETS = 2;
    private static final int PAGE_WATCHFACES = 3;
    private static final int PAGE_CREDITS = 4;
    private static final int PAGES_COUNT = 5;

    private class TutorialPagerAdapter extends FragmentStatePagerAdapter {

        public TutorialPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case PAGE_INTRODUCTION:
                    return new IntroductionFragment();
                case PAGE_HOME_WIDGETS:
                    return new HomeWidgetsTutorialFragment();
                case PAGE_LOCK_WIDGETS:
                    return new LockWidgetsTutorialFragment();
                case PAGE_WATCHFACES:
                    return new WatchfaceTutorialFragment();
                case PAGE_CREDITS:
                    return new CreditsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return PAGES_COUNT;
        }
    }
}

