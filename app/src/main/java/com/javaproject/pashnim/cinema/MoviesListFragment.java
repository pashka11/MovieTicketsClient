package com.javaproject.pashnim.cinema;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.javaproject.pashnim.cinema.Objects.MovieDetails;
import com.javaproject.pashnim.cinema.Objects.MovieDisplay;
import com.javaproject.pashnim.cinema.WebInterfaces.MoviesServiceAPI;
import com.javaproject.pashnim.cinema.WebInterfaces.MoviesServiceFactory;
import com.javaproject.pashnim.cinema.WebInterfaces.WebApiConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Nimrod on 24/06/2017.
 */

public class MoviesListFragment extends Fragment  implements MovieClickedListener
{
    // Views
    ProgressBar m_progressBar;

    // Variables
    List<MovieDisplay>  m_movieDisplays;
    MoviesListAdapter   m_moviesAdapter;
    RecyclerView        m_moviesListRecyclerView;

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
        View v = inflater.inflate(R.layout.fragment_movies_list, container, false);

        m_progressBar = (ProgressBar) v.findViewById(R.id.pb_movies);

        m_moviesAdapter = new MoviesListAdapter(this);

        m_moviesListRecyclerView = (RecyclerView) v.findViewById(R.id.rv_movies);
        m_moviesListRecyclerView.setAdapter(m_moviesAdapter);
        m_moviesListRecyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), 2));
        m_moviesListRecyclerView.addItemDecoration(new MoviesListFragment.GridSpacingItemDecoration(2, dpToPx(10), true));
        m_moviesListRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // TODO : Check if the movies list is null before we reload
        ShowMoviesList();

        return v;
    }

    public void ShowMoviesList()
    {
        Single.create((SingleOnSubscribe<List<MovieDetails>>) emitter ->
        {
            final MoviesServiceAPI moviesService = MoviesServiceFactory.GetInstance();

            List<MovieDetails> result = moviesService.GetAllMovies().execute().body();

            if (result != null)
                emitter.onSuccess(result);
            else
                emitter.onError(null);

        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).doOnSubscribe(
                disposable -> m_progressBar.setVisibility(View.VISIBLE)
        ).subscribeWith(new DisposableSingleObserver<List<MovieDetails>>()
        {

            @Override
            public void onSuccess(@NonNull List<MovieDetails> moviesDetails)
            {
                m_movieDisplays = new ArrayList<>(moviesDetails.size());

                for (MovieDetails movie : moviesDetails)
                    m_movieDisplays.add(new MovieDisplay(movie, null));

                m_moviesAdapter.SetData(m_movieDisplays);

                LoadMoviesImages(moviesDetails);

                m_progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Throwable throwable)
            {
                Log.d("Movies", "Error retrieving the movies");

                m_progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(getActivity(), "Failed Loading Movies", Toast.LENGTH_SHORT).show();
            }
        });
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
                .subscribeWith(new DisposableObserver<MovieImageArrivedEvent>()
                {
                    @Override
                    public void onNext(@NonNull MovieImageArrivedEvent movieImageArrivedEvent)
                    {
                        m_moviesAdapter.SetImageAt(movieImageArrivedEvent.image, movieImageArrivedEvent.position);
                    }

                    @Override
                    public void onError(Throwable throwable)
                    {
                        Log.d("Movies", "Failed loading image");// of movie: " + currentMovie.Name);
                    }

                    @Override
                    public void onComplete()
                    {
                    }
                });
    }

    @Override
    public void OnMovieItemClicked(int position)
    {
        ((MainActivity)getActivity()).ShowSelectScreeningFragment(m_movieDisplays.get(position));
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
        inflater.inflate(R.menu.menu_ticket_order_process,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem backItem = menu.findItem(R.id.back_action);
        MenuItem nextItem = menu.findItem(R.id.next_action);

        nextItem.setVisible(false);
        backItem.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.admin_action:
            {
                Toast.makeText(getActivity(), "Admin", Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.refresh_action:
            {
                ShowMoviesList();

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
