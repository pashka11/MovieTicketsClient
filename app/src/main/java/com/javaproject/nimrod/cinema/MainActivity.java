package com.javaproject.nimrod.cinema;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.javaproject.nimrod.cinema.Objects.MovieDetails;
import com.javaproject.nimrod.cinema.Objects.MovieDisplay;
import com.javaproject.nimrod.cinema.Objects.Screening;
import com.javaproject.nimrod.cinema.Objects.Seat;

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
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_page,menu);

        // Create search bar in the action bar
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView  = (SearchView) searchItem.getActionView();
        searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.START));

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(null != searchManager )
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // TODO: move this searchView shit to the fragment
        // https://stackoverflow.com/questions/43580268/searchview-is-not-working-in-fragment - page with searchview in fragment code
        // searchView.setOnQueryTextListener();
        // On the callback method call the Adapter.FilterDataByMovieName(query)

        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                // Let the child fragment on focus to handle this!!!
                return false;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void CloseAllFragments()
    {
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void ShowMoviesListFragment()
    {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.container, new MoviesListFragment())
                /*.addToBackStack(null)*/.commit();
                // TODO: Check if not adding to stack solved the go back problems
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
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchMovies(query);
        }
    }

    private void SearchMovies(String query)
    {
        // TODO :: Search movies with the query,
        // TODO: pass the movies to the movies list fragment and then to adapter
    }

    public interface DataReceiver
    {
        void PassData(Object ... objects) throws Exception;
    }
}
