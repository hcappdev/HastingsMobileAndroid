package edu.hastings.hastingscollege.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import edu.hastings.hastingscollege.tabfragments.BreakfastFragment;
import edu.hastings.hastingscollege.tabfragments.DinnerFragment;
import edu.hastings.hastingscollege.tabfragments.LunchFragment;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    // Number of Viewpager pages
    final int PAGE_COUNT = 3;
    // Tab titles
    private static final String[] tabsTitles = { "Breakfast", "Lunch", "Dinner"  };

    private String dayOfWeek;

    public TabsPagerAdapter(FragmentManager fm, String day) {
        super(fm);
        this.dayOfWeek = day;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return BreakfastFragment.newInstance(dayOfWeek);
            case 1:
                return LunchFragment.newInstance(dayOfWeek);
            case 2:
                return DinnerFragment.newInstance(dayOfWeek);
        }
        return null;
    }

    public CharSequence getPageTitle(int position) {
        return tabsTitles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
