package com.javaproject.pashnim.cinema;

import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.javaproject.pashnim.cinema.DisplayObjects.MovieDisplay;
import com.javaproject.pashnim.cinema.Objects.MovieDetails;

public class MovieDescriptionActivity extends AppCompatActivity {

    MovieDetails m_displayedMovie;

    ImageView m_movieImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_decription);
        final EditText txtDate = (EditText) findViewById(R.id.txtdate);

        // TODO: do all the findViewByIds here for all the components of this page;

        m_displayedMovie = (MovieDetails) getIntent().getSerializableExtra("movie");
        Bitmap image = getIntent().getParcelableExtra("pic");
        // TODO : save the above bitmap in a member (m_movieImage)

        PopulateFields();
        LoadScreenings();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    DateDialogFragment dialog = new DateDialogFragment(v);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                }
            });
    }

    private void LoadScreenings()
    {
        // TODO: Use RxJava here to request all the screenings for the movie and then set the dateDialog like i did in the main activity
    }

    private void PopulateFields()
    {
        // TODO : Set the the values from m_currentMovie here;
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
