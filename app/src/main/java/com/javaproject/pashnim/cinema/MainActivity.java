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
import com.javaproject.pashnim.cinema.Objects.Seat;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private MovieDisplay _selectedMovie;
    private Screening _selectedScreening;

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
        _selectedMovie = movieDisplay;

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new ScreeningSelectionFragment())
                .addToBackStack(null).commit();
    }

    public void ShowPurchaseDetailsFragment(List<Seat> selectedSeats, String selectionId) throws Exception
    {
        PurchaseFinishFragment frag = new PurchaseFinishFragment();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, frag)
                .addToBackStack(null).commit();

        // Passing the required data for the fragment
        frag.PassData(_selectedScreening, _selectedMovie.MovieDetails, selectedSeats, selectionId);
    }

    public void ShowSelectSeatsFragment(Screening screening) throws Exception
    {
        _selectedScreening = screening;

        SeatsSelectionFragment frag = new SeatsSelectionFragment();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, frag)
                .addToBackStack(null).commit();

        frag.PassData(screening, _selectedMovie.MovieDetails);

        // TODO : we can use add function instead of replace with a unique TAG and then hide the fragment with the tag;
//        getFragmentManager()
//                .beginTransaction()
//                .hide(getFragmentManager().findFragmentById(R.id.container))
//                .show(new SeatsSelectionFragment())
//                .addToBackStack(null).commit();
    }

    public MovieDetails getSelectedMovie()
    {
        return _selectedMovie.MovieDetails;
    }

    public Bitmap getSelectedMovieImage()
    {
        return _selectedMovie.MoviePicture;
    }

    public Screening getSelectedScreening()
    {
        return _selectedScreening;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public interface DataReceiver
    {
        void PassData(Object ... objects) throws Exception;
    }
}
