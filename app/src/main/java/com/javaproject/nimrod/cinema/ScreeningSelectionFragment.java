package com.javaproject.nimrod.cinema;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javaproject.nimrod.cinema.Objects.MovieDetails;
import com.javaproject.nimrod.cinema.Objects.Screening;
import com.javaproject.nimrod.cinema.WebInterfaces.MoviesServiceFactory;
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

    List<Screening> _allScreenings;
    List<Screening> _selectedDayScreenings;
    MonthAdapter.CalendarDay _selectedDay;
    Screening _selectedScreening;

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

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        if (_allScreenings != null && _allScreenings.size() != 0)
        {
            dpd.setSelectableDays(_allScreenings.stream()
                    .map(screening ->
                    {
                        Calendar cal = Calendar.getInstance();
                        cal.set(screening.Time.getYear(),
                                screening.Time.getMonthOfYear() - 1,
                                screening.Time.getDayOfMonth());
                        return cal;
                    })
                    .toArray(Calendar[]::new));

            dpd.show(getFragmentManager(), "Datepickerdialog");
        }
        else
        {
            Log.d("Screenings", "screenings were not found while trying to init date picker");
            Snackbar.make(getView(), "No screenings available", Snackbar.LENGTH_LONG).show();
        }
    }

    private void LoadScreenings()
    {
        MoviesServiceFactory.GetInstance().GetMovieScreenings(m_displayedMovie.Id, true).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe((screenings, throwable) ->
                {
                    if (screenings != null)
                        _allScreenings = screenings;
                    else
                        Toast.makeText(getContext(), "Failed Loading Screenings", Toast.LENGTH_SHORT).show();
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
        _selectedDay = view.getSelectedDay();

        _selectedDayScreenings = _allScreenings
                .parallelStream().filter(screening ->
                        (screening.Time.toLocalDate().isEqual(new LocalDate(year, monthOfYear + 1, dayOfMonth))))
                .collect(Collectors.toList());

        ShowSelectedScreeningTimeDialog();
    }

    private void ShowSelectedScreeningTimeDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.choose_screening_time);

        builder.setNegativeButton("Cancel", (dialog, id) ->
                Toast.makeText(getContext(), R.string.screening_selection_canceled, Toast.LENGTH_SHORT).show());

        String[] selectedTimes = _selectedDayScreenings.stream()
                .map(screening -> screening.Time.toString(ISODateTimeFormat.hourMinute()))
                .toArray(String[]::new);

        builder.setItems(selectedTimes, (dialog, which) ->
        {
            _selectedScreening = _selectedDayScreenings.get(which);

            _selectedScreeningView.setText(
                    String.format(getString(R.string.selected_screening),
                            _selectedScreening.Time.toString("dd/MM/yyyy - HH:mm")));

            _selectedScreeningView.setVisibility(View.VISIBLE);
        });

        builder.create().show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        MenuItem nextItem = menu.findItem(R.id.next_action);

        nextItem.setVisible(true);
        searchItem.setVisible(false);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                // Tell the activity back was pressed
                getActivity().onBackPressed();

                return true;
            }
            case R.id.next_action:
            {
                if (_selectedScreening != null)
                    try                    {
                        ((MainActivity)getActivity()).ShowSelectSeatsFragment(_selectedScreening);
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
