package com.javaproject.pashnim.cinema;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javaproject.pashnim.cinema.Objects.MovieDetails;
import com.javaproject.pashnim.cinema.Objects.Screening;
import com.javaproject.pashnim.cinema.WebInterfaces.MoviesServiceFactory;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.MonthAdapter;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Nimrod on 24/06/2017.
 */

public class ScreeningSelectionFragment extends Fragment implements DatePickerDialog.OnDateSetListener
{
    MovieDetails m_displayedMovie;
    Bitmap m_savedImageBitmap;

    List<Screening> m_allScreenings;
    List<Screening> m_selectedDayScreenings;
    MonthAdapter.CalendarDay m_selectedDay;
    Screening m_selectedScreening;

    @BindView(R.id.iv_movie_image) ImageView _movieImage;
    @BindView(R.id.tv_movie_title) TextView _movieTitle;
    @BindView(R.id.tv_movie_year) TextView _movieYear;
    @BindView(R.id.tv_movie_duration) TextView _movieDuration;
    @BindView(R.id.tv_movie_genre) TextView _movieGenre;
    @BindView(R.id.tv_movie_director) TextView _movieDirector;
    @BindView(R.id.tv_movie_actors) TextView _movieActors;
    @BindView(R.id.tv_movie_description) TextView _movieDescription;
    @BindView(R.id.tv_selected_screening) TextView _selectedScreeningView;

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
        ButterKnife.bind(this, v);

        _movieDescription.setMovementMethod(ScrollingMovementMethod.getInstance());

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
    }

    @OnClick(R.id.btn_date)
    void OnDatePickerButtonClicked()
    {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        if (m_allScreenings != null)
        {
            dpd.setSelectableDays(m_allScreenings.stream()
                    .map(screening ->
                    {
                        Calendar cal = Calendar.getInstance();
                        cal.set(screening.Time.getYear(),
                                screening.Time.getMonthOfYear(),
                                screening.Time.getDayOfMonth());
                        return cal;
                    })
                    .toArray(Calendar[]::new));

            dpd.show(getFragmentManager(), "Datepickerdialog");
        }
        else
            Log.d("Screenings", "screenings were not found while trying to init date picker");
    }

    private void LoadScreenings()
    {
        MoviesServiceFactory.GetInstance().GetMovieScreenings(m_displayedMovie.Id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
        _movieTitle.setText(m_displayedMovie.Name);
        _movieActors.setText(String.format(getString(R.string.actors), m_displayedMovie.Actors));
        _movieDuration.setText(String.format(getString(R.string.duration), String.valueOf(m_displayedMovie.Duration)));
        _movieGenre.setText(String.format(getString(R.string.genres), m_displayedMovie.Genres));
        _movieYear.setText(String.format("%s %s", getString(R.string.release_date), m_displayedMovie.ReleaseDate.toString(getString(R.string.release_date_time_pattern))));
        _movieImage.setImageBitmap(m_savedImageBitmap);
        _movieDirector.setText(String.format(getString(R.string.director), m_displayedMovie.Director));
        _movieDescription.setText(String.format(getString(R.string.description_format), m_displayedMovie.Description));
    }

    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
    {
        m_selectedDay = view.getSelectedDay();

        m_selectedDayScreenings = m_allScreenings
                .parallelStream().filter(screening ->
                        (screening.Time.toLocalDate().isEqual(new LocalDate(year, monthOfYear, dayOfMonth))))
                .collect(Collectors.toList());

        ShowSelectedScreeningTimeDialog();
    }

    private void ShowSelectedScreeningTimeDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.choose_screening_time);

        builder.setNegativeButton("Cancel", (dialog, id) ->
                Toast.makeText(getContext(), R.string.screening_selection_canceled, Toast.LENGTH_SHORT).show());

        String[] selectedTimes = m_selectedDayScreenings.stream()
                .map(screening -> screening.Time.toString(ISODateTimeFormat.hourMinute()))
                .toArray(String[]::new);

        builder.setItems(selectedTimes, (dialog, which) ->
        {
            m_selectedScreening = m_selectedDayScreenings.get(which);
            _selectedScreeningView.setText(
                    String.format(getString(R.string.selected_screening),
                            m_selectedScreening.Time.toString("dd/MM/yyyy - HH:mm")));
        });

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
                    try                    {
                        ((MainActivity)getActivity()).ShowSelectSeatsFragment(m_selectedScreening);
                    } catch (Exception e)                    {
                        e.printStackTrace();
                    }
                else
                    Toast.makeText(getContext(), R.string.screening_wasnt_selected, Toast.LENGTH_LONG).show();

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
