package com.javaproject.nimrod.cinema.Objects;

import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * Created by Nimrod on 15/06/2017.
 */

public class MovieDetails implements Serializable
{
    public MovieDetails()
    {

    }

    public MovieDetails(String id, String name, String description, String imageName, String director, String actors, LocalDate releaseDate, String genres, int duration)
    {
        Id = id;
        Name = name;
        Description = description;
        ImageName = imageName;
        Director = director;
        Actors = actors;
        ReleaseDate = releaseDate;
        Genres = genres;
        Duration = duration;
    }

    public MovieDetails(String name, String description, String imageName, String director, String actors, LocalDate releaseDate, String genres, int duration)
    {
        this ("", name, description, imageName, director, actors, releaseDate, genres, duration);
    }

    public String Id;
    public String Name;
    public String Description;
    public String ImageName;
    public LocalDate ReleaseDate;
    public String Director;
    public int Duration;
    public String Genres;
    public String Actors;

    @Override
    public String toString()
    {
        return Name;
    }
}