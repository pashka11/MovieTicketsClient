package com.javaproject.nimrod.cinema;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.javaproject.nimrod.cinema.DataInterfaces.HallsChangedListener;
import com.javaproject.nimrod.cinema.Objects.Hall;
import com.javaproject.nimrod.cinema.Validation.TextInputLayoutDataAdapter;
import com.javaproject.nimrod.cinema.WebInterfaces.MoviesServiceFactory;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Nimrod on 17/08/2017.
 */

public class ManageHallFragment extends Fragment implements Validator.ValidationListener
{
    private Validator _validator;
    private List<Hall> _hallsList;
    // Changes listener
    private static HallsChangedListener _hallsChangedListener;

    // Expandable Views
    @BindView(R.id.expl_add_hall)
    ExpandableLayout _addHallLayout;
    @BindView(R.id.expl_delete_hall)
    ExpandableLayout _deletHallLayout;

    @BindView(R.id.til_hall_id) @NotEmpty(messageResId = R.string.empty_field_error)
    TextInputLayout _hallIdView;
    @BindView(R.id.til_hall_rows_num) @NotEmpty(messageResId = R.string.empty_field_error) @Pattern(regex = "30|[1-2][0-9]|[5-9]", messageResId = R.string.row_col_count_field_error)
    TextInputLayout _rowsCountView;
    @BindView(R.id.til_hall_seats_in_row) @NotEmpty(messageResId = R.string.empty_field_error) @Pattern(regex = "30|[1-2][0-9]|[5-9]", messageResId = R.string.row_col_count_field_error)
    TextInputLayout _seatsCountView;
    @BindView(R.id.sp_halls)
    Spinner _hallsSpinner;
    private ArrayAdapter<Hall> _hallsSpinnerAdapter;

    public static Fragment newInstance(HallsChangedListener hallsChangedListener)
    {
        _hallsChangedListener = hallsChangedListener;

        return new ManageHallFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_management_hall, container, false);
        ButterKnife.bind(this, v);

        // Setting fields validator
        _validator = new Validator(this);
        _validator.setValidationListener(this);
        _validator.registerAdapter(TextInputLayout.class, new TextInputLayoutDataAdapter());

        LoadHalls();

        return v;
    }

    private void LoadHalls()
    {
        MoviesServiceFactory.GetInstance().GetHalls()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((halls, throwable) -> {
                    // If everything is ok
                    if (throwable == null)
                    {
                        _hallsList = halls;
                        _hallsSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, _hallsList);
                        _hallsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        _hallsSpinner.setAdapter(_hallsSpinnerAdapter);
                    }
                });
    }

    @OnClick(R.id.btn_add_hall)
    public void OnAddHallClicked()
    {
        _validator.validate();
    }

    @OnClick(R.id.btn_delete_hall)
    public void OnDeleteHallClicked()
    {
        if (_hallsSpinnerAdapter.getCount() == 0)
        {
            Snackbar.make(getView(), "No halls to delete", Snackbar.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Delete");
        alert.setMessage(R.string.hall_delete_confirmation);
        alert.setPositiveButton("Yes", (dialog, which) ->
        {
            DeleteSelectedHall();
            dialog.dismiss();
        });

        alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        alert.show();
    }

    /**
     * Delete the selected hall
     */
    private void DeleteSelectedHall()
    {
        MoviesServiceFactory.GetInstance().DeleteHall(((Hall)_hallsSpinner.getSelectedItem()).HallId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((responseBody, throwable) ->
                {
                    if (throwable != null)
                    {
                        Snackbar snack = Snackbar.make(getView(), R.string.failed_deleting_hall, Snackbar.LENGTH_LONG);
                        snack.setAction(R.string.retry, v -> OnDeleteHallClicked());
                        snack.show();
                    }
                    else
                    {
                        Snackbar.make(getView(), R.string.operation_success_delete_hall, Snackbar.LENGTH_LONG).show();
                        ClearAll();

                        Hall selectedItem = ((Hall)_hallsSpinner.getSelectedItem());

                        _hallsSpinnerAdapter.remove(selectedItem);
                        _hallsList.remove(selectedItem);

                        _hallsSpinnerAdapter.notifyDataSetChanged();
                        _hallsChangedListener.HallsChanged(_hallsList);
                    }
                });
    }

    @Override
    public void onValidationSucceeded()
    {
        ClearErrorOnAllFields();

        AddHall();
    }

    /**
     * Add hall, ask the server to create a new hall
     */
    private void AddHall()
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

                        // Add the new hall to all the lists and notify everyone for changes
                        hall.HallId = s;

                        // Adding the hall only here! THIS IS THE SPINNERS DATA!!!
                        _hallsList.add(hall);

                        // Notify other fragments and ourselves
                        _hallsSpinnerAdapter.notifyDataSetChanged();
                        _hallsChangedListener.HallsChanged(_hallsList);
                    }
                });
    }

    /**
     * Clear error on all view fields
     */
    private void ClearErrorOnAllFields()
    {
        _hallIdView.setError(null);
        _rowsCountView.setError(null);
        _seatsCountView.setError(null);
        _hallIdView.setErrorEnabled(false);
        _rowsCountView.setErrorEnabled(false);
        _seatsCountView.setErrorEnabled(false);
    }

    /**
     * Clear data from all fields
     */
    private void ClearAll()
    {
        _hallIdView.getEditText().setText("");
        _rowsCountView.getEditText().setText("");
        _seatsCountView.getEditText().setText("");

        // Lose focus from everything
        getActivity().getCurrentFocus().clearFocus();
    }

    /**
     * Create hall object from view objects
     * @return
     */
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

    @OnClick(R.id.btn_toggle)
    public void OnToggleButtonClicked()
    {
        _addHallLayout.toggle();
        _deletHallLayout.toggle();
    }

}
