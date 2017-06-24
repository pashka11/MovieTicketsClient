package com.javaproject.pashnim.cinema;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.javaproject.pashnim.cinema.Objects.MovieDetails;
import com.javaproject.pashnim.cinema.Objects.MovieDisplay;
import com.javaproject.pashnim.cinema.Objects.Screening;

public class MainActivity extends AppCompatActivity
{
    private MovieDisplay m_selectedMovie;
    private Screening m_selectedScreening;

    // TODO : Save Fragments here so we can HIDE/SHOW them on back
    // TODO : Change transaction to hide previous, add new and commit instead of current replace

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ShowMoviesListFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_page,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.back_action:
            {
                onBackPressed();

                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        //MenuItem item = menu.findItem(android.R.id.home);
        //item.setVisible(false);

        return true;
    }

    public void ShowMoviesListFragment()
    {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.container, new MoviesListFragment())
                .addToBackStack(null).commit();
    }

    public void ShowSelectScreeningFragment(MovieDisplay movieDisplay)
    {
        m_selectedMovie = movieDisplay;

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new ScreeningSelectionFragment())
                .addToBackStack(null).commit();
    }

    public void ShowSelectSeatsFragment(Screening screening)
    {
        m_selectedScreening = screening;

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SeatsSelectionFragment())
                .addToBackStack(null).commit();

        // TODO : we can use add function instead of replace with a unique TAG and then hide the fragment with the tag;
//        getFragmentManager()
//                .beginTransaction()
//                .hide(getFragmentManager().findFragmentById(R.id.container))
//                .show(new SeatsSelectionFragment())
//                .addToBackStack(null).commit();
    }

    public MovieDetails getSelectedMovie()
    {
        return m_selectedMovie.MovieDetails;
    }

    public Bitmap getSelectedMovieImage()
    {
        return m_selectedMovie.MoviePicture;
    }

    public Screening getSelectedScreening()
    {
        return m_selectedScreening;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


}
