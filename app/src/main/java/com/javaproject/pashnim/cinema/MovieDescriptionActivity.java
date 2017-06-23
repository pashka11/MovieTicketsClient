package com.javaproject.pashnim.cinema;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javaproject.pashnim.cinema.Objects.MovieDetails;
import com.javaproject.pashnim.cinema.Objects.Screening;
import com.javaproject.pashnim.cinema.WebInterfaces.MoviesServiceFactory;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.MonthAdapter;

import org.joda.time.format.ISODateTimeFormat;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MovieDescriptionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    MovieDetails m_displayedMovie;
    Bitmap m_savedImageBitmap;

    List<Screening> m_allScreenings;

    List<Screening> m_selectedScreenings;
    MonthAdapter.CalendarDay m_selectedDay;

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

        m_displayedMovie = (MovieDetails) getIntent().getSerializableExtra("movie");
        m_savedImageBitmap = getIntent().getParcelableExtra("pic");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        PopulateFields();

        LoadScreenings();

        InitializeDatePicker();
    }

    private void InitializeDatePicker()
    {
        m_movieScreeningDate.setOnClickListener(v ->
        {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );

            dpd.setSelectableDays(m_allScreenings.stream()
                    .map(screening ->
                    {
                        Calendar cal = Calendar.getInstance();
                        cal.set(screening.ScreeningTime.getYear(),
                                screening.ScreeningTime.getMonthOfYear(),
                                screening.ScreeningTime.getDayOfMonth());
                        return cal;
                    })
                    .toArray(Calendar[]::new));

            dpd.show(getFragmentManager(), "Datepickerdialog");
        });
    }

    private void LoadScreenings()
    {
        Single.create((SingleOnSubscribe<List<Screening>>) singleEmitter ->
                {
                    List<Screening> screenings = MoviesServiceFactory.GetInstance().GetMovieScreenings(m_displayedMovie.Id).execute().body();

                    if (screenings != null)
                        singleEmitter.onSuccess(screenings);
                    else
                        singleEmitter.onError(null);
                }
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe((screenings, throwable) ->
                {
                    if (screenings != null)
                        m_allScreenings = screenings;
                    else
                        Toast.makeText(this, "Failed Loading Screenings", Toast.LENGTH_SHORT).show();
                });
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
    {
        m_selectedDay = view.getSelectedDay();
        m_selectedScreenings = m_allScreenings.stream().filter(screening ->
                (screening.ScreeningTime.dayOfMonth().get() == dayOfMonth &&
                        screening.ScreeningTime.monthOfYear().get() == monthOfYear &&
                        screening.ScreeningTime.year().get() == year)).collect(Collectors.toList());

        ShowSelectedScreeningTimeDialog();
    }

    private void ShowSelectedScreeningTimeDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Choose Time");

        builder.setNegativeButton("Cancel", (dialog, id) ->
                Toast.makeText(this, "Canceled screening selection", Toast.LENGTH_SHORT).show());

        String[] selectedTimes = m_selectedScreenings.stream()
                .map(screening -> screening.ScreeningTime.toString(ISODateTimeFormat.hourMinute()))
                .toArray(String[]::new);

        builder.setItems(selectedTimes, (dialog, which) ->
                ShowSelectSeatsFragment(m_selectedScreenings.get(which)));

        builder.create().show();
    }

    private void ShowSelectSeatsFragment(Screening screening)
    {
        // TODO: create seats fragment or intent
    }
}
