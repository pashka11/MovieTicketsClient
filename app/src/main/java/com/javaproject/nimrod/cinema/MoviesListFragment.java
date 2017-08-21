package com.javaproject.nimrod.cinema;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.javaproject.nimrod.cinema.Objects.MovieDetails;
import com.javaproject.nimrod.cinema.Objects.MovieDisplay;
import com.javaproject.nimrod.cinema.WebInterfaces.MoviesServiceFactory;
import com.javaproject.nimrod.cinema.WebInterfaces.WebApiConstants;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Nimrod on 24/06/2017.
 */

public class MoviesListFragment extends Fragment implements MovieClickedListener
{
    // Views
    @BindView(R.id.pb_movies) ProgressBar _progressBar;
    @BindView(R.id.rv_movies) RecyclerView m_moviesListRecyclerView;

    // Variables
    List<MovieDisplay> _movieDisplays;
    MoviesListAdapter _moviesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        View v = inflater.inflate(R.layout.fragment_movies_list, container, false);
        ButterKnife.bind(this, v);
        // TODO : remember to unbind all on destroy!!!! check syntax on butterknife site

        _moviesAdapter = new MoviesListAdapter(this);

        m_moviesListRecyclerView.setAdapter(_moviesAdapter);
        m_moviesListRecyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), 3));
        m_moviesListRecyclerView.addItemDecoration(new MoviesListFragment.GridSpacingItemDecoration(3, dpToPx(10), true));
        m_moviesListRecyclerView.setItemAnimator(new DefaultItemAnimator());

        LoadMoviesList();

        return v;
    }

    public void LoadMoviesList()
    {
        LoadMoviesList(false);
    }

    public void LoadMoviesList(boolean forceLoad)
    {
        if (forceLoad || _movieDisplays == null)
            MoviesServiceFactory.GetInstance().GetAllMovies()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(
                    disposable ->
                    {
                        _progressBar.setVisibility(View.VISIBLE);
                        m_moviesListRecyclerView.setVisibility(View.GONE);
                    }
            ).subscribe((moviesDetails, throwable) ->
            {
                _progressBar.setVisibility(View.GONE);
                m_moviesListRecyclerView.setVisibility(View.VISIBLE);

                if (moviesDetails != null)
                {
                    _movieDisplays = moviesDetails.stream()
                            .map(movie -> new MovieDisplay(movie, null))
                            .collect(Collectors.toList());

                    _moviesAdapter.SetData(_movieDisplays);

                    LoadMoviesImages(moviesDetails);
                }
                else
                {
                    Log.d("Movies", "Error retrieving the movies");

                    Snackbar.make(getView(),
                            "Failed Loading Movies", Toast.LENGTH_SHORT)
                            .setAction(R.string.retry, v -> LoadMoviesList(true))
                            .show();
                }
            });
        else
            _moviesAdapter.SetData(_movieDisplays);
    }

    public void LoadMoviesImages(final List<MovieDetails> movies)
    {
        Observable.create((ObservableOnSubscribe<MovieImageArrivedEvent>) emitter ->
        {
            for (int i = 0; i < movies.size(); i++)
            {
                final MovieDetails currentMovie = movies.get(i);

                Bitmap movieImage = Picasso
                        .with(getActivity())
                        .load(WebApiConstants.Images.Url + "/" + currentMovie.ImageName)
                        .get();

                if (movieImage != null)
                    emitter.onNext(new MovieImageArrivedEvent(movieImage, i));
                else
                    emitter.onError(null);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(movieImageArrivedEvent ->
                                _moviesAdapter.SetImageAt(movieImageArrivedEvent.image, movieImageArrivedEvent.position),
                        exception -> Log.d("Movies", "Failed loading image: " + exception.getMessage()));

    }

    @Override
    public void OnMovieItemClicked(MovieDisplay movie)
    {
        ((MainActivity)getActivity()).ShowSelectScreeningFragment(movie);
    }

    public class MovieImageArrivedEvent
    {
        public final Bitmap image;
        public final int position;

        public MovieImageArrivedEvent(Bitmap image, int position)
        {
            this.image = image;
            this.position = position;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //inflater.inflate(R.menu.menu_ticket_order_process,menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                _moviesAdapter.FilterDataByMovieName(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (TextUtils.isEmpty(newText)){
                    _moviesAdapter.ClearFilteredData();

                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        MenuItem nextItem = menu.findItem(R.id.next_action);

        nextItem.setVisible(false);
        searchItem.setVisible(true);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.refresh_action:
            {
                LoadMoviesList(true);

                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
