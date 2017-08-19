package com.javaproject.nimrod.cinema;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.javaproject.nimrod.cinema.Objects.Hall;
import com.javaproject.nimrod.cinema.Objects.MovieDetails;
import com.javaproject.nimrod.cinema.Objects.Screening;
import com.javaproject.nimrod.cinema.Validation.TextInputLayoutDataAdapter;
import com.javaproject.nimrod.cinema.WebInterfaces.MoviesServiceFactory;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Nimrod on 17/08/2017.
 */

public class AddScreeningFragment extends Fragment implements Validator.ValidationListener
{
    private Validator _validator;
    private List<MovieDetails> _moviesList;
    private List<Hall> _hallsList;
    private LocalDateTime _screeningDateTime;

    @BindView(R.id.sp_movie)
    Spinner _moviesSpinner;
    @BindView(R.id.sp_hall)
    Spinner _hallsSpinner;
    @BindView(R.id.til_screening_date) @NotEmpty(messageResId = R.string.empty_field_error)
    TextInputLayout _screeningDateView;
    @BindView(R.id.til_screening_time) @NotEmpty(messageResId = R.string.empty_field_error)
    TextInputLayout _screeningTimeView;
    @BindView(R.id.til_screening_price) @NotEmpty(messageResId = R.string.empty_field_error) @Pattern(regex = "[0-9]{1,3}", messageResId = R.string.price_field_error)
    TextInputLayout _priceView;


    public static Fragment newInstance()
    {
        return new AddScreeningFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_add_screening, container, false);
        ButterKnife.bind(this, v);

        // Setting fields validator
        _validator = new Validator(this);
        _validator.setValidationListener(this);
        _validator.registerAdapter(TextInputLayout.class, new TextInputLayoutDataAdapter());

        LoadValuesFromServer();

        return v;
    }

    private void LoadValuesFromServer()
    {
        MoviesServiceFactory.GetInstance().GetAllMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((movieDetails, throwable) -> {
                    // If everything is ok
                    if (throwable == null)
                    {
                        _moviesList = movieDetails;
                        ArrayAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, _moviesList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        _moviesSpinner.setAdapter(adapter);
                    }
                });

        MoviesServiceFactory.GetInstance().GetHalls()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((halls, throwable) -> {
                    // If everything is ok
                    if (throwable == null)
                    {
                        _hallsList = halls;
                        ArrayAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, _hallsList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        _hallsSpinner.setAdapter(adapter);
                    }
                });
    }

    @OnFocusChange(R.id.tiet_screening_date)
    public void OnSelectScreeningDateFocused(View v, boolean focusGained)
    {
        if (!focusGained)
            return;

        // Prevent keyboard from appearing since we show a date dialog
        ((EditText)v).setShowSoftInputOnFocus(false);

        Calendar now = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,(view, year, month, dayOfMonth) ->
        {
            _screeningDateView.getEditText().setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
            _screeningDateTime = new LocalDateTime(year, month, dayOfMonth, 0, 0);
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        datePicker.getDatePicker().setMinDate(now.getTimeInMillis());
        datePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        datePicker.show();
    }

    @OnFocusChange(R.id.tiet_screening_time)
    public void OnSelectScreeningTimeFocused(View v, boolean focusGained)
    {
        if (!focusGained)
            return;

        // Prevent keyboard from appearing since we show a date dialog
        ((EditText)v).setShowSoftInputOnFocus(false);

        LocalTime now = LocalTime.now();

        TimePickerDialog timePicker = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                (view, hourOfDay, minute) ->
                {
                    _screeningTimeView.getEditText().setText(String.format("%02d:%02d", hourOfDay, minute));
                    _screeningDateTime = _screeningDateTime.withTime(hourOfDay, minute, 0, 0);
                },
                now.getHourOfDay(), now.getMinuteOfHour(),true);

        timePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        timePicker.show();
    }

    @OnClick(R.id.btn_add_screening)
    public void OnAddScreeningClicked()
    {
        _validator.validate();
    }

    @Override
    public void onValidationSucceeded()
    {
        Screening screening = ConstructScreeningObject();

        MoviesServiceFactory.GetInstance().AddScreening(screening)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((s, throwable) -> {
                    if (throwable != null)
                    {
                        Snackbar addScreeningSnack = Snackbar.make(getView(), R.string.failed_adding_screening, Snackbar.LENGTH_LONG);
                        addScreeningSnack.setAction(R.string.retry, v -> OnAddScreeningClicked());
                        addScreeningSnack.show();
                    }
                    else
                    {
                        Snackbar.make(getView(), R.string.operation_success_add_screening, Snackbar.LENGTH_LONG).show();
                        ClearAll();
                    }
                });
    }

    private void ClearErrorOnAllFields()
    {
        _screeningDateView.setError(null);
        _screeningTimeView.setError(null);
        _priceView.setError(null);
        _screeningDateView.setErrorEnabled(false);
        _screeningTimeView.setErrorEnabled(false);
        _priceView.setErrorEnabled(false);
    }

    private void ClearAll()
    {
        _screeningDateView.getEditText().setText("");
        _screeningTimeView.getEditText().setText("");
        _priceView.getEditText().setText("");

        // Lose focus from everything
        getActivity().getCurrentFocus().clearFocus();
    }

    private Screening ConstructScreeningObject()
    {
        return new Screening(
                ((MovieDetails) _moviesSpinner.getSelectedItem()).Id,
                _screeningDateTime,
                ((Hall) _hallsSpinner.getSelectedItem()).HallId,
                Integer.parseInt(_priceView.getEditText().getText().toString()),
                null);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors)
    {
        ClearErrorOnAllFields();

        for (ValidationError error : errors)
        {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            // Display error messages
            if (view instanceof TextInputLayout)
                ((TextInputLayout) view).setError(message);
            else if (view instanceof TextView)
                ((TextView) view).setText(message);
        }

        Snackbar.make(getView(), R.string.illegal_fields, Snackbar.LENGTH_LONG).show();
    }
}

