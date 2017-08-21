package com.javaproject.nimrod.cinema;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.javaproject.nimrod.cinema.DataInterfaces.DataReceiver;
import com.javaproject.nimrod.cinema.DataInterfaces.HallsChangedListener;
import com.javaproject.nimrod.cinema.DataInterfaces.MoviesChangedListener;
import com.javaproject.nimrod.cinema.Objects.Hall;
import com.javaproject.nimrod.cinema.Objects.MovieDetails;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nimrod on 17/08/2017.
 */

public class ManagementActivity extends AppCompatActivity implements MoviesChangedListener, HallsChangedListener
{
    private static String tabTitles[] = new String[] { "Movie", "Screening", "Hall" };

    private static final int MOVIE_FRAGMENT = 0;
    private static final int SCREENING_FRAGMENT = 1;
    private static final int HALL_FRAGMENT = 2;

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

        setTitle("Management");

        _viewPagerAdapter = new ManagementFragmentsAdapter(getFragmentManager(), this, this);
        _viewPager.setAdapter(_viewPagerAdapter);

        _tabLayout.setupWithViewPager(_viewPager);
    }

    public class ManagementFragmentsAdapter extends FragmentPagerAdapter
    {
        private final static int NUM_ITEMS = 3;
        private final MoviesChangedListener moviesListener;
        private final HallsChangedListener hallsListener;
        SparseArray<Fragment> registeredFragments = new SparseArray<>(NUM_ITEMS);

        public ManagementFragmentsAdapter(FragmentManager fragmentManager, MoviesChangedListener moviesListener, HallsChangedListener hallsListener) {
            super(fragmentManager);
            this.moviesListener = moviesListener;
            this.hallsListener = hallsListener;
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
                case MOVIE_FRAGMENT:
                    return ManageMovieFragment.newInstance(moviesListener);
                case SCREENING_FRAGMENT:
                    return ManageScreeningFragment.newInstance();
                case HALL_FRAGMENT:
                    return ManageHallFragment.newInstance(hallsListener);
                default:
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

    @Override
    public void HallsChanged(List<Hall> halls)
    {
        try
        {
            ((DataReceiver)_viewPagerAdapter.getRegisteredFragment(SCREENING_FRAGMENT)).PassData(halls);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void MoviesChanged(List<MovieDetails> movies)
    {
        try
        {
            ((DataReceiver)_viewPagerAdapter.getRegisteredFragment(SCREENING_FRAGMENT)).PassData(movies);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
