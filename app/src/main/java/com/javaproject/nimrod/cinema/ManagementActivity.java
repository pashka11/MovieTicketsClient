package com.javaproject.nimrod.cinema;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nimrod on 17/08/2017.
 */

public class ManagementActivity extends AppCompatActivity
{
    private static String tabTitles[] = new String[] { "Movie", "Screening", "Hall" };

    @BindView(R.id.vp_management)
    ViewPager _viewPager;
    @BindView(R.id.management_tabs)
    TabLayout _tabLayout;
    private ManagementFragmentsAdapter _viewPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        ButterKnife.bind(this);

        _viewPagerAdapter = new ManagementFragmentsAdapter(getFragmentManager());
        _viewPager.setAdapter(_viewPagerAdapter);

        _tabLayout.setupWithViewPager(_viewPager);
    }

    public static class ManagementFragmentsAdapter extends FragmentPagerAdapter
    {
        private static int NUM_ITEMS = 3;

        public ManagementFragmentsAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
//                case 0: // Fragment # 0 - This will show FirstFragment
//                    return AddHallFragment.newInstance();
//                case 1: // Fragment # 0 - This will show FirstFragment different title
//                    return AddMovieFragment.newInstance();
//                case 2: // Fragment # 1 - This will show SecondFragment
//                    return AddScreeningFragment.newInstance();
                case 0: // Fragment # 0 - This will show FirstFragment
                    return AddMovieFragment.newInstance();
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return AddScreeningFragment.newInstance();
                case 2: // Fragment # 1 - This will show SecondFragment
                    return AddHallFragment.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

    }
}
