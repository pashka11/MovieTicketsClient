package com.javaproject.pashnim.cinema;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.javaproject.pashnim.cinema.DisplayObjects.MovieDisplay;
import com.javaproject.pashnim.cinema.Objects.MovieDetails;
import com.javaproject.pashnim.cinema.WebInterfaces.MoviesServiceAPI;
import com.javaproject.pashnim.cinema.WebInterfaces.MoviesServiceFactory;
import com.javaproject.pashnim.cinema.WebInterfaces.WebApiConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MovieClickedListener
{
    // Views
    ProgressBar m_progressBar;

    // Variables
    List<MovieDisplay>  m_movieDisplays;
    MoviesListAdapter   m_moviesAdapter;
    RecyclerView        m_moviesListRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        m_progressBar = (ProgressBar) findViewById(R.id.pb_movies);

        m_moviesAdapter = new MoviesListAdapter(this);

        m_moviesListRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        m_moviesListRecyclerView.setAdapter(m_moviesAdapter);
        m_moviesListRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        m_moviesListRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        m_moviesListRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ShowMoviesList();
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

                Toast.makeText(MainActivity.this, "Failed Loading Movies", Toast.LENGTH_SHORT).show();
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
                        .with(MainActivity.this)
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
        // Start a new activity if view was pressed
        Intent intent = new Intent(this, MovieDescriptionActivity.class);

        MovieDetails det = m_movieDisplays.get(position).MovieDetails;
        Bitmap bit = m_movieDisplays.get(position).MoviePicture;

        // TODO : This method starts a new activity with the movie selected, but there is a problem,
        // When the image is too big it crushes, so we need to find another way to pass the data, maybe create an EventBus
        // Or make the details page a fragment so it can access the data

        intent.putExtra("movie", det);
        intent.putExtra("pic", bit);

        startActivity(intent);
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
            case R.id.check:
            {
                Toast.makeText(MainActivity.this, "Check Working", Toast.LENGTH_SHORT).show();
                Intent goToNextActivity = new Intent(MainActivity.this, MovieDescriptionActivity.class);
                startActivity(goToNextActivity);

                break;
            }
            case R.id.admin_action:
            {
                Toast.makeText(MainActivity.this, "Admin", Toast.LENGTH_SHORT).show();

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
