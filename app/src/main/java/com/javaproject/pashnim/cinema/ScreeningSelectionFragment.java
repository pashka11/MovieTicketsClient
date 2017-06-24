package com.javaproject.pashnim.cinema;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

/**
 * Created by Nimrod on 24/06/2017.
 */

public class ScreeningSelectionFragment extends Fragment  implements DatePickerDialog.OnDateSetListener
{
    MovieDetails m_displayedMovie;
    Bitmap m_savedImageBitmap;

    List<Screening> m_allScreenings;
    List<Screening> m_selectedDayScreenings;
    MonthAdapter.CalendarDay m_selectedDay;
    Screening m_selectedScreening;

    ImageView m_movieImage;
    TextView m_movieTitle;
    TextView m_movieYear;
    TextView m_movieDuration;
    TextView m_movieGenre;
    TextView m_movieDirector;
    TextView m_movieActors;
    TextView m_movieDescription;
    Button m_movieScreeningDate;


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
        View v = inflater.inflate(R.layout.fragment_movie_description, container, false);

        m_movieScreeningDate = (Button) v.findViewById(R.id.btn_date);
        m_movieTitle = (TextView) v.findViewById(R.id.tv_movie_title);
        m_movieYear = (TextView) v.findViewById(R.id.tv_movie_year);
        m_movieDuration = (TextView) v.findViewById(R.id.tv_movie_duration);
        m_movieGenre = (TextView) v.findViewById(R.id.tv_movie_genre);
        m_movieDirector = (TextView) v.findViewById(R.id.tv_movie_director);
        m_movieActors = (TextView) v.findViewById(R.id.tv_movie_actors);
        m_movieDescription = (TextView) v.findViewById(R.id.tv_movie_description);
        m_movieImage = (ImageView) v.findViewById(R.id.iv_movie_image);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        m_displayedMovie = ((MainActivity)getActivity()).getSelectedMovie();
        m_savedImageBitmap = ((MainActivity)getActivity()).getSelectedMovieImage();

        PopulateFields();

        // TODO : do not load screenings if we already have screenings!!! check if screenings are null
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
                        Toast.makeText(getActivity(), "Failed Loading Screenings", Toast.LENGTH_SHORT).show();
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

    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
    {
        m_selectedDay = view.getSelectedDay();
        m_selectedDayScreenings = m_allScreenings.stream().filter(screening ->
                (screening.ScreeningTime.dayOfMonth().get() == dayOfMonth &&
                        screening.ScreeningTime.monthOfYear().get() == monthOfYear &&
                        screening.ScreeningTime.year().get() == year)).collect(Collectors.toList());

        ShowSelectedScreeningTimeDialog();
    }

    private void ShowSelectedScreeningTimeDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Choose Time");

        builder.setNegativeButton("Cancel", (dialog, id) ->
                Toast.makeText(getContext(), "Canceled screening selection", Toast.LENGTH_SHORT).show());

        String[] selectedTimes = m_selectedDayScreenings.stream()
                .map(screening -> screening.ScreeningTime.toString(ISODateTimeFormat.hourMinute()))
                .toArray(String[]::new);

        builder.setItems(selectedTimes, (dialog, which) ->
                m_selectedScreening = m_selectedDayScreenings.get(which));

        builder.create().show();
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

        nextItem.setVisible(true);
        backItem.setVisible(true);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.next_action:
            {
                if (m_selectedScreening != null)
                    ((MainActivity)getActivity()).ShowSelectSeatsFragment(m_selectedScreening);

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
