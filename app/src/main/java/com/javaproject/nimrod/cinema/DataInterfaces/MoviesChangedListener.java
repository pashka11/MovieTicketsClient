package com.javaproject.nimrod.cinema.DataInterfaces;

import com.javaproject.nimrod.cinema.Objects.MovieDetails;

import java.util.List;

/**
 * Created by Nimrod on 20/08/2017.
 */

public interface MoviesChangedListener
{
    void MoviesChanged(List<MovieDetails> movies);
}
