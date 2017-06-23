package com.javaproject.pashnim.cinema;

import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.javaproject.pashnim.cinema.DisplayObjects.MovieDisplay;
import com.javaproject.pashnim.cinema.Objects.MovieDetails;
import com.javaproject.pashnim.cinema.Objects.Screening;
import com.javaproject.pashnim.cinema.WebInterfaces.MoviesServiceFactory;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MovieDescriptionActivity extends AppCompatActivity {

    MovieDetails m_displayedMovie;
    Bitmap m_savedImageBitmap;

    ImageView m_movieImage;
    TextView m_movieTitle;
    TextView m_movieYear;
    TextView m_movieDuration;
    TextView m_movieGenre;
    TextView m_movieDirector;
    TextView m_movieActors;
    TextView m_movieDescription;
    EditText m_movieScreeningDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_decription);

        m_movieScreeningDate = (EditText) findViewById(R.id.txtdate);
        m_movieTitle = (TextView) findViewById(R.id.tv_movie_title);
        m_movieYear = (TextView) findViewById(R.id.tv_movie_year);
        m_movieDuration = (TextView) findViewById(R.id.tv_movie_duration);
        m_movieGenre = (TextView) findViewById(R.id.tv_movie_genre);
        m_movieDirector = (TextView) findViewById(R.id.tv_movie_director);
        m_movieActors = (TextView) findViewById(R.id.tv_movie_actors);
        m_movieDescription = (TextView) findViewById(R.id.tv_movie_description);
        m_movieImage = (ImageView) findViewById(R.id.iv_movie_image);



        // TODO: do all the findViewByIds here for all the components of this page;

        m_displayedMovie = (MovieDetails) getIntent().getSerializableExtra("movie");
        m_savedImageBitmap = getIntent().getParcelableExtra("pic");
        // TODO : save the above bitmap in a member (m_movieImage)

        PopulateFields();
        LoadScreenings();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }

        m_movieScreeningDate.setOnClickListener(v ->
        {
                DateDialogFragment dialog = new DateDialogFragment(v);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
            });
    }

    private void LoadScreenings()
    {
        // TODO: Use RxJava here to request all the screenings for the movie and then set the dateDialog like i did in the main activity
        Single.create((SingleOnSubscribe<List<Screening>>) singleEmitter ->
        {
            List<Screening> screenings = MoviesServiceFactory.GetInstance().GetMovieScreenings(m_displayedMovie.Id).execute().body();

            if (screenings != null)
                singleEmitter.onSuccess(screenings);
            else
                singleEmitter.onError(null);
        }
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe((screenings, throwable) -> {
                    Log.d("tag", " ok ok ok put guitars");
                } //  TODO : Set the screenings here to the Date thingy
        );
    }

    private void PopulateFields()
    {
        m_movieTitle.setText(m_displayedMovie.Name);
        m_movieActors.setText(m_displayedMovie.Actors.stream().map(Object::toString).collect(Collectors.joining(", ")));
        m_movieDescription.setText(m_displayedMovie.Description);
        m_movieDuration.setText(String.valueOf(m_displayedMovie.Duration));
        m_movieGenre.setText(m_displayedMovie.Genres);
        m_movieYear.setText(m_displayedMovie.ReleaseDate.toString());
        m_movieImage.setImageBitmap(m_savedImageBitmap);
    }

    public boolean onOptionsItemSelected(MenuItem item){

        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }
}
