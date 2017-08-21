package com.javaproject.nimrod.cinema;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.javaproject.nimrod.cinema.DataInterfaces.MoviesChangedListener;
import com.javaproject.nimrod.cinema.Objects.MovieDetails;
import com.javaproject.nimrod.cinema.Validation.TextInputLayoutDataAdapter;
import com.javaproject.nimrod.cinema.WebInterfaces.MoviesServiceFactory;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Nimrod on 17/08/2017.
 */

public class ManageMovieFragment extends Fragment implements Validator.ValidationListener
{
    private List<MovieDetails> _moviesList;
    private static MoviesChangedListener _moviesChangedListener;

    // Expandable Views
    @BindView(R.id.expl_add_movie)
    ExpandableLayout _addMovieLayout;
    @BindView(R.id.expl_delete_movie)
    ExpandableLayout _deleteMovieLayout;

    // Layout fields
    @BindView(R.id.til_release_date) @NotEmpty(messageResId = R.string.empty_field_error)
    TextInputLayout _releaseDate;
    @BindView(R.id.til_movie_name) @NotEmpty(messageResId = R.string.empty_field_error)
    TextInputLayout _movieName;
    @BindView(R.id.til_actors) @Pattern(regex = "^([a-zA-Z\\s]+(,[a-zA-Z\\s]+)*)$", messageResId = R.string.actors_field_error)
    TextInputLayout _actors;
    @BindView(R.id.til_director) @NotEmpty(messageResId = R.string.empty_field_error)
    TextInputLayout _director;
    @BindView(R.id.til_duration) @Pattern(regex = "^[1-9][0-9][0-9]|[1-9][0-9]$", messageResId = R.string.duration_field_error)
    TextInputLayout _duration;
    @BindView(R.id.til_description) @NotEmpty(messageResId = R.string.empty_field_error)
    TextInputLayout _description;
    @BindView(R.id.til_genres) @Pattern(regex = "^([a-zA-Z\\s]+(,[a-zA-Z\\s]+)*)$", messageResId = R.string.genres_field_error)
    TextInputLayout _genres;
    @BindView(R.id.tv_imageName)
    @NotEmpty (message = "Image missing")
    TextView _imageName;
    @BindView(R.id.sp_movies_for_add)
    Spinner _moviesSpinner;

    private Validator _validator;
    private Bitmap _movieImage;
    private byte[] _selectedImageData;
    private boolean _wasImageUploaded;
    private ArrayAdapter<MovieDetails> _moviesSpinnerAdapter;


    public static Fragment newInstance(MoviesChangedListener moviesChangedListener)
    {
        _moviesChangedListener = moviesChangedListener;

        return new ManageMovieFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_management_movie, container, false);
        ButterKnife.bind(this, v);

        // Setting fields validator
        _validator = new Validator(this);
        _validator.setValidationListener(this);
        _validator.registerAdapter(TextInputLayout.class, new TextInputLayoutDataAdapter());

        LoadMovies();

