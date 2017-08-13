package com.javaproject.nimrod.cinema.Objects;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Nimrod on 15/06/2017.
 */

public class MovieDisplay implements Serializable
{
    public Bitmap MoviePicture;
    public MovieDetails MovieDetails;

    public MovieDisplay(MovieDetails details, Bitmap moviePicture)
    {
        MovieDetails = details;
        MoviePicture = moviePicture;
    }

}
