package com.javaproject.pashnim.cinema;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.javaproject.pashnim.cinema.DisplayObjects.MovieView;
import com.javaproject.pashnim.cinema.Objects.MovieDetails;
import com.javaproject.pashnim.cinema.WebInterfaces.MoviesServiceAPI;
import com.javaproject.pashnim.cinema.WebInterfaces.MoviesServiceFactory;

import java.io.IOException;
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
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    RecyclerView m_moviesList;
    MoviesListAdapter m_moviesAdapter;
    ProgressBar m_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        m_progressBar = (ProgressBar) findViewById(R.id.pb_movies);

        m_moviesAdapter = new MoviesListAdapter();

        m_moviesList = (RecyclerView) findViewById(R.id.rv_movies);
        m_moviesList.setAdapter(m_moviesAdapter);
        m_moviesList.setLayoutManager(new GridLayoutManager(this, 2));
        m_moviesList.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        m_moviesList.setItemAnimator(new DefaultItemAnimator());

        ShowMoviesList();
    }

    public void ShowMoviesList()
    {
        Single.create(new SingleOnSubscribe<List<MovieDetails>>()
        {
            @Override
            public void subscribe(@NonNull SingleEmitter<List<MovieDetails>> emitter) throws Exception
            {
                final MoviesServiceAPI moviesService = MoviesServiceFactory.GetInstance();

                emitter.onSuccess(moviesService.GetAllMovies().execute().body());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).doOnSubscribe(
                new Consumer<Disposable>()
                {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception
                    {
                        m_progressBar.setVisibility(View.VISIBLE);
                    }
                }
        ).subscribeWith(new DisposableSingleObserver<List<MovieDetails>>()
        {

            @Override
            public void onSuccess(@NonNull List<MovieDetails> moviesDetails)
            {
                List<MovieView> movieViews = new ArrayList<>(moviesDetails.size());

                for (MovieDetails movie : moviesDetails)
                    movieViews.add(new MovieView(movie, null));

                m_moviesAdapter.SetData(movieViews);

                LoadMoviesImages(moviesDetails);

                m_progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(@NonNull Throwable throwable)
            {
                Log.d("Movies", "Error retrieving the movies");
            }
        });
    }

    public void LoadMoviesImages(final List<MovieDetails> movies)
    {
        Observable.create(new ObservableOnSubscribe<MovieImageArrivedEvent>()
        {
            @Override
            public void subscribe(@NonNull ObservableEmitter<MovieImageArrivedEvent> emitter) throws Exception
            {
                MoviesServiceAPI movieService = MoviesServiceFactory.GetInstance();

                for (int i = 0; i < movies.size(); i++)
                {
                    final MovieDetails currentMovie = movies.get(i);

                    // Fetching the movie image
                    ResponseBody body = movieService.GetMoviePicture(currentMovie.ImageName).execute().body();

                    byte[] imageBytes = new byte[0];

                    try
                    {
                        imageBytes = body.bytes();
                    } catch (IOException e)
                    {
                        emitter.onError(null);

                        e.printStackTrace();
                    }

                    Bitmap movieImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                    if (movieImage != null)
                        emitter.onNext(new MovieImageArrivedEvent(movieImage, i));
                    else
                        emitter.onError(null);
                }
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

//    public void LoadMovieImages(List<MovieDetails> movies)
//    {
//        List<MovieView> movieViews = new ArrayList<>(movies.size());
//
//        MoviesServiceAPI movieService = MoviesServiceFactory.GetInstance();
//
//        for (int i = 0; i < movies.size(); i++)
//        {
//            final MovieDetails currentMovie = movies.get(i);
//            final int currentIndex = i;
//
//            // Fetching the movie image
//            movieService.GetMoviePicture(currentMovie.ImageName).enqueue(new Callback<ResponseBody>()
//            {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
//                {
//                    ResponseBody body = response.body();
//
//                    byte[] imageBytes = new byte[0];
//                    try
//                    {
//                        imageBytes = body.bytes();
//                    } catch (IOException e)
//                    {
//                        e.printStackTrace();
//                    }
//
//                    Bitmap movieImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//
//                    if (movieImage != null)
//                        m_moviesAdapter.SetImageAt(movieImage, currentIndex);
//                    else
//                        Log.d("Movies", "Failed loading image of movie: " + currentMovie.Name);
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t)
//                {
//
//                }
//            });
//        }
//    }

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
                Intent goToNextActivity = new Intent(MainActivity.this, MoviesDescriptionActivity.class);
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
