package com.javaproject.nimrod.cinema;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.javaproject.nimrod.cinema.Objects.Hall;
import com.javaproject.nimrod.cinema.Validation.TextInputLayoutDataAdapter;
import com.javaproject.nimrod.cinema.WebInterfaces.MoviesServiceFactory;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Nimrod on 17/08/2017.
 */

public class AddHallFragment extends Fragment implements Validator.ValidationListener
{
    private Validator _validator;
    private List<Hall> _hallsList;

    @BindView(R.id.til_hall_id) @NotEmpty(messageResId = R.string.empty_field_error)
    TextInputLayout _hallIdView;
    @BindView(R.id.til_hall_rows_num) @NotEmpty(messageResId = R.string.empty_field_error) @Pattern(regex = "30|[1-2][0-9]|[5-9]", messageResId = R.string.row_col_count_field_error)
    TextInputLayout _rowsCountView;
    @BindView(R.id.til_hall_seats_in_row) @NotEmpty(messageResId = R.string.empty_field_error) @Pattern(regex = "30|[1-2][0-9]|[5-9]", messageResId = R.string.row_col_count_field_error)
    TextInputLayout _seatsCountView;

    public static Fragment newInstance()
    {
        return new AddHallFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_add_hall, container, false);
        ButterKnife.bind(this, v);

        // Setting fields validator
        _validator = new Validator(this);
        _validator.setValidationListener(this);
        _validator.registerAdapter(TextInputLayout.class, new TextInputLayoutDataAdapter());

        //LoadValuesFromServer();

        return v;
    }

    @OnClick(R.id.btn_add_hall)
    public void OnAddHallClicked()
    {
        _validator.validate();
    }

    @Override
    public void onValidationSucceeded()
    {
        Hall hall = ConstructHallObject();

        MoviesServiceFactory.GetInstance().AddHall(hall)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((s, throwable) -> {
                    if (throwable != null)
                    {
                        Snackbar addMovieSnack = Snackbar.make(getView(), R.string.failed_adding_hall, Snackbar.LENGTH_LONG);
                        addMovieSnack.setAction(R.string.retry, v -> OnAddHallClicked());
                        addMovieSnack.show();
                    }
                    else
                    {
                        Snackbar.make(getView(), R.string.operation_success_add_hall, Snackbar.LENGTH_LONG).show();
                        ClearAll();
                    }
                });
    }

    private void ClearErrorOnAllFields()
    {
        _hallIdView.setError(null);
        _rowsCountView.setError(null);
        _seatsCountView.setError(null);
        _hallIdView.setErrorEnabled(false);
        _rowsCountView.setErrorEnabled(false);
        _seatsCountView.setErrorEnabled(false);
    }

    private void ClearAll()
    {
        _hallIdView.getEditText().setText("");
        _rowsCountView.getEditText().setText("");
        _seatsCountView.getEditText().setText("");

        // Lose focus from everything
        getActivity().getCurrentFocus().clearFocus();
    }

    private Hall ConstructHallObject()
    {
        return new Hall(_hallIdView.getEditText().getText().toString(),
                Integer.parseInt(_rowsCountView.getEditText().getText().toString()),
                Integer.parseInt(_seatsCountView.getEditText().getText().toString()));
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
        }

        Snackbar.make(getView(), R.string.illegal_fields, Snackbar.LENGTH_LONG).show();
    }

}
