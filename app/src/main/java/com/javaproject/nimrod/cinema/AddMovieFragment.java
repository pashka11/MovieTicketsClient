package com.javaproject.nimrod.cinema;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.javaproject.nimrod.cinema.Objects.MovieDetails;
import com.javaproject.nimrod.cinema.Validation.TextInputLayoutDataAdapter;
import com.javaproject.nimrod.cinema.WebInterfaces.MoviesServiceFactory;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Nimrod on 17/08/2017.
 */

public class AddMovieFragment extends Fragment implements Validator.ValidationListener
{
    // Add fields
    @BindView(R.id.til_release_date)
    TextInputLayout _releaseDate;
    @BindView(R.id.til_movie_name) @NotEmpty(messageResId = R.string.empty_field_error)
    TextInputLayout _movieName;
    @BindView(R.id.til_actors) @Pattern(regex = "^([a-zA-Z]+(,[a-zA-Z]+)*)$", messageResId = R.string.actors_field_error)
    TextInputLayout _actors;
    @BindView(R.id.til_director) @NotEmpty(messageResId = R.string.empty_field_error)
    TextInputLayout _director;
    @BindView(R.id.til_duration) @Pattern(regex = "^[1-9][0-9][0-9]|[1-9][0-9]", messageResId = R.string.duration_field_error)
    TextInputLayout _duration;
    @BindView(R.id.til_description) @NotEmpty(messageResId = R.string.empty_field_error)
    TextInputLayout _description;
    @BindView(R.id.til_genres) @Pattern(regex = "^[1-9][0-9][0-9]|[1-9][0-9]", messageResId = R.string.duration_field_error)
    TextInputLayout _genres;

    private Validator _validator;
    private Bitmap _movieImage;


    public static Fragment newInstance()
    {
        return new AddMovieFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_add_movie, container, false);
        ButterKnife.bind(this, v);

        // Setting fields validator
        _validator = new Validator(this);
        _validator.setValidationListener(this);
        _validator.registerAdapter(TextInputLayout.class, new TextInputLayoutDataAdapter());

        return v;
    }

    @OnClick(R.id.iv_choose_movie_picture)
    public void OnChooseMoviePicture()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            StartGallery();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    2000);
        }
    }

    private void StartGallery()
    {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        cameraIntent.setType("image/*");
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //super method removed
        if (resultCode == RESULT_OK)
        {
            if (requestCode == 1000)
            {
                Uri returnUri = data.getData();
                try
                {
                    _movieImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClick(R.id.tiet_release_date)
    public void OnChooseDateClicked()
    {
        Calendar now = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,(view, year, month, dayOfMonth) ->
                _releaseDate.getEditText().setText(String.format("%d/%d/%d", dayOfMonth, month, year)), now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        datePicker.getDatePicker().setMinDate(now.getTimeInMillis());
        datePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        datePicker.show();
    }

    @OnClick(R.id.btn_add_movie)
    public void OnAddMovieClicked()
    {
        _validator.validate();
    }

    @Override
    public void onValidationSucceeded()
    {
        MovieDetails movie = CreateMovieDetails();
        Bitmap movieImage = GetMovieImage();

        MoviesServiceFactory.GetInstance().AddMovie(movie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((responseBody, throwable) -> {

            if (throwable == null)
                Toast.makeText(getContext(), R.string.failed_adding_movie, Toast.LENGTH_LONG).show();
            else
                Snackbar.make(getView(), R.string.operation_success_add_movie, Toast.LENGTH_LONG).show();
                });
    }

    private MovieDetails CreateMovieDetails()
    {
        return new MovieDetails(
                _movieName.getEditText().getText().toString(),
                _description.getEditText().getText().toString(),
                "",
                _director.getEditText().getText().toString(),
                _actors.getEditText().getText().toString(),
                LocalDate.parse(_releaseDate.getEditText().getText().toString()),
                _genres.getEditText().getText().toString(),
                Integer.parseInt(_duration.getEditText().getText().toString()));
    }

    private Bitmap GetMovieImage()
    {
        return null;
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
            else
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }

        Snackbar.make(getView(), R.string.illegal_fields, Toast.LENGTH_LONG).show();
    }

    private void ClearErrorOnAllFields()
    {
        _movieName.setError(null);
        _actors.setError(null);
        _releaseDate.setError(null);
        _duration.setError(null);
        _director.setError(null);
    }
}

