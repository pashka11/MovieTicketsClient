package com.javaproject.pashnim.cinema.DisplayObjects;

import android.graphics.Bitmap;
import android.graphics.Movie;

import com.javaproject.pashnim.cinema.Objects.MovieDetails;

/**
 * Created by Nimrod on 15/06/2017.
 */

public class MovieView
{
    public Bitmap MoviePicture;
    public MovieDetails MovieDetails;

    public MovieView(MovieDetails details, Bitmap moviePicture)
    {
        MovieDetails = details;
        MoviePicture = moviePicture;
    }

}