        return v;
    }

    public void LoadMovies()
    {
        MoviesServiceFactory.GetInstance().GetAllMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((movieDetails, throwable) -> {
                    // If everything is ok
                    if (throwable == null)
                    {
                        _moviesList = movieDetails;
                        _moviesSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, movieDetails);
                        _moviesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        _moviesSpinner.setAdapter(_moviesSpinnerAdapter);
                    }
                });
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
                    _imageName.setText(GetImageNameFromUri(getActivity().getContentResolver(), returnUri));

                    InputStream stream = getActivity().getContentResolver().openInputStream(returnUri);

                    int size = stream.available();
                    byte[] bytes = new byte[size];

                    if (stream.read(bytes) == 0)
                        Log.d("Upload Image", "0 bytes were read!");

                    _selectedImageData = bytes;

                    // Indicating that the image was not uploaded yet
                    _wasImageUploaded = false;

                    // Upload the movie image
                    UploadMovieImage(bytes);
                }
                catch (IOException e)
                {
                    Snackbar.make(getView(), "Failed loading image", Snackbar.LENGTH_LONG)
                            .setAction(R.string.retry, v -> OnChooseMoviePicture()).show();

                    e.printStackTrace();
                }
            }
        }
    }

    private String GetImageNameFromUri(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    @OnFocusChange(R.id.tiet_release_date)
    public void OnChooseDateClicked(View v, boolean focusGained)
    {
        // Skip if were leaving the date field
        if (!focusGained)
            return;

        // Prevent keyboard from appearing since we show a date dialog
        ((EditText)v).setShowSoftInputOnFocus(false);

        Calendar now = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,(view, year, month, dayOfMonth) ->
                _releaseDate.getEditText().setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)), now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        datePicker.getDatePicker().setMinDate(now.getTimeInMillis());
        datePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        datePicker.show();
    }

    private void DeleteSelectedMovie()
    {
        MoviesServiceFactory.GetInstance().DeleteMovie(((MovieDetails)_moviesSpinner.getSelectedItem()).Id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((responseBody, throwable) ->
                {
                    if (throwable != null)
                    {
                        Snackbar snack = Snackbar.make(getView(), R.string.failed_deleting_movie, Snackbar.LENGTH_LONG);
                        snack.setAction(R.string.retry, v -> OnDeleteMovieClicked());
                        snack.show();
                    }
                    else
                    {
                        MovieDetails selectedItem = ((MovieDetails)_moviesSpinner.getSelectedItem());

                        Snackbar.make(getView(),
                                String.format(getString(R.string.operation_success_delete_movie), selectedItem.Name),
                                Snackbar.LENGTH_LONG).show();
                        ClearAll();

                        _moviesSpinnerAdapter.remove(selectedItem);
                        _moviesSpinnerAdapter.notifyDataSetChanged();
                        _moviesList.remove(selectedItem);
                        _moviesChangedListener.MoviesChanged(_moviesList);
                    }
                });
    }

    @OnClick(R.id.btn_delete_movie)
    public void OnDeleteMovieClicked()
    {
        if (_moviesSpinnerAdapter.getCount() == 0)
        {
            Snackbar.make(getView(), "No movies to delete", Snackbar.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Delete");
        alert.setMessage(R.string.movie_delete_confirmation);
        alert.setPositiveButton("Yes", (dialog, which) ->
        {
            DeleteSelectedMovie();
            dialog.dismiss();
        });

        alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        alert.show();
    }

    @OnClick(R.id.btn_add_movie)
    public void OnAddMovieClicked()
    {
        if (_wasImageUploaded)
            _validator.validate();
        else
            Snackbar.make(getView(), "Image did not upload yet", Snackbar.LENGTH_LONG).show();
    }

    /**
     * When validation on adding a movie fields passed
     */
    @Override
    public void onValidationSucceeded()
    {
        ClearErrorOnAllFields();

        AddMovie();
    }

    private void AddMovie()
    {
        MovieDetails movie = CreateMovieDetails();

        MoviesServiceFactory.GetInstance().AddMovie(movie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((movieId, throwable) -> {

                    if (throwable != null)
                    {
                        Snackbar addMovieSnack = Snackbar.make(getView(), R.string.failed_adding_movie, Snackbar.LENGTH_LONG);
                        addMovieSnack.setAction(R.string.retry, v -> OnAddMovieClicked());
                        addMovieSnack.show();
                    }
                    else
                    {
                        Snackbar.make(getView(), R.string.operation_success_add_movie, Snackbar.LENGTH_LONG).show();
                        ClearAll();

                        // Add the new movie to all the lists and notify everyone for changes
                        movie.Id = movieId;

                        // Adding the movie to the list, THIS IS THE SPINNERS DATA so we dont have to add to adapter too.
                        _moviesList.add(movie);

                        // Notify other fragments and ourselves
                        _moviesSpinnerAdapter.notifyDataSetChanged();
                        _moviesChangedListener.MoviesChanged(_moviesList);
                    }
                });
    }

    private void UploadMovieImage(byte[] imageData)
    {
        if (imageData.length == 0)
        {
            Log.d("Management", "Cannot upload empty image");
            return;
        }

        // Constructing a multipart for uploading a photo
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageData);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", _imageName.getText().toString().toLowerCase(), reqFile);

        // Uploading the image async
        MoviesServiceFactory.GetInstance().UploadImage(body).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe((responseBody1, throwable1) ->
                {
                    if (throwable1 != null)
                    {
                        Log.d("Management", "Failed uploading image");

                        Snackbar imageUploadSnack = Snackbar.make(getView(), "Failed uploading image", Snackbar.LENGTH_LONG);
                        imageUploadSnack.setAction(R.string.retry, v ->
                                Single.create(e -> OnChooseMoviePicture()).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(o ->
                                                UploadMovieImage(_selectedImageData)));
                    }
                    else
                    {
                        Snackbar.make(getView(), "Image uploaded", Snackbar.LENGTH_LONG).show();
                        _wasImageUploaded = true;
                    }
                });
    }

    private MovieDetails CreateMovieDetails()
    {
        return new MovieDetails(
                _movieName.getEditText().getText().toString(),
                _description.getEditText().getText().toString(),
                _imageName.getText().toString(),
                _director.getEditText().getText().toString(),
                _actors.getEditText().getText().toString(),
                LocalDate.parse(_releaseDate.getEditText().getText().toString(),
                        DateTimeFormat.forPattern(getString(R.string.release_date_format))),
                _genres.getEditText().getText().toString(),
                Integer.parseInt(_duration.getEditText().getText().toString()));
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

    private void ClearErrorOnAllFields()
    {
        _movieName.setError(null);
        _actors.setError(null);
        _releaseDate.setError(null);
        _duration.setError(null);
        _director.setError(null);
        _genres.setError(null);

        _movieName.setErrorEnabled(false);
        _actors.setErrorEnabled(false);
        _releaseDate.setErrorEnabled(false);
        _duration.setErrorEnabled(false);
        _director.setErrorEnabled(false);
        _genres.setErrorEnabled(false);
    }

    private void ClearAll()
    {
        // Clear fields
        _movieName.getEditText().setText("");
        _actors.getEditText().setText("");
        _releaseDate.getEditText().setText("");
        _duration.getEditText().setText("");
        _director.getEditText().setText("");
        _wasImageUploaded = false;
        _imageName.setText("");
        _selectedImageData = null;
        _genres.getEditText().setText("");
        _description.getEditText().setText("");

        // Lose focus from everything
        getActivity().getCurrentFocus().clearFocus();
    }

    @OnClick(R.id.btn_toggle)
    public void OnToggleButtonClicked()
    {
        _addMovieLayout.toggle();
        _deleteMovieLayout.toggle();
    }
}


